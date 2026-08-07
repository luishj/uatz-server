package br.com.uatz.server.repository;

import br.com.uatz.model.ProductAlias;
import br.com.uatz.server.vo.ProductAliasCandidateVO;
import java.util.List;
import java.util.Optional;

public interface ProductAliasRepository extends GenericRepository<ProductAlias, Long> {

    /**
     * Acerto exato: o apelido normalizado já foi visto antes e resolve para um
     * produto. É o caminho barato, que pega o que o operador já confirmou.
     */
    Optional<Long> findProductIdByAlias(long scopeId, String alias);

    /**
     * Busca fuzzy por trigramas: candidatos rankeados por similaridade quando o
     * acerto exato falha. Usada para sugerir produtos na triagem.
     */
    List<ProductAliasCandidateVO> buscarCandidatos(long scopeId, String alias, int limite);

    /**
     * Grava o apelido se ele ainda não existir no escopo. É o que faz a base
     * crescer: o nome do produto entra como SEED e cada confirmação do operador
     * como MANUAL. Retorna {@code true} quando inseriu.
     */
    boolean registrarApelido(long scopeId, Long productId, String alias, String source);
}
