package com.tehacko.backend_java.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")  // Escaping the reserved keyword "user"
public class User {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uId;

    private String email;
    private String password;

    @Column(name = "is_admin", nullable = false) // Ensures correct DB column name
    private boolean isAdmin;

}

//    @Column(name = "birth_date") // Maps to PostgreSQL DATE column
//    private LocalDate date;