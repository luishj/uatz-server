package br.com.uatz.server.api;

import static io.restassured.RestAssured.given;

import br.com.uatz.server.repository.BudgetRequestRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Cobre os três lugares onde o webhook precisa se defender: assinatura inválida
 * (pedido forjado), assinatura válida (fluxo normal) e reenvio da Meta — o item
 * 4, em que o mesmo payload não pode virar dois pedidos.
 *
 * <p>Precisa do {@code whatsapp.app-secret} do perfil de teste; a assinatura é
 * recalculada aqui com o mesmo segredo. Cada teste usa telefone e id de mensagem
 * únicos para não colidir com dados de execuções anteriores no banco de teste.</p>
 */
@QuarkusTest
class WhatsAppWebhookTest {

    private static final String APP_SECRET = "test-app-secret";
    private static final String ENDPOINT = "/api/whatsapp/webhook";

    @Inject
    BudgetRequestRepository budgetRequestRepository;

    @Test
    void assinaturaInvalidaEhRecusada() {
        String payload = montarPayload(novoId(), novoTelefone(), "10 sacos de cimento");

        given()
                .contentType("application/json")
                .header("X-Hub-Signature-256", "sha256=deadbeef")
                .body(payload)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(403);
    }

    @Test
    void assinaturaValidaCriaPedido() {
        String phone = novoTelefone();
        String payload = montarPayload(novoId(), phone, "10 sacos de cimento");

        given()
                .contentType("application/json")
                .header("X-Hub-Signature-256", assinar(payload))
                .body(payload)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200);

        Assertions.assertEquals(1, contarPedidosDoTelefone(phone));
    }

    @Test
    void mesmoPayloadDuasVezesCriaUmPedidoSo() {
        String phone = novoTelefone();
        String payload = montarPayload(novoId(), phone, "10 sacos de cimento");
        String assinatura = assinar(payload);

        postarComSucesso(payload, assinatura);
        postarComSucesso(payload, assinatura);

        Assertions.assertEquals(1, contarPedidosDoTelefone(phone),
                "O reenvio do mesmo payload não pode criar um segundo pedido");
    }

    private void postarComSucesso(String payload, String assinatura) {
        given()
                .contentType("application/json")
                .header("X-Hub-Signature-256", assinatura)
                .body(payload)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200);
    }

    private long contarPedidosDoTelefone(String phone) {
        return budgetRequestRepository.count("client.phone = ?1", phone);
    }

    private static String montarPayload(String messageId, String phone, String texto) {
        return "{"
                + "\"object\":\"whatsapp_business_account\","
                + "\"entry\":[{\"id\":\"ENTRY\",\"changes\":[{\"field\":\"messages\",\"value\":{"
                + "\"messaging_product\":\"whatsapp\","
                + "\"messages\":[{"
                + "\"id\":\"" + messageId + "\","
                + "\"from\":\"" + phone + "\","
                + "\"timestamp\":\"1700000000\","
                + "\"type\":\"text\","
                + "\"text\":{\"body\":\"" + texto + "\"}"
                + "}]}}]}]}";
    }

    private static String assinar(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(APP_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return "sha256=" + HexFormat.of().formatHex(hmac);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao assinar o payload de teste", e);
        }
    }

    private static String novoTelefone() {
        return "5511" + (System.nanoTime() % 1_000_000_000L);
    }

    private static String novoId() {
        return "wamid.test." + System.nanoTime();
    }
}
