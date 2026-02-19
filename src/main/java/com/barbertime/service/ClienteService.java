package com.barbertime.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barbertime.dto.ClienteDetalheDTO;
import com.barbertime.dto.ClienteResumoDTO;
import com.barbertime.dto.HistoricoAgendamentoDTO;
import com.barbertime.dto.NovoClienteDTO;
import com.barbertime.entity.Cliente;
import com.barbertime.repository.AgendamentoRepository;
import com.barbertime.repository.ClienteRepository;

import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // Agora recebe o barbeariaId vindo do Token/Controller
    public List<ClienteResumoDTO> listarClientes(String busca, Long barbeariaId) {
        List<Cliente> clientes;
        
        if (busca == null || busca.isEmpty()) {
            clientes = clienteRepository.findByBarbeariaId(barbeariaId);
        } else {
            // Chamada corrigida aqui:
            clientes = clienteRepository.buscarPorTermoEBarbearia(barbeariaId, busca);
        }

        return clientes.stream()
            .map(c -> new ClienteResumoDTO(
                c.getId(), 
                c.getNome(), 
                c.getTelefone(), 
                c.getTotalCortes(), 
                c.getModalidade().name()))
            .toList();
    }

    public ClienteDetalheDTO obterDetalhes(Long id, Long barbeariaId) {
        Cliente c = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        
        // Bloqueio de segurança (Ponto 1.1)
        if (!c.getBarbeariaId().equals(barbeariaId)) {
            throw new RuntimeException("Acesso negado: Este cliente pertence a outra unidade.");
        }

        List<HistoricoAgendamentoDTO> historico = agendamentoRepository.findFirst10ByClienteIdOrderByDataDesc(id)
            .stream()    
            .map(a -> new HistoricoAgendamentoDTO(a.getData(), a.getHorario(), a.getServico(), a.getValor(), a.getStatus().name()))
            .toList();

        double media = (c.getTotalCortes() > 0) ? c.getTotalGasto() / c.getTotalCortes() : 0.0;

        return new ClienteDetalheDTO(
            c.getId(), c.getNome(), c.getTelefone(), c.getModalidade().name(),
            c.getTotalCortes(), c.getQuantidadeAtrasos(), media, c.getNotasTecnicas(), historico
        );
    }

    @Transactional
    public void atualizarNotas(Long id, String notas, Long barbeariaId) {
        Cliente c = clienteRepository.findById(id).orElseThrow();
        
        if (!c.getBarbeariaId().equals(barbeariaId)) {
            throw new RuntimeException("Acesso negado.");
        }

        // Sanitização básica contra XSS (Ponto 1.3)
        String notasLimpas = notas.replaceAll("<[^>]*>", ""); // Remove tags HTML
        c.setNotasTecnicas(notasLimpas);
        clienteRepository.save(c);
    }
    
    @Transactional
    public ClienteResumoDTO cadastrarClienteManual(NovoClienteDTO dto, Long barbeariaId) {
        // 1. Verifica se já existe um cliente com esse telefone NESTA barbearia
        if (clienteRepository.findByTelefoneAndBarbeariaId(dto.telefone(), barbeariaId).isPresent()) {
            throw new RuntimeException("Este cliente já está cadastrado nesta unidade.");
        }

        // 2. Cria a nova entidade
        Cliente novo = new Cliente();
        novo.setNome(dto.nome());
        novo.setTelefone(dto.telefone());
        novo.setNotasTecnicas(dto.notas());
        novo.setBarbeariaId(barbeariaId); // Segurança: Vincula à barbearia do logado
        novo.setModalidade(Cliente.ModalidadeCliente.NOVO);

        Cliente salvo = clienteRepository.save(novo);

        // 3. Retorna o DTO resumido para atualizar a lista no seu Front-end
        return new ClienteResumoDTO(
            salvo.getId(), 
            salvo.getNome(), 
            salvo.getTelefone(), 
            0, 
            salvo.getModalidade().name()
        );
    }
    
    
    
    
    
    
    
    
    
    
    
}