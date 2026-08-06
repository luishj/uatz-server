package br.com.uatz.server.util;

import br.com.uatz.server.dto.budget.BudgetItemRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Transforma o texto livre que o cliente manda no WhatsApp em itens do pedido.
 * Cada linha (ou trecho separado por {@code ;} ou {@code ,}) vira um item:
 * o número inicial é a quantidade e o resto é a descrição, da qual a unidade é
 * inferida por palavras-chave ("saco", "metro", "caixa"). Sem número, cai em
 * {@code 1 un}; mensagem vazia vira um item único com o texto cru.
 *
 * <p>Ficou fora do serviço, numa classe estática pura, porque é a regra mais
 * fácil de errar em silêncio e a que mais compensa cobrir com teste.</p>
 */
public final class WhatsAppItemParser {

    private static final String UNIDADE_PADRAO = "un";
    private static final BigDecimal QUANTIDADE_PADRAO = BigDecimal.ONE;
    private static final String PADRAO_QUANTIDADE = "\\d+[\\.,]?\\d*";

    private WhatsAppItemParser() {
    }

    public static List<BudgetItemRequest> parse(String rawMessage) {
        List<String> rawItems = splitRawItems(rawMessage);
        List<BudgetItemRequest> items = new ArrayList<>();

        for (String rawItem : rawItems) {
            String cleaned = rawItem.trim();
            if (cleaned.isBlank()) {
                continue;
            }

            BigDecimal quantity = QUANTIDADE_PADRAO;
            String description = cleaned;

            String[] tokens = cleaned.split("\\s+", 2);
            if (tokens.length > 1 && tokens[0].matches(PADRAO_QUANTIDADE)) {
                quantity = new BigDecimal(tokens[0].replace(",", "."));
                description = tokens[1].trim();
            }

            items.add(new BudgetItemRequest(null, description, quantity, resolveUnit(description)));
        }

        if (items.isEmpty()) {
            items.add(new BudgetItemRequest(null, rawMessage.trim(), QUANTIDADE_PADRAO, UNIDADE_PADRAO));
        }

        return items;
    }

    private static String resolveUnit(String description) {
        String normalized = description.toLowerCase();

        if (normalized.contains("saco")) {
            return "saco";
        }
        if (normalized.contains("metro")) {
            return "m";
        }
        if (normalized.contains("caixa")) {
            return "caixa";
        }
        return UNIDADE_PADRAO;
    }

    private static List<String> splitRawItems(String rawMessage) {
        if (rawMessage.contains("\n")) {
            return rawMessage.lines().toList();
        }
        if (rawMessage.contains(";")) {
            return List.of(rawMessage.split(";"));
        }
        if (rawMessage.contains(",")) {
            return List.of(rawMessage.split(","));
        }
        return List.of(rawMessage);
    }
}
