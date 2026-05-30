package br.com.gabrielmsantos.budgeting.controller;

import br.com.gabrielmsantos.budgeting.controller.dto.FunctionalitiesDto;
import br.com.gabrielmsantos.budgeting.sevice.FunctionalitiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/functionalities")
@Tag(name = "Functionalities Controller", description = "RESTFULL API for managing Functionalities.")
public record FuncionalitiesController(FunctionalitiesService functionalitiesService) {

    @GetMapping
    @Operation(summary = "Get all Functionalities", description = "Retrieve a list of all registered Functionalitiess")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful")
    })
    public ResponseEntity<List<FunctionalitiesDto>> findAll() {
        var functionalitiess = functionalitiesService.findAll();
        var functionalitiessDto = functionalitiess.stream().map(FunctionalitiesDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(functionalitiessDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Functionalities by ID", description = "Retrieve a specific Functionalities based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "404", description = "Functionalities not found")
    })
    public ResponseEntity<FunctionalitiesDto> findById(@PathVariable Long id) {
        var functionalities = functionalitiesService.findById(id);
        return ResponseEntity.ok(new FunctionalitiesDto(functionalities));
    }

    @PostMapping
    @Operation(summary = "Create a new Functionalities", description = "Create a new Functionalities and return the created Functionalities's data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Functionalities created successfully"),
            @ApiResponse(responseCode = "422", description = "Invalid Functionalities data provided")
    })
    public ResponseEntity<FunctionalitiesDto> create(@RequestBody FunctionalitiesDto FunctionalitiesDto) {
        var Functionalities = functionalitiesService.create(FunctionalitiesDto.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(Functionalities.getId())
                .toUri();
        return ResponseEntity.created(location).body(new FunctionalitiesDto(Functionalities));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Functionalities", description = "Update the data of an existing Functionalities based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Functionalities updated successfully"),
            @ApiResponse(responseCode = "404", description = "Functionalities not found"),
            @ApiResponse(responseCode = "422", description = "Invalid Functionalities data provided")
    })
    public ResponseEntity<FunctionalitiesDto> update(@PathVariable Long id, @RequestBody FunctionalitiesDto FunctionalitiesDto) {
        var functionalities = functionalitiesService.update(id, FunctionalitiesDto.toModel());
        return ResponseEntity.ok(new FunctionalitiesDto(functionalities));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Functionalities", description = "Delete an existing Functionalities based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Functionalities deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Functionalities not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        functionalitiesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}