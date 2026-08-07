package br.com.uatz.server.repository.impl;

import br.com.uatz.model.ProductAlias;
import br.com.uatz.server.repository.ProductAliasRepository;
import br.com.uatz.server.util.QueryUtil;
import br.com.uatz.server.vo.ProductAliasCandidateVO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ProductAliasRepositoryImpl extends GenericRepositoryImpl<ProductAlias, Long> implements ProductAliasRepository {

    @Override
    public Optional<Long> findProductIdByAlias(long scopeId, String alias) {
        String sql = "SELECT product_id FROM product_aliases WHERE scope_id = :scope AND alias = :alias";

        Query query = QueryUtil.createNativeQuery(getEntityManager(), sql,
                Map.of("scope", scopeId, "alias", alias));

        return Optional.ofNullable(QueryUtil.getSingleField(query, Long.class));
    }

    /**
     * O operador {@code %} usa o índice GIN e filtra pelo
     * {@code pg_trgm.similarity_threshold} (padrão 0.3). {@code MAX} + {@code GROUP BY}
     * porque um produto pode ter vários apelidos e queremos o melhor por produto.
     * Os aliases {@code AS productId/name/score} têm que casar com os campos do VO.
     */
    @Override
    public List<ProductAliasCandidateVO> buscarCandidatos(long scopeId, String alias, int limite) {
        String sql = "SELECT pa.product_id AS productId, p.name AS name, "
                + "MAX(similarity(pa.alias, :alias))::double precision AS score "
                + "FROM product_aliases pa "
                + "JOIN products p ON p.id = pa.product_id "
                + "WHERE pa.scope_id = :scope AND pa.alias % :alias "
                + "GROUP BY pa.product_id, p.name "
                + "ORDER BY score DESC "
                + "LIMIT :limite";

        Query query = QueryUtil.createNativeQuery(getEntityManager(), sql,
                Map.of("scope", scopeId, "alias", alias, "limite", limite));

        return QueryUtil.getResultList(query, ProductAliasCandidateVO.class);
    }

    /**
     * {@code ON CONFLICT DO NOTHING} sobre a unique (scope_id, alias) resolve a
     * corrida sem verificar antes: se o apelido já existe no escopo, o insert é
     * ignorado. {@code executeUpdate} devolve 0 nesse caso.
     */
    @Override
    @Transactional
    public boolean registrarApelido(long scopeId, Long productId, String alias, String source) {
        String sql = "INSERT INTO product_aliases (scope_id, product_id, alias, source, created_at) "
                + "VALUES (:scope, :productId, :alias, :source, now()) "
                + "ON CONFLICT (scope_id, alias) DO NOTHING";

        int inseridos = getEntityManager().createNativeQuery(sql)
                .setParameter("scope", scopeId)
                .setParameter("productId", productId)
                .setParameter("alias", alias)
                .setParameter("source", source)
                .executeUpdate();

        return inseridos > 0;
    }
}
