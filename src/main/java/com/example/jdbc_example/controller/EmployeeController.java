package com.example.jdbc_example.controller;

import com.example.jdbc_example.model.request.EmployeeCreateDTO;
import com.example.jdbc_example.model.request.EmployeeUpdateDTO;
import com.example.jdbc_example.model.response.EmployeeGetDTO;
import com.example.jdbc_example.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    @PostMapping
    public EmployeeGetDTO save(@Valid @RequestBody EmployeeCreateDTO createDTO){
        return employeeService.save(createDTO);
    }
    @PutMapping
    public EmployeeGetDTO update(@Valid @RequestBody EmployeeUpdateDTO updateDTO){
        return employeeService.update(updateDTO);
    }
    @GetMapping("/{id}")
    public EmployeeGetDTO findById(@PathVariable Long id){
        return employeeService.findById(id);
    }
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        employeeService.deleteById(id);
    }
    @PostMapping("/multiple-save")
    public Set<EmployeeGetDTO> multipleSave(@RequestBody @Valid EmployeeCreateDTO createDTO){
        AtomicInteger i = new AtomicInteger(0);
        Set<EmployeeCreateDTO> collect = Stream.generate(() -> EmployeeCreateDTO.builder()
                .firstName(createDTO.firstName() + " " + i.getAndIncrement())
                .lastName(createDTO.lastName() + " " + i.getAndIncrement())
                .username(createDTO.username() + " " + i.getAndIncrement())
                .build()).limit(100).collect(Collectors.toSet());
        return employeeService.saveAll(collect);
    }

    @PostMapping("/multiple-save-with-repo")
    public Set<EmployeeGetDTO> multipleSaveWithRepo(@RequestBody @Valid EmployeeCreateDTO createDTO){
        AtomicInteger i = new AtomicInteger(0);
        Set<EmployeeCreateDTO> collect = Stream.generate(() -> EmployeeCreateDTO.builder()
                .firstName(createDTO.firstName() + " " + i.getAndIncrement())
                .lastName(createDTO.lastName() + " " + i.getAndIncrement())
                .username(createDTO.username() + " " + i.getAndIncrement())
                .build()).limit(100).collect(Collectors.toSet());
        return employeeService.saveAllWithRepository(collect);
    }
}
