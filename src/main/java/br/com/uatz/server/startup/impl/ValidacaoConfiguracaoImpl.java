package br.com.uatz.server.startup.impl;

import br.com.uatz.server.env.Enviroment;
import br.com.uatz.server.util.StringUtil;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.time.Duration;
import org.jboss.logging.Logger;

/**
 * Confere na subida o que, se estiver errado, só apareceria em produção no pior
 * momento possível: a assinatura do JWT falharia no primeiro login e o webhook
 * aceitaria payload de qualquer origem.
 *
 * <p>A aplicação não sobe com configuração de segurança quebrada — errar aqui é
 * barato, errar depois do deploy não é.</p>
 */
@ApplicationScoped
public class ValidacaoConfiguracaoImpl {

    private static final Logger logger = Logger.getLogger(ValidacaoConfiguracaoImpl.class);

    private static final String EMISSOR = "uatz";
    private static final String ASSUNTO_VERIFICACAO = "startup-check";

    private final Enviroment enviroment;
    private final JWTParser jwtParser;

    @Inject
    public ValidacaoConfiguracaoImpl(Enviroment enviroment, JWTParser jwtParser) {
        this.enviroment = enviroment;
        this.jwtParser = jwtParser;
    }

    void onStart(@Observes StartupEvent evento) {
        validarParDeChaves();
        validarWebhookWhatsApp();
    }

    /**
     * Assina e relê um token descartável. Cobre de uma vez os três jeitos de
     * quebrar a autenticação: chave privada ilegível, chave pública ilegível e
     * par que não combina (regerar só um dos lados).
     */
    private void validarParDeChaves() {
        try {
            String token = Jwt.issuer(EMISSOR)
                    .upn(ASSUNTO_VERIFICACAO)
                    .subject(ASSUNTO_VERIFICACAO)
                    .expiresIn(Duration.ofSeconds(60))
                    .sign();

            jwtParser.parse(token);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Par de chaves do JWT inválido: confira JWT_PRIVATE_KEY_LOCATION e "
                            + "JWT_PUBLIC_KEY_LOCATION. Em desenvolvimento, gere o par com o comando "
                            + "documentado no README.", e);
        }

        logger.info("=========== JWT: par de chaves validado ===========");
    }

    /**
     * Sem app secret a conferência do HMAC é pulada e o webhook, que é
     * {@code @PermitAll}, aceitaria pedido forjado de qualquer telefone. Isso só
     * pode valer com o envio desligado, ou seja, em desenvolvimento.
     */
    private void validarWebhookWhatsApp() {
        if (!enviroment.isWhatsAppHabilitado()) {
            logger.warn("WhatsApp desabilitado: mensagens apenas no log e assinatura do webhook não conferida");
            return;
        }

        if (StringUtil.isNullOrEmpty(enviroment.getWhatsAppSegredoApp())) {
            throw new IllegalStateException(
                    "WHATSAPP_APP_SECRET é obrigatório quando WHATSAPP_ENABLED=true: sem ele a "
                            + "assinatura do webhook não é conferida e qualquer um poderia criar pedidos.");
        }

        if (StringUtil.isNullOrEmpty(enviroment.getWhatsAppTokenVerificacao())) {
            throw new IllegalStateException(
                    "WHATSAPP_VERIFY_TOKEN é obrigatório quando WHATSAPP_ENABLED=true: a Meta não "
                            + "consegue concluir a verificação do webhook sem ele.");
        }

        if (StringUtil.isNullOrEmpty(enviroment.getWhatsAppTokenAcesso())
                || StringUtil.isNullOrEmpty(enviroment.getWhatsAppPhoneNumberId())) {
            throw new IllegalStateException(
                    "WHATSAPP_ACCESS_TOKEN e WHATSAPP_PHONE_NUMBER_ID são obrigatórios quando "
                            + "WHATSAPP_ENABLED=true: sem eles nenhuma mensagem chega ao cliente.");
        }

        logger.info("=========== WHATSAPP: configuração da Cloud API validada ===========");
    }

}
