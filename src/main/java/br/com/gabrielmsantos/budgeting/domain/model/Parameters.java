package br.com.gabrielmsantos.budgeting.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity(name = "tb_parameters")
public class Parameters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functionality_id", referencedColumnName = "id")
    @JsonIgnore
    private Functionalities functionality;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Functionalities getFunctionality() {
        return functionality;
    }

    public void setFunctionality(Functionalities functionality) {
        this.functionality = functionality;
    }
}