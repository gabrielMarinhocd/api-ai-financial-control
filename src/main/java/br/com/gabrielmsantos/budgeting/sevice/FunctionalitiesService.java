package br.com.gabrielmsantos.budgeting.sevice;

import br.com.gabrielmsantos.budgeting.domain.model.Functionalities;
import br.com.gabrielmsantos.budgeting.domain.model.Parameters;

import java.util.List;

public interface FunctionalitiesService extends CrudService<Long, Functionalities> {
    List<Functionalities> findAllWithParameters();
}
