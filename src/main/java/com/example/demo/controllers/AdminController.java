package com.example.demo.controllers;

import com.example.demo.Dto.StudentDto;
import com.example.demo.models.Student;
import com.example.demo.models.User;
import com.example.demo.responses.RegisterResponse;
import com.example.demo.responses.StudentResponse;
import com.example.demo.services.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    AdminController(AdminService adminService){
        this.adminService=adminService;
    }

    @GetMapping("/hello")
    public String adminEndpoint(){
        return "Hello, Admin";
    }

    @PostMapping("/createStudent")
    public ResponseEntity<?> createStudent(@Valid @RequestBody StudentDto studentDto) {
        System.out.println("Creating Student");
        try {
            User createdUser = adminService.createUser(studentDto);
            Student student = createdUser.getStudent();
            StudentResponse studentResponse = new StudentResponse(
                    createdUser.getEmail(),
                    student.getUser().getUsername(), // Assuming username is the full name
                    student.getEnrollNo(),
                    student.getCollege()
            );
            return ResponseEntity.ok(new RegisterResponse<>(true, "Student created successfully",studentResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating student");
        }
    }

}
