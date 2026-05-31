package br.com.gabrielmsantos.budgeting.controller.dto;

import br.com.gabrielmsantos.budgeting.domain.model.Parameters;
import br.com.gabrielmsantos.budgeting.domain.model.Functionalities;

public record ParametersDto(Long id, String name, String value, Functionalities functionality) {

    public ParametersDto(Parameters model) {
        this(model.getId(), model.getName(), model.getValue(), model.getFunctionality());
    }

    public Parameters toModel() {
        Parameters model = new Parameters();
        model.setId(this.id);
        model.setName(this.name);
        model.setValue(this.value);
        model.setFunctionality(this.functionality);

        return model;
    }
}