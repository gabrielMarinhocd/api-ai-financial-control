package br.com.gabrielmsantos.budgeting.domain.repository;

import br.com.gabrielmsantos.budgeting.domain.model.Functionalities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FunctionalitiesRepository extends JpaRepository<Functionalities, Long> {

    @Query("""
    SELECT DISTINCT f
    FROM tb_functionalities f
    LEFT JOIN FETCH f.parameters
""")
    List<Functionalities> findAllWithParameters();

}
