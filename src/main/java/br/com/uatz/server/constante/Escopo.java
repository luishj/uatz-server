package br.com.uatz.server.constante;

/**
 * Setor do catálogo, usado para isolar os apelidos de produto. Hoje só existe
 * material de construção; quando entrarem farmácia, autopeça, etc., cada um
 * ganha um id próprio e o {@code scope_id} vira FK de uma tabela de setores.
 */
public final class Escopo {

    public static final long CONSTRUCAO = 1L;

    private Escopo() {
    }
}
