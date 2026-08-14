package com.library.personnel.service;

import com.library.personnel.dto.response.ImportResultResponse;
import com.library.personnel.entity.Employee;
import com.library.personnel.enums.EmployeeStatus;
import com.library.personnel.enums.WhitelistType;
import com.library.personnel.exception.BusinessException;
import com.library.personnel.repository.EmployeeRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final WhitelistService whitelistService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ImportService(EmployeeRepository employeeRepository,
                         EmployeeService employeeService,
                         WhitelistService whitelistService) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.whitelistService = whitelistService;
    }

    @Transactional
    public ImportResultResponse importEmployees(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new BusinessException(40001, "File name is required");
        }

        ImportResultResponse result = new ImportResultResponse();

        try {
            List<String[]> rows;
            if (fileName.endsWith(".csv")) {
                rows = parseCsv(file);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                rows = parseExcel(file);
            } else {
                throw new BusinessException(40001, "Unsupported file format. Supported: CSV, .xlsx, .xls");
            }

            // Skip header row, process data rows
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                int rowNum = i + 1; // 1-based for display

                try {
                    if (row.length < 4) {
                        result.addFailure(rowNum, "Insufficient columns. Expected at least: employeeId, name, department, position");
                        continue;
                    }

                    String employeeId = row[0].trim();
                    String name = row[1].trim();
                    String department = row[2].trim();
                    String position = row[3].trim();

                    if (employeeId.isBlank()) {
                        result.addFailure(rowNum, "Missing required field: employeeId");
                        continue;
                    }
                    if (name.isBlank()) {
                        result.addFailure(rowNum, "Missing required field: name");
                        continue;
                    }
                    if (department.isBlank()) {
                        result.addFailure(rowNum, "Missing required field: department");
                        continue;
                    }
                    if (position.isBlank()) {
                        result.addFailure(rowNum, "Missing required field: position");
                        continue;
                    }

                    // Validate against IMPORT whitelist
                    if (!whitelistService.checkWhitelist(employeeId, WhitelistType.IMPORT)) {
                        result.addFailure(rowNum, "Not in import whitelist");
                        continue;
                    }

                    // Check if employee already exists
                    if (employeeRepository.existsByEmployeeId(employeeId)) {
                        result.addFailure(rowNum, "Employee already exists: " + employeeId);
                        continue;
                    }

                    Employee employee = new Employee();
                    employee.setEmployeeId(employeeId);
                    employee.setName(name);
                    employee.setDepartment(department);
                    employee.setPosition(position);

                    // Optional fields
                    if (row.length > 4 && !row[4].trim().isBlank()) {
                        employee.setPhone(row[4].trim());
                    }
                    if (row.length > 5 && !row[5].trim().isBlank()) {
                        employee.setEmail(row[5].trim());
                    }
                    if (row.length > 6 && !row[6].trim().isBlank()) {
                        try {
                            employee.setHireDate(LocalDate.parse(row[6].trim(), DATE_FORMATTER));
                        } catch (DateTimeParseException e) {
                            result.addFailure(rowNum, "Invalid date format: " + row[6].trim() + " (expected yyyy-MM-dd)");
                            continue;
                        }
                    }
                    employee.setStatus(EmployeeStatus.ACTIVE);
                    employee.setDeleted(false);

                    employeeRepository.save(employee);
                    result.incrementSuccess();

                } catch (Exception e) {
                    result.addFailure(rowNum, e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new BusinessException(40002, "Failed to read file: " + e.getMessage());
        }

        return result;
    }

    public byte[] generateTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Import Template");
            String[] headers = {"employeeId", "name", "department", "position", "phone", "email", "hireDate"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Add a sample row
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("EMP000001");
            sampleRow.createCell(1).setCellValue("John Doe");
            sampleRow.createCell(2).setCellValue("Engineering");
            sampleRow.createCell(3).setCellValue("Developer");
            sampleRow.createCell(4).setCellValue("13800138000");
            sampleRow.createCell(5).setCellValue("john@example.com");
            sampleRow.createCell(6).setCellValue("2025-01-01");

            try (var baos = new java.io.ByteArrayOutputStream()) {
                workbook.write(baos);
                return baos.toByteArray();
            }
        } catch (IOException e) {
            throw new BusinessException(50000, "Failed to generate template: " + e.getMessage());
        }
    }

    private List<String[]> parseCsv(MultipartFile file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                rows.add(parseCsvLine(line));
            }
        }
        return rows;
    }

    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString().trim());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString().trim());
        return fields.toArray(new String[0]);
    }

    private List<String[]> parseExcel(MultipartFile file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                List<String> cells = new ArrayList<>();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    if (cell == null) {
                        cells.add("");
                    } else {
                        cells.add(getCellValueAsString(cell));
                    }
                }
                rows.add(cells.toArray(new String[0]));
            }
        }
        return rows;
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    yield String.valueOf((long) val);
                }
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    try {
                        yield cell.getStringCellValue();
                    } catch (Exception e2) {
                        yield "";
                    }
                }
            }
            default -> "";
        };
    }
}