package com.example.demo.services;

import com.example.demo.Dto.StudentDto;
import com.example.demo.models.Student;
import com.example.demo.models.User;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(StudentDto studentDto) {
        if (userRepository.existsByEmail(studentDto.getEmail())) {
            throw new IllegalArgumentException("Student with email " + studentDto.getEmail() + " already exists.");
        }
        // Map StudentDto data to User entity
        User user = new User();
        user.setEmail(studentDto.getEmail());
        user.setFullName(studentDto.getFullName());
        user.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        // Assign role automatically
        user.setRole("STUDENT");
        System.out.println(user);
        // Create Student entity and set specific properties if the role is STUDENT
        if ("STUDENT".equals(user.getRole())) {
            Student student = new Student();
            student.setEnrollNo(studentDto.getEnrollNo());
            student.setCollege(studentDto.getCollege());
            student.setUser(user);  // Link User to Student
            user.setStudent(student);  // Link Student to User
        }

        // Save the user and the student (if present) due to cascading
        return userRepository.save(user);
    }

    public List<Student> getStudentsFromDb(){
        return studentRepository.findAll();
    }

}
