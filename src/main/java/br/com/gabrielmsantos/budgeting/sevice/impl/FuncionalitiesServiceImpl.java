package br.com.gabrielmsantos.budgeting.sevice.impl;

import br.com.gabrielmsantos.budgeting.domain.model.Functionalities;
import br.com.gabrielmsantos.budgeting.domain.model.Parameters;
import br.com.gabrielmsantos.budgeting.domain.repository.FunctionalitiesRepository;
import br.com.gabrielmsantos.budgeting.domain.repository.ParametersRepository;
import br.com.gabrielmsantos.budgeting.sevice.FunctionalitiesService;
import br.com.gabrielmsantos.budgeting.sevice.exception.BusinessException;
import br.com.gabrielmsantos.budgeting.sevice.exception.NotFoundException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class FuncionalitiesServiceImpl implements FunctionalitiesService {
    private final FunctionalitiesRepository functionalitiesRepository;
    private  final ParametersRepository parametersRepository;

    public FuncionalitiesServiceImpl(FunctionalitiesRepository functionalitiesRepository, ParametersRepository parametersRepository) {
        this.functionalitiesRepository = functionalitiesRepository;
        this.parametersRepository = parametersRepository;
    }

    @Transactional(readOnly = true)
    public List<Functionalities> findAll() {
        return this.functionalitiesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Functionalities> findAllWithParameters() {
        return this.functionalitiesRepository.findAllWithParameters();
    }

    @Transactional(readOnly = true)
    public Functionalities findById(Long id) {
        return this.functionalitiesRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Functionalities create(Functionalities functionalitiesToCreate) {

        ofNullable(functionalitiesToCreate)
                .orElseThrow(() -> new BusinessException("Functionalities to create must not be null."));

        Functionalities response = this.functionalitiesRepository.save(functionalitiesToCreate);

        if (functionalitiesToCreate.getParameters() != null) {
            for (Parameters parameter : functionalitiesToCreate.getParameters()) {
                parameter.setFunctionality(response);
                this.parametersRepository.save(parameter);
            }
        }

        return response;
    }

    @Transactional
    public Functionalities update(Long id, Functionalities FunctionalitiesToUpdate) {
        Functionalities dbFunctionalities = this.findById(id);
        if (!dbFunctionalities.getId().equals(FunctionalitiesToUpdate.getId())) {
            throw new BusinessException("Update IDs must be the same.");
        }

        dbFunctionalities.setName(FunctionalitiesToUpdate.getName());
        dbFunctionalities.setDescription(FunctionalitiesToUpdate.getDescription());

        return this.functionalitiesRepository.save(dbFunctionalities);
    }

    @Transactional
    public void delete(Long id) {
        Functionalities dbFunctionalities = this.findById(id);
        this.functionalitiesRepository.delete(dbFunctionalities);
    }
}