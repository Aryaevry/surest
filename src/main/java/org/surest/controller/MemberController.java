package org.surest.controller;

import org.surest.dto.MemberDto;
import org.surest.entity.Member;
import org.surest.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/members")
@Validated
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Get a paginated list of members, optionally filtered by first name or last name.
     * Accessible by USER or ADMIN roles.
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
        return memberService.getMembers(page, size, sort, firstName, lastName);
    }

    /**
     * Get a member by ID.
     * Throws UserNotFoundException if the member does not exist.
     * Accessible by USER or ADMIN roles.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Member getMemberById(@PathVariable UUID id) {
        return memberService.getMemberById(id);
    }

    /**
     * Create a new member.
     * Accessible only by ADMIN role.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Member> createMember(@Valid @RequestBody MemberDto memberDto) {
        Member member = Member.builder()
                .firstName(memberDto.getFirstName())
                .lastName(memberDto.getLastName())
                .dateOfBirth(memberDto.getDateOfBirth())
                .email(memberDto.getEmail())
                .build();
        Member saved = memberService.createMember(member);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Update an existing member by ID.
     * Throws UserNotFoundException if the member does not exist.
     * Accessible only by ADMIN role.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Member updateMember(@PathVariable UUID id, @Valid @RequestBody MemberDto memberDto) {
        Member memberDetails = Member.builder()
                .firstName(memberDto.getFirstName())
                .lastName(memberDto.getLastName())
                .dateOfBirth(memberDto.getDateOfBirth())
                .email(memberDto.getEmail())
                .build();
        return memberService.updateMember(id, memberDetails);
    }

    /**
     * Delete a member by ID.
     * Throws UserNotFoundException if the member does not exist.
     * Accessible only by ADMIN role.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
