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

    @Column(name = "refresh_token")
    private String refreshToken;

    public int getUId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}


//    @Column(name = "birth_date") // Maps to PostgreSQL DATE column
//    private LocalDate date;