package br.com.uatz.server.vo;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Candidato a produto devolvido pela busca fuzzy dos apelidos, com o
 * {@code score} de similaridade (0 a 1) para ranquear as sugestões da triagem.
 *
 * <p>É classe comum, não record: o {@code QueryUtil} preenche por reflexão via
 * construtor sem-arg, e os nomes dos campos (em minúsculo) têm que bater com os
 * aliases do SQL — {@code productId}, {@code name}, {@code score}.</p>
 */
@RegisterForReflection
public class ProductAliasCandidateVO {

    private Long productId;
    private String name;
    private Double score;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
