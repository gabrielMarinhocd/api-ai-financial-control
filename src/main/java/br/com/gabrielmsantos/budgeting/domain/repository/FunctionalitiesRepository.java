package br.com.gabrielmsantos.budgeting.domain.repository;

import br.com.gabrielmsantos.budgeting.domain.model.Functionalities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FunctionalitiesRepository extends JpaRepository<Functionalities, Long> {
}
