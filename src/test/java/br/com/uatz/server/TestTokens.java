package br.com.uatz.server;

import br.com.uatz.server.constante.Perfil;
import io.smallrye.jwt.build.Jwt;
import java.time.Duration;
import java.util.Set;

/**
 * Assina tokens JWT para os testes com a mesma chave privada da aplicação
 * ({@code smallrye.jwt.sign.key.location}). O {@code upn} vira o
 * {@code jsonWebToken.getName()} usado para resolver o fornecedor, e os
 * {@code groups} são os perfis conferidos por {@code @RolesAllowed}.
 */
public final class TestTokens {

    private static final String EMISSOR = "uatz";

    private TestTokens() {
    }

    public static String vendor(String email) {
        return comPerfil(email, Perfil.VENDOR);
    }

    public static String operator(String email) {
        return comPerfil(email, Perfil.OPERATOR);
    }

    public static String admin(String email) {
        return comPerfil(email, Perfil.ADMIN);
    }

    public static String comPerfil(String email, String perfil) {
        return Jwt.issuer(EMISSOR)
                .upn(email)
                .subject(email)
                .groups(Set.of(perfil))
                .expiresIn(Duration.ofMinutes(10))
                .sign();
    }
}
