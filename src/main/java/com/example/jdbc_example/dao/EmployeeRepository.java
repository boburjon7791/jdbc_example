package com.example.jdbc_example.dao;

import com.example.jdbc_example.model.base.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query(value = """
            select e.username  as username,
                   e.firstName as firstName
            from Employee e
            """)
    Page<EmployeeProjection> findAllEmployeesJpql(Pageable pageable);

    @Query(nativeQuery = true, value = """
            select e.username   as username ,
                   e.first_name as firstName
            from employees e
            """)
    Page<EmployeeProjection> findAllEmployeesSql(Pageable pageable);
}
