package com.example.jdbc_example.controller;

import com.example.jdbc_example.dao.EmployeeProjection;
import com.example.jdbc_example.model.base.ApiResponse;
import com.example.jdbc_example.model.base.PaginationData;
import com.example.jdbc_example.model.request.EmployeeCreateDTO;
import com.example.jdbc_example.model.request.EmployeeUpdateDTO;
import com.example.jdbc_example.model.response.EmployeeGetDTO;
import com.example.jdbc_example.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @GetMapping("/jpql")
    public ApiResponse<List<EmployeeProjection>> findAllWithJpql(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        Page<EmployeeProjection> employee = employeeService.findAllEmployeeWithJpql(PageRequest.of(page, size));
        return ApiResponse.ok(employee.getContent(), PaginationData.of(employee));
    }

    @GetMapping("/sql")
    public Page<EmployeeProjection> findAllWithSql(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return employeeService.findAllEmployeeWithSql(PageRequest.of(page, size));
    }

    @GetMapping("/jpql/object-array")
    public ApiResponse<List<Object[]>> findAllWithJpqlObjectArray(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        Page<Object[]> employee = employeeService.findAllEmployeeWithJpqlObjectArray(PageRequest.of(page, size));
        return ApiResponse.ok(employee.getContent(), PaginationData.of(employee));
    }

    @GetMapping("/sql/object-array")
    public Page<Object[]> findAllWithSqlObjectArray(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return employeeService.findAllEmployeeWithSqlObjectArray(PageRequest.of(page, size));
    }
}
