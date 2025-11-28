package org.surest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "role")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // Optimistic locking version field using timestamp
    @Version
    private Instant lastUpdated;

    // Getters and setters
}
