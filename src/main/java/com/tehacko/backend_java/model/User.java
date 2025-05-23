package com.tehacko.backend_java.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_created_at")
    private LocalDateTime resetTokenCreatedAt;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenCreatedAt() {
        return resetTokenCreatedAt;
    }

    public void setResetTokenCreatedAt(LocalDateTime resetTokenCreatedAt) {
        this.resetTokenCreatedAt = resetTokenCreatedAt;
    }
}


//    @Column(name = "birth_date") // Maps to PostgreSQL DATE column
//    private LocalDate date;