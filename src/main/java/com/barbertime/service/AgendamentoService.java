package com.barbertime.service;

import com.barbertime.dto.AgendaBarbeiroResponseDTO;
import com.barbertime.dto.AgendaGeralBarbeariaDTO;
import com.barbertime.dto.HorarioDisponivelDTO;
import com.barbertime.dto.NovoAgendamentoDTO;
import com.barbertime.entity.Agendamento;
import com.barbertime.entity.Barbeiro;
import com.barbertime.entity.GradeHoraria;
import com.barbertime.entity.StatusAgendamento;
import com.barbertime.exception.BusinessException;
import com.barbertime.repository.AgendamentoRepository;
import com.barbertime.repository.BarbeiroRepository;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private BarbeiroRepository barbeiroRepository;
    @Autowired
    private GradeHorariaRepository gradeRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE) 
    public String criarAgendamento(@Valid NovoAgendamentoDTO dto) {
        
        // 1. Valida existência do barbeiro
        Barbeiro barbeiro = barbeiroRepository.findById(dto.getBarbeiroId())
                .orElseThrow(() -> new EntityNotFoundException("Barbeiro não encontrado"));

        // 2. Valida se o horário pertence à grade (Regra de Negócio)
        boolean naGrade = gradeRepository.existsByBarbeiroIdAndHorario(dto.getBarbeiroId(), dto.getHorario());
        if (!naGrade) {
            throw new BusinessException("Este horário não faz parte da grade de trabalho deste barbeiro.");
        }

        // 3. Valida se é uma data/hora futura
        LocalDateTime agendamentoFull = LocalDateTime.of(dto.getData(), dto.getHorario());
        if (agendamentoFull.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Não é possível agendar para um horário que já passou.");
        }

        // 4. Tenta salvar (A Unique Constraint no banco impedirá a concorrência)
        try {
            // Verifica antes para evitar disparar a exception do banco desnecessariamente
            if (agendamentoRepository.existsByBarbeiroIdAndDataAndHorarioAndStatusNot(
                    dto.getBarbeiroId(), dto.getData(), dto.getHorario(), StatusAgendamento.CANCELADO)) {
                throw new BusinessException("Este horário já está ocupado.");
            }

            Agendamento agendamento = new Agendamento();
            agendamento.setBarbeiro(barbeiro);
            agendamento.setData(dto.getData());
            agendamento.setHorario(dto.getHorario());
            agendamento.setClienteNome(dto.getClienteNome());
            agendamento.setClienteTelefone(dto.getClienteTelefone());
            agendamento.setStatus(StatusAgendamento.CONFIRMADO);

            agendamentoRepository.save(agendamento);
            return "Agendamento realizado com sucesso!";
            
        } catch (DataIntegrityViolationException e) {
            // Caso dois agendamentos passem pelo IF ao mesmo tempo, o banco travará aqui
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

        // Lógica do Filtro: Se o barbeiroId for nulo, traz "Todos" (primeiro botão do seu protótipo)
        if (barbeiroIdFiltro == null) {
            agendamentos = agendamentoRepository.findByData(data);
        } else {
            agendamentos = agendamentoRepository.findByBarbeiroIdAndData(barbeiroIdFiltro, data);
        }

        return agendamentos.stream()
            .map(a -> {
                // Gera o link do WhatsApp com mensagem automática
                String msg = "Olá " + a.getClienteNome() + ", aqui é da Barbearia. Confirmamos seu horário às " + a.getHorario() + "?";
                String linkWa = "https://wa.me/55" + a.getClienteTelefone().replaceAll("\\D", "") 
                                + "?text=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
                
                return new AgendaBarbeiroResponseDTO(
                    a.getId(), 
                    a.getHorario(), 
                    a.getClienteNome(), 
                    a.getClienteTelefone(),
                    a.getServico(), // "Corte", "Barba", etc.
                    a.getValor(), 
                    a.getStatus().name(), 
                    linkWa, 
                    a.getBarbeiro().getNome()
                );
            })
            .sorted(Comparator.comparing(AgendaBarbeiroResponseDTO::horario))
            .toList();
    }
    
    @Transactional
    public void atualizarStatus(Long agendamentoId, StatusAgendamento novoStatus) {
    	
        // 1. Busca o agendamento no banco
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new BusinessException("Agendamento não encontrado"));
        
        // 2. Segurança: Identifica quem está logado pelo Token
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // 3. Bloqueia se um barbeiro tentar mexer na agenda de outro
        if (!agendamento.getBarbeiro().getEmail().equals(emailLogado)) {
            throw new BusinessException("Você não tem permissão para alterar este agendamento.");
        }

        // 4. Salva a alteração (isso vai mudar a cor do card no front-end)
        agendamento.setStatus(novoStatus);
        agendamentoRepository.save(agendamento);
    }
    
    @Transactional(readOnly = true)
    public List<AgendaGeralBarbeariaDTO> buscarAgendaCompletaDaBarbearia(LocalDate data) {
        // 1. Busca todos os barbeiros cadastrados
        List<Barbeiro> barbeiros = barbeiroRepository.findAll();

        // 2. Para cada barbeiro, gera a lista de agendamentos dele
        return barbeiros.stream().map(b -> {
            // Reutilizamos o método buscarAgendaDashboard que já criamos antes
            List<AgendaBarbeiroResponseDTO> agendaDoBarbeiro = buscarAgendaDashboard(data, b.getId());
            
            return new AgendaGeralBarbeariaDTO(
                b.getId(),
                b.getNome(),
                agendaDoBarbeiro
            );
        }).toList();
    }
    
}
