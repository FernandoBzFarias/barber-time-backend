package com.barbertime.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.barbertime.dto.ComissaoBarbeiroDTO;
import com.barbertime.dto.EvolucaoMensalDTO;
import com.barbertime.dto.FinanceiroResumoDTO;
import com.barbertime.entity.Agendamento;
import com.barbertime.entity.Barbeiro;
import com.barbertime.repository.AgendamentoRepository;
import com.barbertime.repository.BarbeiroRepository;
import jakarta.transaction.Transactional;

@Service
public class FinanceiroService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private BarbeiroRepository barbeiroRepository;
    @Autowired
    private DespesaService despesaService;

    public FinanceiroResumoDTO calcularFechamentoMensal(Long barbeariaId, int mes, int ano) {
        // 1. Busca todos os barbeiros da unidade
        List<Barbeiro> barbeiros = barbeiroRepository.findByBarbeariaId(barbeariaId);

        double faturamentoTotalUnidade = 0.0;
        double totalComissoesUnidade = 0.0;
        List<ComissaoBarbeiroDTO> detalhes = new ArrayList<>();

        for (Barbeiro b : barbeiros) {
            // 2. Busca agendamentos FINALIZADOS do barbeiro no mês/ano específico
            List<Agendamento> agendamentos = agendamentoRepository.buscarFinalizadosPorPeriodo(b.getId(), mes, ano);

            double faturamentoBarbeiro = agendamentos.stream().mapToDouble(Agendamento::getValor).sum();
            
            // Aplica a comissão individual do barbeiro
            double valorComissao = faturamentoBarbeiro * (b.getPercentualComissao() / 100);

            faturamentoTotalUnidade += faturamentoBarbeiro;
            totalComissoesUnidade += valorComissao;

            detalhes.add(new ComissaoBarbeiroDTO(
                b.getId(), b.getNome(), faturamentoBarbeiro, b.getPercentualComissao(), valorComissao
            ));
        }

        // 3. Lucro Líquido (Faturamento - Comissões)  
        Double totalDespesas = despesaService.calcularTotalMes(barbeariaId, mes, ano);
        double lucroLiquido = faturamentoTotalUnidade - totalComissoesUnidade - totalDespesas;
        
        
        // 4. Busca os dados do gráfico e calcula a variação vs mês anterior
        List<EvolucaoMensalDTO> grafico = buscarEvolucao6Meses(barbeariaId);
        Double variacao = calcularVariacaoPercentual(barbeariaId, mes, ano); // <--- Lógica real aplicada aqui

        return new FinanceiroResumoDTO(
            faturamentoTotalUnidade, 
            totalComissoesUnidade, 
            lucroLiquido, 
            variacao, // Agora dinâmico!
            detalhes,
            grafico
        );
    }
    
    @Transactional
    public void atualizarComissao(Long barbeiroId, Double novoPercentual) {
        Barbeiro b = barbeiroRepository.findById(barbeiroId).orElseThrow();
        b.setPercentualComissao(novoPercentual);
        barbeiroRepository.save(b);
    }
    
    public List<EvolucaoMensalDTO> buscarEvolucao6Meses(Long barbeariaId) {
        List<EvolucaoMensalDTO> evolucao = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        // Itera pelos últimos 6 meses para gerar o gráfico
        for (int i = 5; i >= 0; i--) {
            LocalDate dataConsulta = hoje.minusMonths(i);
            int mes = dataConsulta.getMonthValue();
            int ano = dataConsulta.getYear();
            
            Double faturamento = agendamentoRepository.buscarFaturamentoMensalUnidade(barbeariaId, mes, ano);
            
            evolucao.add(new EvolucaoMensalDTO(
                traduzirMes(dataConsulta.getMonthValue()), // Nome do mês abreviado
                faturamento != null ? faturamento : 0.0
            ));
        }
        return evolucao;
    }
    
    private Double calcularVariacaoPercentual(Long barbeariaId, int mes, int ano) {
        Double atual = agendamentoRepository.buscarFaturamentoMensalUnidade(barbeariaId, mes, ano);
        atual = (atual != null) ? atual : 0.0;

        int mesAnterior = (mes == 1) ? 12 : mes - 1;
        int anoAnterior = (mes == 1) ? ano - 1 : ano;

        Double anterior = agendamentoRepository.buscarFaturamentoMensalUnidade(barbeariaId, mesAnterior, anoAnterior);
        
        if (anterior == null || anterior == 0) return 0.0;

        return ((atual - anterior) / anterior) * 100;
    }

    private String traduzirMes(int mes) {
        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        return meses[mes - 1];
}
    }
