package br.com.uatz.server.service.impl;

import br.com.uatz.server.repository.ProductAliasRepository;
import br.com.uatz.server.service.ProductMatchingService;
import br.com.uatz.server.util.NormalizacaoProduto;
import br.com.uatz.server.util.StringUtil;
import br.com.uatz.server.vo.ProductAliasCandidateVO;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductMatchingServiceImpl implements ProductMatchingService {

    private static final String SOURCE_SEED = "SEED";
    private static final String SOURCE_MANUAL = "MANUAL";

    private final ProductAliasRepository productAliasRepository;

    public ProductMatchingServiceImpl(ProductAliasRepository productAliasRepository) {
        this.productAliasRepository = productAliasRepository;
    }

    @Override
    public Optional<Long> resolverProdutoExato(long scopeId, String texto) {
        String alias = NormalizacaoProduto.normalizar(texto);
        if (StringUtil.isNullOrEmpty(alias)) {
            return Optional.empty();
        }
        return productAliasRepository.findProductIdByAlias(scopeId, alias);
    }

    @Override
    public List<ProductAliasCandidateVO> sugerir(long scopeId, String texto, int limite) {
        String alias = NormalizacaoProduto.normalizar(texto);
        if (StringUtil.isNullOrEmpty(alias)) {
            return List.of();
        }
        return productAliasRepository.buscarCandidatos(scopeId, alias, limite);
    }

    @Override
    public void confirmar(long scopeId, Long productId, String texto) {
        gravarApelido(scopeId, productId, texto, SOURCE_MANUAL);
    }

    @Override
    public void semear(long scopeId, Long productId, String nomeProduto) {
        gravarApelido(scopeId, productId, nomeProduto, SOURCE_SEED);
    }

    private void gravarApelido(long scopeId, Long productId, String texto, String source) {
        if (productId == null) {
            return;
        }
        String alias = NormalizacaoProduto.normalizar(texto);
        if (StringUtil.isNullOrEmpty(alias)) {
            return;
        }
        productAliasRepository.registrarApelido(scopeId, productId, alias, source);
    }
}
