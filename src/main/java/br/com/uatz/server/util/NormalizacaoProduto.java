package br.com.uatz.server.util;

import java.text.Normalizer;
import java.util.Set;

/**
 * Forma canônica do texto de um item para casar com produtos do catálogo.
 * Minúsculas, sem acento, sem pontuação e sem as palavras de embalagem/ligação
 * que não ajudam a identificar o produto ("saco de", "kg", "un").
 *
 * <p>Plural fica de fora de propósito: o {@code pg_trgm} é robusto a ele, e
 * remover o "s" final quebraria palavras como "gesso". A mesma normalização é
 * aplicada ao gravar o apelido e ao buscar, para que os dois lados batam.</p>
 */
public final class NormalizacaoProduto {

    private static final Set<String> STOPWORDS = Set.of(
            "de", "do", "da", "dos", "das", "com", "para", "e",
            "saco", "sacos", "caixa", "caixas", "pacote", "pacotes", "fardo", "fardos",
            "unidade", "unidades", "un", "kg", "g", "grama", "gramas", "peca", "pecas",
            "litro", "litros", "l", "ml", "metro", "metros", "m"
    );

    private NormalizacaoProduto() {
    }

    public static String normalizar(String texto) {
        if (StringUtil.isNullOrEmpty(texto)) {
            return StringUtil.STRING_VAZIA;
        }

        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", StringUtil.STRING_VAZIA);

        String limpo = semAcento.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        StringBuilder resultado = new StringBuilder();
        for (String palavra : limpo.split(" ")) {
            if (palavra.isBlank() || STOPWORDS.contains(palavra)) {
                continue;
            }
            if (resultado.length() > 0) {
                resultado.append(" ");
            }
            resultado.append(palavra);
        }

        return resultado.toString();
    }
}
