package org.surest.controller.surest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.surest.dto.MemberDto;
import org.surest.dto.MemberResponseDto;
import org.surest.entity.Member;
import org.surest.service.MemberService;

import java.util.UUID;

/**
 * REST controller for managing members.
 * <p>
 * Provides endpoints to create, read, update, and delete members.
 * Supports pagination and optional filtering for retrieving member lists.
 * Access is restricted using {@link PreAuthorize} annotations for role-based security.
 */
@RestController
@RequestMapping("/api/v1/members")
@Validated
@Slf4j
@Tag(
        name = "Member API",
        description = "Endpoints for managing members, including creation, update, deletion, and retrieval."
)
public class MemberController {


    private final MemberService memberService;

    /**
     * Constructs a MemberController with the specified {@link MemberService}.
     *
     * @param memberService the service that contains business logic for members
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
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
    @Operation(
            summary = "Get list of members",
            description = "Returns paginated and optionally filtered list of members. Accessible by USER and ADMIN."
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<Member> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName
    ) {
        log.info("Fetching members list: page={}, size={}, sort={}, firstName={}, lastName={}",
                page, size, sort, firstName, lastName);
        return memberService.getMembers(page, size, sort, firstName, lastName);
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
    @Operation(
            summary = "Get member by ID",
            description = "Retrieves a member using their UUID. Throws an exception if not found."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Member> getMemberById(@PathVariable UUID id) {
        log.info("Fetching member with ID: {}", id);
        Member member = memberService.getMemberById(id);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    /**
     * Creates a new member.
     * <p>
     * Accessible only by users with the "ADMIN" role.
     *
     * @param memberDto the DTO containing member details
     * @return a {@link ResponseEntity} with the created {@link Member} and HTTP status 201 (Created)
     */
    @Operation(
            summary = "Create a new member",
            description = "Creates a new member. Accessible only to ADMIN users."
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Member> createMember(@Valid @RequestBody MemberDto memberDto) {
        log.info("Creating new member: {} {}", memberDto.getFirstName(), memberDto.getLastName());

        Member member = Member.builder()
                .firstName(memberDto.getFirstName())
                .lastName(memberDto.getLastName())
                .dateOfBirth(memberDto.getDateOfBirth())
                .email(memberDto.getEmail())
                .build();

        Member saved = memberService.createMember(member);
        log.info("Member created successfully with ID: {}", saved.getId());

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
    @Operation(
            summary = "Update an existing member",
            description = "Updates the details of an existing member by UUID. Only ADMIN users can access this."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity <MemberResponseDto> updateMember(@PathVariable UUID id, @Valid @RequestBody MemberDto memberDto) {
        log.info("Updating member with ID: {}", id);

        Member memberDetails = Member.builder()
                .firstName(memberDto.getFirstName())
                .lastName(memberDto.getLastName())
                .dateOfBirth(memberDto.getDateOfBirth())
                .email(memberDto.getEmail())
                .build();

        MemberResponseDto updated = memberService.updateMember(id, memberDetails);
        log.info("Member updated successfully: {}", updated.id());

        return new ResponseEntity<>(updated,HttpStatus.OK);
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
    @Operation(
            summary = "Delete a member",
            description = "Deletes a member using their UUID. Only ADMIN users can perform this action."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        log.info("Deleting member with ID: {}", id);
        memberService.deleteMember(id);
        log.info("Member deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
