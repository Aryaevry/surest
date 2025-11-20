package org.surest.controller.surest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.surest.dto.MemberDto;
import org.surest.entity.Member;
import org.surest.serviceimpl.MemberServiceImpl;

import java.util.UUID;

/**
 * REST controller for managing members.
 * <p>
 * Provides endpoints to create, read, update, and delete members.
 * Supports pagination and optional filtering for retrieving member lists.
 * Access is restricted using {@link PreAuthorize} annotations for role-based security.
 */
@RestController
@RequestMapping("/members")
@Validated
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberServiceImpl memberServiceImpl;

    /**
     * Constructs a MemberController with the specified {@link MemberServiceImpl}.
     *
     * @param memberServiceImpl the service that contains business logic for members
     */
    public MemberController(MemberServiceImpl memberServiceImpl) {
        this.memberServiceImpl = memberServiceImpl;
    }

    /**
     * Retrieves a paginated list of members, optionally filtered by first name or last name.
     * <p>
     * Accessible by users with the "USER" or "ADMIN" roles.
     *
     * @param page      the page number to retrieve (default: 0)
     * @param size      the number of records per page (default: 10)
     * @param sort      optional sorting criteria
     * @param firstName optional first name filter
     * @param lastName  optional last name filter
     * @return a {@link Page} of {@link Member} matching the criteria
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<Member> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName
    ) {
        logger.info("Fetching members list: page={}, size={}, sort={}, firstName={}, lastName={}",
                page, size, sort, firstName, lastName);
        return memberServiceImpl.getMembers(page, size, sort, firstName, lastName);
    }

    /**
     * Retrieves a member by their unique ID.
     * <p>
     * Accessible by users with the "USER" or "ADMIN" roles.
     * Throws {@link RuntimeException} (e.g., UserNotFoundException) if the member does not exist.
     *
     * @param id the UUID of the member
     * @return the {@link Member} with the specified ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Member getMemberById(@PathVariable UUID id) {
        logger.info("Fetching member with ID: {}", id);
        return memberServiceImpl.getMemberById(id);
    }

    /**
     * Creates a new member.
     * <p>
     * Accessible only by users with the "ADMIN" role.
     *
     * @param memberDto the DTO containing member details
     * @return a {@link ResponseEntity} with the created {@link Member} and HTTP status 201 (Created)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Member> createMember(@Valid @RequestBody MemberDto memberDto) {
        logger.info("Creating new member: {} {}", memberDto.getFirstName(), memberDto.getLastName());

        Member member = Member.builder()
                .firstName(memberDto.getFirstName())
                .lastName(memberDto.getLastName())
                .dateOfBirth(memberDto.getDateOfBirth())
                .email(memberDto.getEmail())
                .build();

        Member saved = memberServiceImpl.createMember(member);
        logger.info("Member created successfully with ID: {}", saved.getId());

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Updates an existing member by ID.
     * <p>
     * Accessible only by users with the "ADMIN" role.
     * Throws {@link RuntimeException} (e.g., UserNotFoundException) if the member does not exist.
     *
     * @param id        the UUID of the member to update
     * @param memberDto the DTO containing updated member details
     * @return the updated {@link Member}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Member updateMember(@PathVariable UUID id, @Valid @RequestBody MemberDto memberDto) {
        logger.info("Updating member with ID: {}", id);

        Member memberDetails = Member.builder()
                .firstName(memberDto.getFirstName())
                .lastName(memberDto.getLastName())
                .dateOfBirth(memberDto.getDateOfBirth())
                .email(memberDto.getEmail())
                .build();

        Member updated = memberServiceImpl.updateMember(id, memberDetails);
        logger.info("Member updated successfully: {}", updated.getId());

        return updated;
    }

    /**
     * Deletes a member by ID.
     * <p>
     * Accessible only by users with the "ADMIN" role.
     * Throws {@link RuntimeException} (e.g., UserNotFoundException) if the member does not exist.
     *
     * @param id the UUID of the member to delete
     * @return a {@link ResponseEntity} with no content (HTTP 204) if deletion is successful
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        logger.info("Deleting member with ID: {}", id);
        memberServiceImpl.deleteMember(id);
        logger.info("Member deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
