package br.com.gabrielmsantos.budgeting.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "tb_functionalities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Functionalities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "functionality", fetch = FetchType.LAZY)
    private List<Parameters> parameters = new ArrayList<>();
}