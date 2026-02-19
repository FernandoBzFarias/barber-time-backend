package com.barbertime.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.barbertime.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByTelefoneAndBarbeariaId(String telefone, Long barbeariaId);

    List<Cliente> findByBarbeariaId(Long barbeariaId);

    // Esta query resolve o erro e garante que o OR não "vaze" dados de outra barbearia
    @Query("SELECT c FROM Cliente c WHERE c.barbeariaId = :barbeariaId " +
           "AND (LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) " +
           "OR c.telefone LIKE CONCAT('%', :busca, '%'))")
    List<Cliente> buscarPorTermoEBarbearia(@Param("barbeariaId") Long barbeariaId, @Param("busca") String busca);
}
