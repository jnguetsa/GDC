package com.example.demo.GDU.controllers;

import com.example.demo.GDU.dto.employe.EmployeRequest;
import com.example.demo.GDU.dto.employe.EmployeResponse;
import com.example.demo.GDU.services.serviceImpl.EmployeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployeService employerService;

    @GetMapping("/{id}")
    public ResponseEntity<EmployeResponse> getEmployer(@PathVariable Long id) {
        return ResponseEntity.ok(employerService.getEmployer(id));
    }

    @PostMapping
    public ResponseEntity<EmployeResponse> addEmployer(@Valid @RequestBody EmployeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employerService.addEmployer(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployer(@PathVariable Long id) {
        employerService.deleteEmployer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{employerId}/roles/{roleId}")
    public ResponseEntity<EmployeResponse> addRoleToEmployer(
            @PathVariable Long employerId,
            @PathVariable Long roleId) {
        return ResponseEntity.ok(employerService.addRoleToEmployer(employerId, roleId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeResponse> updateEmployer(
            @PathVariable Long id,
            @Valid @RequestBody EmployeRequest request) {
        return ResponseEntity.ok(employerService.updateEmployer(id, request));
    }
@GetMapping("/getAllEmployers")
    public ResponseEntity<List<EmployeResponse>> getAllEmployers() {
        return ResponseEntity.ok(employerService.getAllEmployers());
    }
}