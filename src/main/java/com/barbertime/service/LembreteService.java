package com.barbertime.service;

import com.barbertime.entity.Agendamento;
import com.barbertime.entity.StatusAgendamento;
import com.barbertime.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LembreteService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private NotificationService notificationService;

    // Executa a cada 60 segundos (60000 milissegundos)
    @Scheduled(fixedRate = 60000)
    public void verificarEnviarLembretes() {
        LocalDate hoje = LocalDate.now();
        // Define a janela de tempo: daqui a exatamente 30 minutos
        LocalTime agoraMais30 = LocalTime.now().plusMinutes(30).truncatedTo(ChronoUnit.MINUTES);

        // Procura agendamentos confirmados para hoje e para este horário específico
        List<Agendamento> agendamentos = agendamentoRepository
                .findByDataAndHorarioAndStatus(hoje, agoraMais30, StatusAgendamento.CONFIRMADO);

        for (Agendamento agenda : agendamentos) {
            var barbeiro = agenda.getBarbeiro();
            
            // Verifica se o barbeiro quer receber lembretes e se tem token
            if (barbeiro.isNotificarLembretes() && barbeiro.getDeviceToken() != null) {
                notificationService.enviarPush(
                    barbeiro.getDeviceToken(),
                    "Lembrete de Corte ⏰",
                    "Faltam 30 minutos para o cliente " + agenda.getClienteNome() + " às " + agenda.getHorario()
                );
            }
        }
    }
}
