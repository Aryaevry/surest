package org.surest.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a response view of a {@link org.surest.entity.Member}.
 * <p>
 * This DTO is used for returning member data to API consumers while ensuring that
 * only publicly safe and necessary fields are exposed. It mirrors the structure of
 * the Member entity but is immutable and serialization-friendly.
 * <p>
 * Fields include personal information, timestamp metadata, and optimistic locking data.
 */
public record MemberResponseDto(

        /**
         * Unique identifier of the member, represented as a UUID.
         */
        UUID id,

        /**
         * First name of the member. Required and non-null.
         */
        String firstName,

        /**
         * Last name of the member. Required and non-null.
         */
        String lastName,

        /**
         * Member's date of birth.
         */
        LocalDate dateOfBirth,

        /**
         * Email address of the member. Should be unique and valid.
         */
        String email,

        /**
         * Timestamp indicating when the member record was created in the system.
         * Automatically populated during persistence.
         */
        LocalDateTime createdAt,

        /**
         * Timestamp indicating when the member record was last updated.
         * Automatically updated on each modification.
         */
        LocalDateTime updatedAt,

        /**
         * Optimistic locking timestamp used to ensure safe concurrent updates.
         */
        Instant lastUpdated
)  implements java.io.Serializable {}
