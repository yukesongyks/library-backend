package com.library.personnel.service;

import com.library.personnel.dto.request.EmployeeRequest;
import com.library.personnel.dto.response.EmployeeResponse;
import com.library.personnel.entity.Employee;
import com.library.personnel.enums.EmployeeStatus;
import com.library.personnel.exception.BusinessException;
import com.library.personnel.exception.ResourceNotFoundException;
import com.library.personnel.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Page<EmployeeResponse> listEmployees(String keyword, Pageable pageable) {
        Specification<Employee> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("deleted")));
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("department")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return employeeRepository.findAll(spec, pageable).map(EmployeeResponse::fromEntity);
    }

    public EmployeeResponse getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        if (Boolean.TRUE.equals(employee.getDeleted())) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        return EmployeeResponse.fromEntity(employee);
    }

    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setEmployeeId(generateEmployeeId());
        employee.setName(request.getName());
        employee.setDepartment(request.getDepartment());
        employee.setPosition(request.getPosition());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setHireDate(request.getHireDate());
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDeleted(false);
        employee = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        if (Boolean.TRUE.equals(employee.getDeleted())) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employee.setName(request.getName());
        employee.setDepartment(request.getDepartment());
        employee.setPosition(request.getPosition());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setHireDate(request.getHireDate());
        employee = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        if (Boolean.TRUE.equals(employee.getDeleted())) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    private String generateEmployeeId() {
        String maxId = employeeRepository.findMaxEmployeeId().orElse("EMP000000");
        int num = Integer.parseInt(maxId.substring(3)) + 1;
        return String.format("EMP%06d", num);
    }

    public long countByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeIdAndDeletedFalse(employeeId).isPresent() ? 1 : 0;
    }
}