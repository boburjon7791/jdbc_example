package com.example.jdbc_example.dao;

import com.example.jdbc_example.model.base.Employee;
import com.example.jdbc_example.model.request.EmployeeCreateDTO;
import com.example.jdbc_example.model.request.EmployeeUpdateDTO;
import com.example.jdbc_example.model.response.EmployeeGetDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmployeeDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public EmployeeGetDTO save(EmployeeCreateDTO createDTO){
        Map<String, Object> params = Map.of(
                "check",createDTO.username(),
                Employee._firstName, createDTO.firstName(),
                Employee._lastName, createDTO.lastName(),
                Employee._username, createDTO.username()
        );
        new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("public")
                .withProcedureName("save_employee")
                .execute(new MapSqlParameterSource()
                        .addValue("p_first_name", createDTO.firstName())
                        .addValue("p_last_name", createDTO.lastName())
                        .addValue("p_username", createDTO.username())
                );
        return findByUsername(createDTO.username());
    }

    private EmployeeGetDTO findByUsername(String username) {
        Map<String, Object> params=Map.of(Employee._username, username);
        RowMapper<EmployeeGetDTO> employeeGetDTORowMapper = (rs, rowNum) -> {
            Date createdAt = rs.getDate(Employee._createdAt);
            return EmployeeGetDTO.of(
                    rs.getLong(Employee._id),
                    createdAt.toLocalDate().atTime(createdAt.getHours(), createdAt.getMinutes()),
                    rs.getString(Employee._firstName),
                    rs.getString(Employee._lastName),
                    rs.getString(Employee._username)
            );
        };
        return namedParameterJdbcTemplate.queryForObject("select * from employees where username=:username", params, employeeGetDTORowMapper);
    }

    public boolean existsById(Long id){
        Map<String, Object> params=Map.of(Employee._id, id);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject("select exists(select * from employees where id=:id)", params, Boolean.class));
    }
    public boolean existsByUsername(String username){
        Map<String, Object> params=Map.of(Employee._username, username);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject("select exists(select * from employees where username=:username)", params, Boolean.class));
    }
    public EmployeeGetDTO findById(Long id){
        Map<String, Object> params=Map.of(Employee._id, id);
        RowMapper<EmployeeGetDTO> employeeGetDTORowMapper = (rs, rowNum) -> EmployeeGetDTO.of(
                rs.getLong(Employee._id),
                rs.getDate(Employee._createdAt).toLocalDate().atStartOfDay(),
                rs.getString(Employee._firstName),
                rs.getString(Employee._lastName),
                rs.getString(Employee._username)
        );
        return namedParameterJdbcTemplate.queryForObject("select * from employees where id=:id", params, employeeGetDTORowMapper);
    }
    public Set<EmployeeGetDTO> findAllByUsernames(Set<String> usernames){
        Map<String, Object> params=Map.of(Employee._username+"s", usernames);
        RowMapper<EmployeeGetDTO> employeeGetDTORowMapper = (rs, rowNum) -> EmployeeGetDTO.of(
                rs.getLong(Employee._id),
                rs.getDate(Employee._createdAt).toLocalDate().atStartOfDay(),
                rs.getString(Employee._firstName),
                rs.getString(Employee._lastName),
                rs.getString(Employee._username)
        );
        return namedParameterJdbcTemplate.queryForStream("select * from employees where username in (:usernames)", params, employeeGetDTORowMapper).collect(Collectors.toSet());
    }
    public EmployeeGetDTO update(EmployeeUpdateDTO updateDTO){
        Map<String, Object> params=Map.of(
                Employee._firstName, updateDTO.firstName(),
                Employee._lastName, updateDTO.lastName(),
                Employee._id, updateDTO.id(),
                Employee._username, updateDTO.username()
        );
        int updated = namedParameterJdbcTemplate.update("update employees set first_name=:first_name, last_name=:last_name, username=:username where id=:id", params);
        if (updated==0) {
            throw new RuntimeException("user_not_found");
        }
        return findById(updateDTO.id());
    }
    public void deleteById(Long id){
        Map<String, Object> params=Map.of(Employee._id, id);
        namedParameterJdbcTemplate.update("delete from employees where id=:id", params);
    }
    /**
     * in this method, multiple insert queries will be generated like the following this
     *
     * insert into employees(first_name, last_name, username, created_at, deleted, active) values('John', 'Jackson', 'john123', now(), false, true);
     * insert into employees(first_name, last_name, username, created_at, deleted, active) values('Jack', 'Jackson2', 'jack123', now(), false, true);
     * insert into employees(first_name, last_name, username, created_at, deleted, active) values('Alisa', 'Alimovna', 'alis123', now(), false, true);
     * insert into employees(first_name, last_name, username, created_at, deleted, active) values('Johnaton', 'Jackson', 'john12', now(), false, true);
     *
     * insert queries count depends on list size
    * */
    // save list with jdbc batch update
    public Set<EmployeeGetDTO> saveAll(Set<EmployeeCreateDTO> createDTOSet) {
        String sql = "insert into employees(first_name, last_name, username, created_at, deleted, active) values(:first_name, :last_name, :username, now(), false, true)";
        Map[] mapArray = createDTOSet.parallelStream()
                .map(createDTO -> Map.of(
                        Employee._firstName, createDTO.firstName(),
                        Employee._lastName, createDTO.lastName(),
                        Employee._username, createDTO.username()
                )).toArray(Map[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, mapArray);
        return Set.of();
    }

}
