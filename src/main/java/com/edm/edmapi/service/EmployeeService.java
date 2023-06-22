package com.edm.edmapi.service;

import com.edm.model.dto.EmployeeDto;
import com.edm.model.dto.EmployeeViewDto;
import com.edm.model.dto.Role;
import com.google.protobuf.Empty;
import gft.edm.grpc.disposition.*;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final EmployeeServiceGrpc.EmployeeServiceBlockingStub employeeServiceBlockingStub;

    public void deleteByEmployeeCode(String employeeCode) {
        employeeServiceBlockingStub.deleteEmployee(DeleteEmployeeRequest.newBuilder().setId(employeeCode).build());
    }

    public List<EmployeeViewDto> employeesGet() {
        Employees employees = employeeServiceBlockingStub.getAllEmployees(Empty.getDefaultInstance());
        return mapEmployeesToViewDtos(employees);
    }

    public EmployeeViewDto employeesPost(EmployeeDto employeeDto) {
        Employee employee = employeeServiceBlockingStub.createEmployee(CreateEmployeeRequest.newBuilder().setEmployee(employeeToGrpcFormat(employeeDto)).build());
        return mapEmployeeToViewDto(employee);
    }

    public EmployeeViewDto getByEmployeeCode(String employeeCode) {
        Employee employee = employeeServiceBlockingStub.getEmployeeByEmployeeCode(GetEmployeeByEmployeeCodeRequest.newBuilder().setEmployeeCode(employeeCode).build());
        return mapEmployeeToViewDto(employee);

    }

    public EmployeeViewDto updateByEmployeeCode(String employeeCode, EmployeeDto employeeDto) {
        Employee employee = employeeServiceBlockingStub.updateEmployee(UpdateEmployeeRequest.newBuilder().setEmployeeCode(employeeCode).setEmployee(employeeToGrpcFormat(employeeDto)).build());
        return mapEmployeeToViewDto(employee);
    }

    private Employees employeeViewsToGrpcFormat(List<EmployeeDto> employeeDtos) {
        List<Employee> collect = employeeDtos.stream()
                .map(this::employeeToGrpcFormat)
                .toList();
        return Employees.newBuilder().addAllEmployee(collect).build();
    }

    private Employee employeeToGrpcFormat(EmployeeDto employeeDto) {
        return Employee.newBuilder()
                .setFirstName(employeeDto.getFirstName())
                .setLastName(employeeDto.getLastName())
                .setAddress(employeeDto.getAddress())
                .setBirthday(employeeDto.getBirthday().toString())
                .setEmail(employeeDto.getEmail())
                .setEmployeeCode(employeeDto.getEmployeeCode())
                .setPhoneNumber(employeeDto.getPhoneNumber())
                .setRole(employeeDto.getRole().name())
                .setPassword(employeeDto.getPassword())
                .build();
    }

    private EmployeeDto mapEmployeeToDto(Employee employee) {
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MMM-dd");
        LocalDate localDate = timeFormatter.parseLocalDate(employee.getBirthday());
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName(employee.getFirstName());
        employeeDto.setLastName(employee.getLastName());
        employeeDto.setAddress(employee.getAddress());
        employeeDto.setBirthday(java.time.LocalDate.of(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()));
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setEmployeeCode(employee.getEmployeeCode());
        employeeDto.setPhoneNumber(employeeDto.getPhoneNumber());
        employeeDto.setRole(Role.valueOf(employee.getRole()));
        employeeDto.setPassword(employee.getPassword());
        return employeeDto;
    }

    private EmployeeViewDto mapEmployeeToViewDto(Employee employee) {
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MMM-dd");
        LocalDate localDate = timeFormatter.parseLocalDate(employee.getBirthday());
        EmployeeViewDto employeeViewDto = new EmployeeViewDto();
        employeeViewDto.setFirstName(employee.getFirstName());
        employeeViewDto.setLastName(employee.getLastName());
        employeeViewDto.setAddress(employee.getAddress());
        employeeViewDto.setBirthday(java.time.LocalDate.of(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()));
        employeeViewDto.setEmail(employee.getEmail());
        employeeViewDto.setEmployeeCode(employee.getEmployeeCode());
        employeeViewDto.setPhoneNumber(employeeViewDto.getPhoneNumber());
        employeeViewDto.setRole(Role.valueOf(employee.getRole()));
        return employeeViewDto;
    }

    private List<EmployeeViewDto> mapEmployeesToViewDtos(Employees employees) {
        return employees.getEmployeeList()
                .stream()
                .map(this::mapEmployeeToViewDto)
                .toList();
    }

}
