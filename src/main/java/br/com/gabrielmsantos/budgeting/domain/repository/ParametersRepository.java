package br.com.gabrielmsantos.budgeting.domain.repository;

import br.com.gabrielmsantos.budgeting.domain.model.Parameters;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametersRepository extends JpaRepository<Parameters, Long> {
}