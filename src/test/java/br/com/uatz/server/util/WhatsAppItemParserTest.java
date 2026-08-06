package br.com.uatz.server.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.uatz.server.dto.budget.BudgetItemRequest;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Cobre a tradução do texto do WhatsApp em itens do pedido — a regra que decide
 * quantidade, descrição e unidade de cada linha. É teste puro, sem HTTP nem
 * banco.
 */
class WhatsAppItemParserTest {

    @Test
    void quebraLinhasEmItensComQuantidadeEUnidade() {
        List<BudgetItemRequest> items = WhatsAppItemParser.parse("10 sacos de cimento\n100 tijolos");

        assertEquals(2, items.size());

        BudgetItemRequest primeiro = items.get(0);
        assertEquals(0, new BigDecimal("10").compareTo(primeiro.quantity()));
        assertEquals("sacos de cimento", primeiro.productName());
        assertEquals("saco", primeiro.unit());

        BudgetItemRequest segundo = items.get(1);
        assertEquals(0, new BigDecimal("100").compareTo(segundo.quantity()));
        assertEquals("tijolos", segundo.productName());
        assertEquals("un", segundo.unit());
    }

    /**
     * A vírgula decimal na quantidade só é lida como decimal quando a mensagem
     * já tem outro separador de itens (quebra de linha ou {@code ;}) — que é o
     * formato real do WhatsApp, um item por linha. Numa linha isolada a vírgula
     * é tratada como separador de itens (ver {@link
     * #virgulaSozinhaEhSeparadorDeItensNaoDecimal()}).
     */
    @Test
    void aceitaVirgulaComoSeparadorDecimalDaQuantidade() {
        List<BudgetItemRequest> items = WhatsAppItemParser.parse("1,5 metros de fio\n2 sacos de cimento");

        assertEquals(2, items.size());
        BudgetItemRequest item = items.get(0);
        assertEquals(0, new BigDecimal("1.5").compareTo(item.quantity()));
        assertEquals("metros de fio", item.productName());
        assertEquals("m", item.unit());
    }

    /**
     * Limitação conhecida: sem quebra de linha, a vírgula é separador de itens,
     * então "1,5 metros" vira dois itens em vez de uma quantidade decimal. Fica
     * registrado aqui para que a mudança seja consciente se um dia for corrigida.
     */
    @Test
    void virgulaSozinhaEhSeparadorDeItensNaoDecimal() {
        List<BudgetItemRequest> items = WhatsAppItemParser.parse("1,5 metros de fio");

        assertEquals(2, items.size());
    }

    @Test
    void mensagemSemQuantidadeCaiEmUmaUnidade() {
        List<BudgetItemRequest> items = WhatsAppItemParser.parse("cimento");

        assertEquals(1, items.size());
        BudgetItemRequest item = items.get(0);
        assertEquals(0, BigDecimal.ONE.compareTo(item.quantity()));
        assertEquals("cimento", item.productName());
        assertEquals("un", item.unit());
    }

    @Test
    void mensagemVaziaViraItemUnicoComTextoCru() {
        List<BudgetItemRequest> items = WhatsAppItemParser.parse("   ");

        assertEquals(1, items.size());
        BudgetItemRequest item = items.get(0);
        assertEquals(0, BigDecimal.ONE.compareTo(item.quantity()));
        assertEquals("un", item.unit());
        assertTrue(item.productName().isBlank());
    }

    @Test
    void separaItensPorVirgulaQuandoNaoHaQuebraDeLinha() {
        List<BudgetItemRequest> items = WhatsAppItemParser.parse("2 caixas de prego, 3 metros de cano");

        assertEquals(2, items.size());
        assertEquals("caixa", items.get(0).unit());
        assertEquals("m", items.get(1).unit());
    }
}
