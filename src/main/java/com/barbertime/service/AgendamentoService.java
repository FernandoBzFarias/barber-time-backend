package com.barbertime.service;

import com.barbertime.dto.AgendaBarbeiroResponseDTO;
import com.barbertime.dto.AgendaGeralBarbeariaDTO;
import com.barbertime.dto.DashboardHomeDTO;
import com.barbertime.dto.HorarioDisponivelDTO;
import com.barbertime.dto.MarketingDTO;
import com.barbertime.dto.NovoAgendamentoDTO;
import com.barbertime.dto.RelatorioCompletoDTO;
import com.barbertime.dto.ResumoDiarioDTO;
import com.barbertime.entity.Agendamento;
import com.barbertime.entity.Barbeiro;
import com.barbertime.entity.Cliente;
import com.barbertime.entity.GradeHoraria;
import com.barbertime.entity.StatusAgendamento;
import com.barbertime.exception.BusinessException;
import com.barbertime.repository.AgendamentoRepository;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.repository.ClienteRepository;
import com.barbertime.repository.GradeHorariaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map; // Resolve o erro: Map cannot be resolved
import com.barbertime.dto.FaturamentoMensalDTO;



@Slf4j
@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private BarbeiroRepository barbeiroRepository;
    @Autowired
    private GradeHorariaRepository gradeRepository;
    @Autowired
    private ClienteRepository clienteRepository;
       
    @Autowired
    private NotificationService notificationService;

    @Transactional(isolation = Isolation.SERIALIZABLE) 
    public String criarAgendamento(@Valid NovoAgendamentoDTO dto) {
        
        // 1. Valida barbeiro e obtém a barbearia dele
        Barbeiro barbeiro = barbeiroRepository.findById(dto.getBarbeiroId())
                .orElseThrow(() -> new EntityNotFoundException("Barbeiro não encontrado"));
        
        // IMPORTANTE: Precisamos saber qual barbearia está operando
        Long barbeariaId = barbeiro.getBarbeariaId(); 

        // 2. Validações de Grade e Data (Mantidas)
        if (!gradeRepository.existsByBarbeiroIdAndHorario(dto.getBarbeiroId(), dto.getHorario())) {
            throw new BusinessException("Este horário não faz parte da grade de trabalho.");
        }

        if (LocalDateTime.of(dto.getData(), dto.getHorario()).isBefore(LocalDateTime.now())) {
            throw new BusinessException("Não é possível agendar para o passado.");
        }
 
        try {
            // 3. Verifica ocupação (Mantido)
            if (agendamentoRepository.existsByBarbeiroIdAndDataAndHorarioAndStatusNot(
                    dto.getBarbeiroId(), dto.getData(), dto.getHorario(), StatusAgendamento.CANCELADO)) {
                throw new BusinessException("Este horário já está ocupado.");
            }

            // 4. Lógica de Cliente com Isolamento (Regra 1.1 e 4)
            // Agora buscamos o cliente pelo telefone DENTRO da barbearia específica
            Cliente cliente = clienteRepository.findByTelefoneAndBarbeariaId(dto.getClienteTelefone(), barbeariaId)
                    .orElseGet(() -> {
                        Cliente novo = new Cliente();
                        novo.setNome(dto.getClienteNome());
                        novo.setTelefone(dto.getClienteTelefone());
                        novo.setBarbeariaId(barbeariaId); // <--- VINCULO OBRIGATÓRIO
                        novo.setModalidade(Cliente.ModalidadeCliente.NOVO);
                        return clienteRepository.save(novo); 
                    });

            // 5. Salva o Agendamento
            Agendamento agendamento = new Agendamento();
            agendamento.setBarbeiro(barbeiro);
            agendamento.setData(dto.getData());
            agendamento.setHorario(dto.getHorario());
            agendamento.setClienteNome(dto.getClienteNome());
            agendamento.setClienteTelefone(dto.getClienteTelefone());
            agendamento.setStatus(StatusAgendamento.CONFIRMADO);
            agendamento.setCliente(cliente); 

            if (barbeiro.isNotificarAgendamentos() && barbeiro.getDeviceToken() != null) {
                notificationService.enviarPush(
                    barbeiro.getDeviceToken(), 
                    "Novo Agendamento! ✂️", 
                    "O cliente " + dto.getClienteNome() + " agendou para às " + dto.getHorario()
                );
            }
            
            agendamentoRepository.save(agendamento);
            return "Agendamento realizado com sucesso!";     

        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Conflito de agendamento: Este horário acabou de ser preenchido.");
        }
    }
    public List<HorarioDisponivelDTO> listarDisponibilidade(Long barbeiroId, LocalDate data) {
    	
        // 1. Busca todos os horários que o barbeiro cadastrou na grade dele
        List<GradeHoraria> grade = gradeRepository.findByBarbeiroIdOrderByHorarioAsc(barbeiroId);

        // 2. Busca todos os agendamentos já marcados (não cancelados) para aquele dia
        List<Agendamento> ocupados = agendamentoRepository.findByBarbeiroIdAndData(barbeiroId, data)
                .stream()
                .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO)
                .toList();

        // 3. Mapeia a grade verificando se cada horário está livre ou ocupado
        return grade.stream().map(g -> {
            boolean estaOcupado = ocupados.stream()
                    .anyMatch(o -> o.getHorario().equals(g.getHorario()));
            
            // Regra extra: Se for para hoje, não mostrar horários que já passaram do tempo atual
            boolean jaPassou = data.equals(LocalDate.now()) && g.getHorario().isBefore(LocalTime.now());

            return new HorarioDisponivelDTO(g.getHorario(), !estaOcupado && !jaPassou);}).toList();}   
    
    
    @Transactional
    public void salvarGrade(List<LocalTime> horarios) {
        // 1. Pega o email do barbeiro logado no Token
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Busca o barbeiro no banco pelo email
        Barbeiro barbeiro = barbeiroRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        // 3. Remove a grade antiga para não duplicar
        gradeRepository.deleteAllByBarbeiroId(barbeiro.getId());

        // 4. Salva os novos horários
        List<GradeHoraria> novasGrades = horarios.stream().map(horario -> {
            GradeHoraria gh = new GradeHoraria();
            gh.setHorario(horario);
            gh.setBarbeiro(barbeiro);
            return gh;
        }).toList();

        gradeRepository.saveAll(novasGrades);
    }
    
    // alcula o link do WhatsApp e formata os dados.
    @Transactional(readOnly = true)
    public List<AgendaBarbeiroResponseDTO> buscarAgendaDashboard(LocalDate data, Long barbeiroIdFiltro) {
        List<Agendamento> agendamentos;

        // 1. Buscamos o email de quem está logado para saber qual a barbearia dele
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // 2. Criamos a variável barbeariaId buscando do banco (ISSO RESOLVE O ERRO)
        Long barbeariaId = barbeiroRepository.findByEmail(emailLogado) // <-- DECLARAÇÃO DA VARIÁVEL
                .orElseThrow(() -> new RuntimeException("Barbeiro logado não encontrado"))
                .getBarbeariaId();

        // 3. Lógica do Filtro:
        if (barbeiroIdFiltro == null) {
            // Agora a variável 'barbeariaId' existe e pode ser usada aqui
            agendamentos = agendamentoRepository.findByDataAndBarbearia(data, barbeariaId); // <-- USO DA VARIÁVEL
        } else {
            agendamentos = agendamentoRepository.findByBarbeiroIdAndData(barbeiroIdFiltro, data);
            
            // Proteção para garantir que o filtro por barbeiro respeite a barbearia logada
            if (!agendamentos.isEmpty() && !agendamentos.get(0).getBarbeiro().getBarbeariaId().equals(barbeariaId)) {
                throw new RuntimeException("Acesso negado: Este barbeiro pertence a outra unidade.");
            }
        }

        return agendamentos.stream()
            .map(a -> {
                String msg = "Olá " + a.getClienteNome() + ", aqui é da Barbearia. Confirmamos seu horário às " + a.getHorario() + "?";
                String linkWa = "https://wa.me/55" + a.getClienteTelefone().replaceAll("\\D", "") 
                                + "?text=" + URLEncoder.encode(msg, java.nio.charset.StandardCharsets.UTF_8);
                
                return new AgendaBarbeiroResponseDTO(
                    a.getId(), a.getHorario(), a.getClienteNome(), a.getClienteTelefone(),
                    a.getServico(), a.getValor(), a.getStatus().name(), linkWa, a.getBarbeiro().getNome()
                );
            })
            .sorted(java.util.Comparator.comparing(AgendaBarbeiroResponseDTO::horario))
            .toList();
    }
    
    @Transactional
    public void atualizarStatus(Long agendamentoId, StatusAgendamento novoStatus) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new BusinessException("Agendamento não encontrado"));

        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!agendamento.getBarbeiro().getEmail().equals(emailLogado)) {
            throw new BusinessException("Você não tem permissão para alterar este agendamento.");
        }

        StatusAgendamento statusAntigo = agendamento.getStatus();
        if (statusAntigo == novoStatus) return; // Se não mudou nada, não faz nada.

        agendamento.setStatus(novoStatus);
        
        if (novoStatus == StatusAgendamento.CANCELADO) {
            Barbeiro barbeiro = agendamento.getBarbeiro();
            if (barbeiro.isNotificarCancelamentos() && barbeiro.getDeviceToken() != null) {
                notificationService.enviarPush(
                    barbeiro.getDeviceToken(),
                    "Agendamento Cancelado ⚠️",
                    "O horário das " + agendamento.getHorario() + " foi cancelado."
                );
            }
        }
        
        Cliente cliente = agendamento.getCliente();

        if (cliente != null) {
            if (novoStatus == StatusAgendamento.FINALIZADO) {
                cliente.setTotalCortes(cliente.getTotalCortes() + 1);
                cliente.setCortesSeguidosSemAtraso(cliente.getCortesSeguidosSemAtraso() + 1);
                
                // Regra de Redenção
                if (cliente.getModalidade() == Cliente.ModalidadeCliente.ATRASADO && cliente.getCortesSeguidosSemAtraso() >= 4) {
                    cliente.setQuantidadeAtrasos(0);
                }
            } else if (novoStatus == StatusAgendamento.FALTOU) {
                cliente.setQuantidadeAtrasos(cliente.getQuantidadeAtrasos() + 1);
                cliente.setCortesSeguidosSemAtraso(0);
            }

            // Recalcula a modalidade (Regra 11)
            if (cliente.getQuantidadeAtrasos() >= 3) {
                cliente.setModalidade(Cliente.ModalidadeCliente.ATRASADO);
            } else if (cliente.getTotalCortes() >= 10) {
                cliente.setModalidade(Cliente.ModalidadeCliente.VIP);
            } else if (cliente.getTotalCortes() >= 4) {
                cliente.setModalidade(Cliente.ModalidadeCliente.FIEL);
            } else {
                cliente.setModalidade(Cliente.ModalidadeCliente.NOVO);
            }
            clienteRepository.save(cliente);
        }
        agendamentoRepository.save(agendamento);
    }

    @Transactional(readOnly = true)
    public List<AgendaGeralBarbeariaDTO> buscarAgendaCompletaDaBarbearia(LocalDate data) {
        // 1. Pega a barbearia do usuário logado
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long barbeariaId = barbeiroRepository.findByEmail(email).get().getBarbeariaId();

        // 2. Busca APENAS os barbeiros daquela barbearia específica
        List<Barbeiro> barbeiros = barbeiroRepository.findByBarbeariaId(barbeariaId); 

        return barbeiros.stream().map(b -> {
            List<AgendaBarbeiroResponseDTO> agendaDoBarbeiro = buscarAgendaDashboard(data, b.getId());
            return new AgendaGeralBarbeariaDTO(b.getId(), b.getNome(), agendaDoBarbeiro);
        }).toList();
    }
    
    public ResumoDiarioDTO obterResumoDiario(Long barbeiroId, LocalDate data) {
        // 🔴 Use o nome correto do seu repositório (ex: agendamentoRepository)
        // Se o seu repositório for 'repository', verifique se ele foi declarado no topo da classe
        List<Agendamento> agendamentos = agendamentoRepository.findByBarbeiroIdAndData(barbeiroId, data);
        
        var listaAtiva = agendamentos.stream()
            .filter(a -> !a.getStatus().equals(StatusAgendamento.CANCELADO))
            .toList();

        Integer total = listaAtiva.size();
        Double faturamento = listaAtiva.stream()
            .mapToDouble(a -> a.getValor() != null ? a.getValor() : 0.0)
            .sum();
        
        String primeiro = listaAtiva.isEmpty() ? "--:--" : listaAtiva.get(0).getHorario().toString();
        String ultimo = listaAtiva.isEmpty() ? "--:--" : listaAtiva.get(listaAtiva.size() - 1).getHorario().toString();

        return new ResumoDiarioDTO(total, faturamento, primeiro, ultimo);
    }
    @Transactional(readOnly = true)
    public RelatorioCompletoDTO obterRelatorioGeral(Long barbeariaId) {
        // 1. Busca todos os agendamentos finalizados da barbearia
        List<Agendamento> finalizados = agendamentoRepository
                .findByBarbeiroBarbeariaIdAndStatus(barbeariaId, StatusAgendamento.FINALIZADO);

        // 2. Cálculos básicos
        long total = finalizados.size();
        double faturamentoTotal = finalizados.stream()
                .mapToDouble(a -> a.getValor() != null ? a.getValor() : 0.0)
                .sum();
        
        double ticketMedio = total > 0 ? faturamentoTotal / total : 0.0;

        // --- MELHORIA: Faturamento Mensal Dinâmico ---
        // Agrupa por Mês (Ex: "JANUARY", "FEBRUARY") e soma os valores
        Map<String, Double> faturamentoPorMes = finalizados.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        a -> a.getData().getMonth().name(),
                        java.util.stream.Collectors.summingDouble(a -> a.getValor() != null ? a.getValor() : 0.0)
                ));

        List<FaturamentoMensalDTO> faturamentoMensal = faturamentoPorMes.entrySet().stream()
                .map(entry -> new FaturamentoMensalDTO(entry.getKey(), entry.getValue()))
                .toList();

        // --- MELHORIA: Distribuição de Serviços (Gráfico de Pizza) ---
        Map<String, Double> servicosMaisRealizados = finalizados.stream()
                .filter(a -> a.getServico() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        Agendamento::getServico,
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (e.getValue().doubleValue() / total) * 100
                ));

        // 3. Lógica para Taxa de Retorno
        long clientesRecorrentes = finalizados.stream()
                .map(a -> a.getCliente().getId())
                .distinct()
                .filter(id -> agendamentoRepository.countByClienteIdAndStatus(id, StatusAgendamento.FINALIZADO) > 1)
                .count();
        
        double taxaRetorno = total > 0 ? (double) clientesRecorrentes / total * 100 : 0.0;

        // O valor 18.0 (crescimento) pode ser calculado comparando com o mês anterior futuramente
        return new RelatorioCompletoDTO(
            total, 
            ticketMedio, 
            taxaRetorno, 
            18.0, 
            faturamentoMensal, 
            servicosMaisRealizados
        );
    }
    
    @Transactional(readOnly = true)
    public MarketingDTO obterDadosMarketing() {
        // 1. Pega o e-mail do barbeiro logado através do Token JWT
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Busca os dados do barbeiro/barbearia
        Barbeiro barbeiro = barbeiroRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new EntityNotFoundException("Barbeiro não encontrado"));

        // 3. Define a URL base (Em produção, isso viria do application.properties)
        String baseUrl = "https://barbertime.com/"; 
        
        // Se o slug for nulo, usamos um fallback baseado no nome para não quebrar a tela
        String slug = (barbeiro.getSlug() != null) ? barbeiro.getSlug() : "unidade-" + barbeiro.getBarbeariaId();
        String linkCompleto = baseUrl + slug;

        return new MarketingDTO(
            linkCompleto, 
            slug, 
            barbeiro.getNome()
        );
    }
    @Transactional(readOnly = true)
    public DashboardHomeDTO obterDashboardPrincipal() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Barbeiro barbeiro = barbeiroRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Barbeiro não encontrado"));

        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        // Busca todos os agendamentos confirmados de hoje para este barbeiro
        List<Agendamento> agendaHoje = agendamentoRepository
                .findByBarbeiroIdAndDataAndStatus(barbeiro.getId(), hoje, StatusAgendamento.CONFIRMADO)
                .stream()
                .sorted(java.util.Comparator.comparing(Agendamento::getHorario))
                .toList();

        // 1. Corte Atual: O agendamento mais próximo que ainda não passou muito tempo
        // (Ex: se o corte dura 30min, ele ainda é o atual até 30min depois do início)
        Agendamento atual = agendaHoje.stream()
                .filter(a -> !a.getHorario().isAfter(agora))
                .reduce((first, second) -> second) // Pega o último que já começou
                .orElse(null);

        // 2. Próximo Corte: O primeiro agendamento que é depois de "agora"
        Agendamento proximo = agendaHoje.stream()
                .filter(a -> a.getHorario().isAfter(agora))
                .findFirst()
                .orElse(null);

        // 3. Total de Clientes hoje (Finalizados + Confirmados)
        long totalHoje = agendamentoRepository.countByBarbeiroIdAndData(barbeiro.getId(), hoje);

        return new DashboardHomeDTO(
            atual != null ? new DashboardHomeDTO.CorteInfoDTO(atual.getClienteNome(), atual.getServico(), atual.getHorario().toString()) : null,
            proximo != null ? new DashboardHomeDTO.CorteInfoDTO(proximo.getClienteNome(), proximo.getServico(), proximo.getHorario().toString()) : null,
            totalHoje
        );
    }
}