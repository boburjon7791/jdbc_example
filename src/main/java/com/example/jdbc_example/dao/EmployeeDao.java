package com.example.jdbc_example.dao;

import com.example.jdbc_example.model.base.Employee;
import com.example.jdbc_example.model.request.EmployeeCreateDTO;
import com.example.jdbc_example.model.request.EmployeeUpdateDTO;
import com.example.jdbc_example.model.response.EmployeeGetDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmployeeDao {
    public static final String findById= """
                do
                $$
                begin
                if not select exists(select id from employees where id=:id)
                raise exception '+user_not_found*';
                end if;
                select *
                from employees
                where id=:id
                end
                $$;
                """;

    public static final String insertOne= """
                do
                $$
                declare exists_name boolean:=null;
                begin
                select exists(select id from employees where username=:username) into exists_name;
                if exists_name then
                    raise exception 'user_already_exists';
                else
                    insert into employees(first_name, last_name, username, created_at, deleted, active)
                    values(:first_name, :last_name, :username, now(), false, true);
                end if;
                commit;
                end
                $$;
                """;
    public static final String existsById= """
                select exists(
                    select id
                    from employees
                    where id=:id
                )
                """;
    public static final String findAllByUsernames= """
                select *
                from employees
                where username = in (:usernames)
                """;
    public static final String existsByUsername= """
                select exists(
                    select id
                    from employees
                    where username=:username
                )
                """;
    public static final String updateOne= """
                do
                $$
                begin
                if select exists(select id from employees where username=:username and id<>:id)
                raise exception '+user_already_exists*';
                end if;
                update employees set first_name=:first_name, last_name=:last_name, username=:username where id=:id
                end
                $$;
                """;
    public static final String deleteById= """
                delete from employees where id=:id
                """;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    public static final String findByUsername= """
                do
                $$
                declare exist_name boolean;
                begin
                select exists(select id from employees where username=:username) into exist_name;
                if exist_name then raise exception '+user_not_found*';
                end if;
                select *
                from employees
                where username=:username
                end
                $$;
            """;

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
//        int inserted = namedParameterJdbcTemplate.update(insertOne, params);
        /*if (inserted==0) {
            throw new RuntimeException("Error");
        }*/
        /*namedParameterJdbcTemplate.update("CALL save_employee(:firstName, :lastName, :username);", Map.of(
                "firstName", createDTO.firstName(),
                "lastName", createDTO.lastName(),
                "username", createDTO.username()
        ));*/
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
        return namedParameterJdbcTemplate.queryForObject(findByUsername, params, employeeGetDTORowMapper);
    }

    public boolean existsById(Long id){
        Map<String, Object> params=Map.of(Employee._id, id);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(existsById, params, Boolean.class));
    }
    public boolean existsByUsername(String username){
        Map<String, Object> params=Map.of(Employee._username, username);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(existsByUsername, params, Boolean.class));
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
        return namedParameterJdbcTemplate.queryForObject(findById, params, employeeGetDTORowMapper);
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
        return namedParameterJdbcTemplate.queryForStream(findAllByUsernames, params, employeeGetDTORowMapper).collect(Collectors.toSet());
    }
    public EmployeeGetDTO update(EmployeeUpdateDTO updateDTO){
        Map<String, Object> params=Map.of(
                Employee._firstName, updateDTO.firstName(),
                Employee._lastName, updateDTO.lastName(),
                Employee._id, updateDTO.id(),
                Employee._username, updateDTO.username()
        );
        int updated = namedParameterJdbcTemplate.update(updateOne, params);
        if (updated==0) {
            throw new RuntimeException("user_not_found");
        }
        return findById(updateDTO.id());
    }
    public void deleteById(Long id){
        Map<String, Object> params=Map.of(Employee._id, id);
        namedParameterJdbcTemplate.update(deleteById, params);
    }

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
