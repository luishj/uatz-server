package br.com.uatz.server.service;

import br.com.uatz.server.dto.budget.BudgetRequestQuoteOptionsResponse;
import br.com.uatz.server.dto.budget.BudgetRequestSelectionResponse;

/**
 * Fechamento do pedido: envia ao cliente a lista numerada de orçamentos, registra
 * a opção que ele escolheu, encerra o pedido e libera os dados de contato entre
 * cliente e fornecedor escolhido.
 */
public interface BudgetRequestClosingService {

    /**
     * Numera as cotações do pedido (menor total primeiro), envia a lista ao
     * cliente e grava a mensagem na conversa dele.
     */
    public abstract BudgetRequestQuoteOptionsResponse sendQuoteOptions(Long requestId);

    /**
     * Registra a escolha do cliente pelo número da opção, fecha o pedido e envia
     * a mensagem automática com o link de conversa com o vendedor.
     */
    public abstract BudgetRequestSelectionResponse selectOption(Long requestId, Integer optionNumber);

    /**
     * Resultado do fechamento para admin e operador, com todos os contatos.
     */
    public abstract BudgetRequestSelectionResponse findSelection(Long requestId);

    /**
     * Resultado do fechamento para um fornecedor. Só o escolhido recebe os dados
     * do cliente; para os outros a resposta vem sem a opção e sem contatos.
     */
    public abstract BudgetRequestSelectionResponse findSelectionForVendor(Long requestId, String vendorEmail);

}
