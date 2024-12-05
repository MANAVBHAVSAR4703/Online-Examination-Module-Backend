package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "monitor_images")
public class MonitorImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private User user;

    @ManyToOne
    @JoinColumn(name = "exam_id", referencedColumnName = "id")
    private Exam exam;

    @Column(name = "capture_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date captureTime = new Date();

    @Lob
    @Column(name = "image", nullable = false)
    private byte[] image;

}
