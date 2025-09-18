package com.micorregimiento.micorregimiento.TestConection.entitys;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_connection")
@Data
public class TestConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}