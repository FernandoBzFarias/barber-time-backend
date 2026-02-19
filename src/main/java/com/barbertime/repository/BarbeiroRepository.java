package com.barbertime.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.barbertime.entity.Barbeiro;

public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long>{
	   Optional<Barbeiro> findByEmail(String email);
	   List<Barbeiro> findByBarbeariaId(Long barbeariaId); }


