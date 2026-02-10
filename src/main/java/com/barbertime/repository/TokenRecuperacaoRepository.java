package com.barbertime.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.barbertime.entity.TokenRecuperacao;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, Long> {

    Optional<TokenRecuperacao> findByToken(String token);
 
}
