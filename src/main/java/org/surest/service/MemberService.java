package org.surest.service;

import org.surest.entity.Member;
import org.surest.exception.UserNotFoundException;
import org.surest.repository.MemberRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Get a paginated list of members, optionally filtered by first name or last name.
     */
    public Page<Member> getMembers(int page, int size, String sort, String firstName, String lastName) {
        Sort sortObj = Sort.by("id");
        if (sort != null && !sort.isBlank()) {
            String[] sortParams = sort.split(",");
            sortObj = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        if (firstName != null && lastName != null) {
            return memberRepository.findAll(
                    (root, query, cb) -> cb.and(
                            cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%")
                    ), pageable);
        } else if (firstName != null) {
            return memberRepository.findAll(
                    (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"),
                    pageable
            );
        } else if (lastName != null) {
            return memberRepository.findAll(
                    (root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"),
                    pageable
            );
        }

        return memberRepository.findAll(pageable);
    }

    /**
     * Get a member by ID. Throws UserNotFoundException if not found.
     */
    @Cacheable(value = "members", key = "#id")
    public Member getMemberById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Member not found with id: " + id));
    }

    /**
     * Create a new member.
     */
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    /**
     * Update an existing member. Throws UserNotFoundException if the member does not exist.
     */
    public Member updateMember(UUID id, Member memberDetails) {
        Member member = getMemberById(id); // will throw UserNotFoundException if not found
        member.setFirstName(memberDetails.getFirstName());
        member.setLastName(memberDetails.getLastName());
        member.setDateOfBirth(memberDetails.getDateOfBirth());
        member.setEmail(memberDetails.getEmail());
        member.setUpdatedAt(java.time.LocalDateTime.now());
        return memberRepository.save(member);
    }

    /**
     * Delete a member by ID. Throws UserNotFoundException if the member does not exist.
     */
    public void deleteMember(UUID id) {
        Member member = getMemberById(id); // will throw UserNotFoundException if not found
        memberRepository.delete(member);
    }
}
