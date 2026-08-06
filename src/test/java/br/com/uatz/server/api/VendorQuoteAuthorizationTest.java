package br.com.uatz.server.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import br.com.uatz.server.TestTokens;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

/**
 * Trava o escopo das cotações — o furo do item 1, onde um fornecedor
 * autenticado enxergava as visões comparativas (com preço do concorrente).
 *
 * <p>Estas asserções cobrem o gate de perfil, que o {@code @RolesAllowed} aplica
 * antes de qualquer acesso a banco, então não dependem de dados semeados: se
 * alguém reabrir {@code /request/{id}} ou {@code /summary} para VENDOR, o teste
 * quebra na hora. As linhas do quadro que exigem fornecedor cadastrado (a
 * cotação própria em {@code /me}, o POST em nome de outro fornecedor) estão
 * descritas no handoff e dependem de banco semeado para rodar.</p>
 */
@QuarkusTest
class VendorQuoteAuthorizationTest {

    private static final String VENDOR_A = "fornecedor-a@uatz.com.br";
    private static final String OPERADOR = "operador@uatz.com.br";
    private static final long QUALQUER_PEDIDO = 999_999L;

    @Test
    void fornecedorNaoVeAsCotacoesDoPedido() {
        given()
                .header("Authorization", "Bearer " + TestTokens.vendor(VENDOR_A))
                .when()
                .get("/api/vendor-quotes/request/{id}", QUALQUER_PEDIDO)
                .then()
                .statusCode(403);
    }

    @Test
    void fornecedorNaoVeOResumoComparativo() {
        given()
                .header("Authorization", "Bearer " + TestTokens.vendor(VENDOR_A))
                .when()
                .get("/api/vendor-quotes/request/{id}/summary", QUALQUER_PEDIDO)
                .then()
                .statusCode(403);
    }

    @Test
    void requisicaoSemTokenEhRecusada() {
        given()
                .when()
                .get("/api/vendor-quotes/request/{id}", QUALQUER_PEDIDO)
                .then()
                .statusCode(401);
    }

    /**
     * O operador passa pelo gate de perfil nas visões comparativas — como o
     * pedido não existe, a resposta é 404 (e não 403), confirmando que o
     * endpoint continua acessível ao operador.
     */
    @Test
    void operadorPassaPeloGateDeComparativo() {
        given()
                .header("Authorization", "Bearer " + TestTokens.operator(OPERADOR))
                .when()
                .get("/api/vendor-quotes/request/{id}/summary", QUALQUER_PEDIDO)
                .then()
                .statusCode(404);
    }
}
