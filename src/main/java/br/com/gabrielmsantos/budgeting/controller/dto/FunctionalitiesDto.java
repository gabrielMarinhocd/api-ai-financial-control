package br.com.gabrielmsantos.budgeting.controller.dto;

import br.com.gabrielmsantos.budgeting.domain.model.Functionalities;
import br.com.gabrielmsantos.budgeting.domain.model.Parameters;
import org.hibernate.annotations.Any;

import java.util.List;

public record FunctionalitiesDto(Long id, String name, String description, List<Parameters> parametersSchema) {

    public FunctionalitiesDto(Functionalities model) {
        this(model.getId(), model.getName(), model.getDescription(), model.getParameters());
    }

    public Functionalities toModel() {
        Functionalities model = new Functionalities();
        model.setId(this.id);
        model.setName(this.name);
        model.setDescription(this.description);
        model.setParameters(this.parametersSchema);

        return model;
    }
}
