package com.barbertime.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.barbertime.entity.Barbearia;


@Repository
public interface BarbeariaRepository extends JpaRepository<Barbearia, Long> {
	
}