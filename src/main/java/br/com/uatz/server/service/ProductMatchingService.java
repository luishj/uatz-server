package br.com.uatz.server.service;

import br.com.uatz.server.vo.ProductAliasCandidateVO;
import java.util.List;
import java.util.Optional;

/**
 * Casa o texto livre de um item (do WhatsApp ou da triagem) com um produto do
 * catálogo, usando os apelidos normalizados. Separa o acerto exato — confiável,
 * usado para vincular sozinho — da sugestão fuzzy, que só propõe candidatos para
 * o operador confirmar.
 */
public interface ProductMatchingService {

    /**
     * Acerto exato: o texto normalizado já é um apelido conhecido no escopo.
     * É o único caminho seguro para vincular automaticamente, sem confirmação.
     */
    Optional<Long> resolverProdutoExato(long scopeId, String texto);

    /**
     * Candidatos rankeados por similaridade, para a triagem sugerir quando não
     * há acerto exato. Não vincula nada sozinho.
     */
    List<ProductAliasCandidateVO> sugerir(long scopeId, String texto, int limite);

    /**
     * Registra a confirmação do operador como apelido MANUAL — é o que faz a
     * base de casamento crescer com o uso.
     */
    void confirmar(long scopeId, Long productId, String texto);

    /**
     * Registra o nome do produto como apelido SEED. Chamado ao criar o produto
     * e no backfill do catálogo existente.
     */
    void semear(long scopeId, Long productId, String nomeProduto);
}
