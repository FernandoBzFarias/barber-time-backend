package com.barbertime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.barbertime.dto.BarbeariaRequestDTO;
import com.barbertime.entity.Barbearia;
import com.barbertime.entity.Barbeiro;
import com.barbertime.repository.BarbeariaRepository;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.entity.Role;
import jakarta.transaction.Transactional;

@Service
public class BarbeariaService {

    @Autowired
    private BarbeariaRepository repository;

    @Autowired
    private BarbeiroRepository barbeiroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Barbearia atualizarDados(Long barbeariaId, BarbeariaRequestDTO dto, String emailLogado) {
        // 1. Busca o barbeiro que está logado no sistema
        Barbeiro barbeiro = barbeiroRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // 2. Validação do Modal: A senha enviada bate com a senha do barbeiro logado?
        if (!passwordEncoder.matches(dto.senhaConfirmacao(), barbeiro.getSenha())) {
            throw new RuntimeException("Senha de confirmação incorreta. Acesso negado aos dados sensíveis.");
        }

        // 3. Verifica se ele é ADMIN (Barbeiro Chefe)
        if (!barbeiro.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Apenas o Barbeiro Chefe pode alterar os dados da barbearia.");
        }

        // 4. Se passou nas validações, atualiza a entidade
        Barbearia barbearia = repository.findById(barbeariaId)
                .orElseThrow(() -> new RuntimeException("Barbearia não encontrada."));

        barbearia.setNome(dto.nome());
        barbearia.setEndereco(dto.endereco());
        barbearia.setCidade(dto.cidade());
        barbearia.setEstado(dto.estado());
        barbearia.setTelefone(dto.telefone());
        barbearia.setEmail(dto.email());
        barbearia.setHorarioAbertura(dto.horarioAbertura());
        barbearia.setHorarioFechamento(dto.horarioFechamento());

        return repository.save(barbearia);
    }
    
    @Transactional
    public void atualizarLogo(Long id, String urlImagem) {
        Barbearia barbearia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbearia não encontrada."));
        barbearia.setLogoUrl(urlImagem);
        repository.save(barbearia);
    }
  
}