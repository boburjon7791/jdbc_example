package com.example.jdbc_example.service;

import com.example.jdbc_example.dao.EmployeeDao;
import com.example.jdbc_example.dao.EmployeeRepository;
import com.example.jdbc_example.model.base.Employee;
import com.example.jdbc_example.model.request.EmployeeCreateDTO;
import com.example.jdbc_example.model.request.EmployeeUpdateDTO;
import com.example.jdbc_example.model.response.EmployeeGetDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeDao employeeDao;
    private final EmployeeRepository employeeRepository;
    public EmployeeGetDTO save(EmployeeCreateDTO createDTO){
        return employeeDao.save(createDTO);
    }
    public Set<EmployeeGetDTO> saveAll(Set<EmployeeCreateDTO> createDTOSet){
        Instant start = Instant.now();

        Set<EmployeeGetDTO> employeeGetDTOS = employeeDao.saveAll(createDTOSet);

        Instant end = Instant.now();

        /**
         * this is result of saving time for 100 rows
         * Duration.between(start, end).toMillis() = 50
         * Duration.between(start, end).toNanos() = 50306100
         *
         * this is result of saving time for 10000 rows
         * Duration.between(start, end).toMillis() = 441
         * Duration.between(start, end).toNanos() = 441779500
         *
         * this is result of saving time for 1000000 rows
         * Duration.between(start, end).toMillis() = 34880
         * Duration.between(start, end).toNanos() = 34880042900
         * */
        System.out.println("Duration.between(start, end).toMillis() = " + Duration.between(start, end).toMillis());
        System.out.println("Duration.between(start, end).toNanos() = " + Duration.between(start, end).toNanos());
        return employeeGetDTOS;
    }
    public Set<EmployeeGetDTO> saveAllWithRepository(Set<EmployeeCreateDTO> createDTOSet){
        Instant start=Instant.now();

        employeeRepository.saveAll(createDTOSet.stream().map(EmployeeCreateDTO::toEntity).collect(Collectors.toSet()));

        Instant end=Instant.now();

        /**
         * this is result of saving time for 100 rows
         * Duration.between(start, end).toMillis() = 267
         * Duration.between(start, end).toNanos() = 267879800
         *
         * this is result of saving time for 10000 rows
         * Duration.between(start, end).toMillis() = 6516
         * Duration.between(start, end).toNanos() = 6516769800
         *
         * this is result of saving time for 1000000 rows
         * Duration.between(start, end).toMillis() = 245751
         * Duration.between(start, end).toNanos() = 245751318000
        * */
        System.out.println("Duration.between(start, end).toMillis() = " + Duration.between(start, end).toMillis());
        System.out.println("Duration.between(start, end).toNanos() = " + Duration.between(start, end).toNanos());
        return Set.of();
    }
    public EmployeeGetDTO update(EmployeeUpdateDTO updateDTO){
        return employeeDao.update(updateDTO);
    }
    public EmployeeGetDTO findById(Long id){
        return employeeDao.findById(id);
    }
    public void deleteById(Long id){
        employeeDao.deleteById(id);
    }
}
