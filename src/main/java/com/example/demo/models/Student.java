package com.example.demo.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long enrollNo;
    private String college;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

}
