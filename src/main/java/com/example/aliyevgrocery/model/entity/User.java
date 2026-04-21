package com.example.aliyevgrocery.model.entity;

import com.example.aliyevgrocery.Enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotNull
    @Size(min = 3, max = 15)
    String username;
    @Size(min = 8)
    String password;
    String email;
    String number;
    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updated_at;
    @Enumerated(EnumType.STRING)
    Roles role;
    @Column(name = "is_active")
    @Builder.Default
    Boolean isActive = true;
}
