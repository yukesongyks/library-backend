package com.library.personnel.service;

import com.library.personnel.dto.response.ImportResultResponse;
import com.library.personnel.entity.Employee;
import com.library.personnel.enums.EmployeeStatus;
import com.library.personnel.enums.WhitelistType;
import com.library.personnel.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private WhitelistService whitelistService;

    private ImportService importService;

    @BeforeEach
    void setUp() {
        importService = new ImportService(employeeRepository, employeeService, whitelistService);
    }

    @Test
    void testImportCsvSuccess() throws IOException {
        String csv = "employeeId,name,department,position,phone,email,hireDate\n" +
                     "EMP000001,John Doe,Engineering,Developer,13800138000,john@example.com,2025-01-01\n" +
                     "EMP000002,Jane Smith,HR,Manager,13900139000,jane@example.com,2025-02-01";
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        when(whitelistService.checkWhitelist(anyString(), eq(WhitelistType.IMPORT))).thenReturn(true);
        when(employeeRepository.existsByEmployeeId(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportResultResponse result = importService.importEmployees(file);

        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        verify(employeeRepository, times(2)).save(any(Employee.class));
    }

    @Test
    void testImportCsvNotInWhitelist() throws IOException {
        String csv = "employeeId,name,department,position\n" +
                     "EMP00001,John Doe,Engineering,Developer";
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        when(whitelistService.checkWhitelist("EMP00001", WhitelistType.IMPORT)).thenReturn(false);

        ImportResultResponse result = importService.importEmployees(file);

        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertEquals("Not in import whitelist", result.getFailures().get(0).getError());
    }

    @Test
    void testImportCsvMissingFields() throws IOException {
        String csv = "employeeId,name,department,position\n" +
                     "EMP00001,,Engineering,";
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        when(whitelistService.checkWhitelist("EMP00001", WhitelistType.IMPORT)).thenReturn(true);

        ImportResultResponse result = importService.importEmployees(file);

        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertTrue(result.getFailures().get(0).getError().contains("Missing required field"));
    }

    @Test
    void testImportCsvMixedResults() throws IOException {
        String csv = "employeeId,name,department,position\n" +
                     "EMP00001,John Doe,Engineering,Developer\n" +
                     "EMP00002,Jane Smith,HR,Manager\n" +
                     "EMP00003,Bob,Finance,Analyst";
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        when(whitelistService.checkWhitelist("EMP00001", WhitelistType.IMPORT)).thenReturn(true);
        when(whitelistService.checkWhitelist("EMP00002", WhitelistType.IMPORT)).thenReturn(false);
        when(whitelistService.checkWhitelist("EMP00003", WhitelistType.IMPORT)).thenReturn(true);
        when(employeeRepository.existsByEmployeeId("EMP00001")).thenReturn(false);
        when(employeeRepository.existsByEmployeeId("EMP00003")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportResultResponse result = importService.importEmployees(file);

        assertEquals(2, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertEquals("Not in import whitelist", result.getFailures().get(0).getError());
    }

    @Test
    void testGenerateTemplate() {
        byte[] template = importService.generateTemplate();
        assertNotNull(template);
        assertTrue(template.length > 0);
    }

    @Test
    void testUnsupportedFileFormat() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());

        assertThrows(com.library.personnel.exception.BusinessException.class,
                () -> importService.importEmployees(file));
    }
}