package com.library.personnel.service;

import com.library.personnel.dto.request.EmployeeRequest;
import com.library.personnel.dto.response.EmployeeResponse;
import com.library.personnel.entity.Employee;
import com.library.personnel.enums.EmployeeStatus;
import com.library.personnel.exception.ResourceNotFoundException;
import com.library.personnel.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository);
    }

    @Test
    void testListEmployees() {
        Employee employee = createTestEmployee();
        Page<Employee> page = new PageImpl<>(List.of(employee));
        when(employeeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<EmployeeResponse> result = employeeService.listEmployees("test", PageRequest.of(0, 20));

        assertEquals(1, result.getTotalElements());
        assertEquals("EMP000001", result.getContent().get(0).getEmployeeId());
        verify(employeeRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetEmployee() {
        Employee employee = createTestEmployee();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponse result = employeeService.getEmployee(1L);

        assertEquals("EMP000001", result.getEmployeeId());
        assertEquals("John", result.getName());
    }

    @Test
    void testGetEmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployee(999L));
    }

    @Test
    void testCreateEmployee() {
        EmployeeRequest request = new EmployeeRequest();
        request.setName("Jane");
        request.setDepartment("Engineering");
        request.setPosition("Developer");
        request.setHireDate(LocalDate.of(2025, 1, 1));

        Employee savedEmployee = createTestEmployee();
        savedEmployee.setName("Jane");
        savedEmployee.setEmployeeId("EMP000001");

        when(employeeRepository.findMaxEmployeeId()).thenReturn(Optional.of("EMP000000"));
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        EmployeeResponse result = employeeService.createEmployee(request);

        assertEquals("Jane", result.getName());
        assertEquals("EMP000001", result.getEmployeeId());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee() {
        Employee employee = createTestEmployee();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeRequest request = new EmployeeRequest();
        request.setName("Updated");
        request.setDepartment("HR");
        request.setPosition("Manager");
        request.setHireDate(LocalDate.of(2025, 1, 1));

        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse result = employeeService.updateEmployee(1L, request);

        assertEquals("Updated", result.getName());
        assertEquals("HR", result.getDepartment());
    }

    @Test
    void testDeleteEmployee() {
        Employee employee = createTestEmployee();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.deleteEmployee(1L);

        assertTrue(employee.getDeleted());
        verify(employeeRepository).save(employee);
    }

    @Test
    void testDeleteEmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(999L));
    }

    @Test
    void testEmployeeIdGeneration() {
        when(employeeRepository.findMaxEmployeeId()).thenReturn(Optional.of("EMP000005"));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            emp.setId(1L);
            return emp;
        });

        EmployeeRequest request = new EmployeeRequest();
        request.setName("Test");
        request.setDepartment("Dept");
        request.setPosition("Pos");
        request.setHireDate(LocalDate.of(2025, 1, 1));

        EmployeeResponse result = employeeService.createEmployee(request);

        assertNotNull(result);
        verify(employeeRepository).save(argThat(emp -> "EMP000006".equals(emp.getEmployeeId())));
    }

    private Employee createTestEmployee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeId("EMP000001");
        employee.setName("John");
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");
        employee.setPhone("13800138000");
        employee.setEmail("john@example.com");
        employee.setHireDate(LocalDate.of(2025, 1, 1));
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDeleted(false);
        return employee;
    }
}