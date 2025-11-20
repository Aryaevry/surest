package org.surest.serviceimpl;

import org.surest.entity.Member;
import org.surest.exception.UserNotFoundException;
import org.surest.repository.MemberRepository;
import org.surest.service.MemberService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Implementation of {@link MemberService} for managing {@link Member} entities.
 * <p>
 * Provides CRUD operations, pagination, and optional filtering by first name or last name.
 * SLF4J logging is used to track all operations for auditing and debugging purposes.
 */
@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Member> getMembers(int page, int size, String sort, String firstName, String lastName) {
        logger.info("Fetching members: page={}, size={}, sort={}, firstName={}, lastName={}",
                page, size, sort, firstName, lastName);

        Sort sortObj = Sort.by("id");
        if (sort != null && !sort.isBlank()) {
            String[] sortParams = sort.split(",");
            sortObj = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Member> result;
        if (firstName != null && lastName != null) {
            result = memberRepository.findAll(
                    (root, query, cb) -> cb.and(
                            cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%")
                    ), pageable);
        } else if (firstName != null) {
            result = memberRepository.findAll(
                    (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"),
                    pageable
            );
        } else if (lastName != null) {
            result = memberRepository.findAll(
                    (root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"),
                    pageable
            );
        } else {
            result = memberRepository.findAll(pageable);
        }

        logger.info("Fetched {} members", result.getNumberOfElements());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "members", key = "#id")
    public Member getMemberById(UUID id) {
        logger.info("Fetching member by ID: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Member not found with id: " + id));
        logger.info("Member found: {}", member.getId());
        return member;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Member createMember(Member member) {
        logger.info("Creating new member: {} {}", member.getFirstName(), member.getLastName());
        Member saved = memberRepository.save(member);
        logger.info("Member created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Member updateMember(UUID id, Member memberDetails) {
        logger.info("Updating member with ID: {}", id);
        Member member = getMemberById(id); // will throw UserNotFoundException if not found
        member.setFirstName(memberDetails.getFirstName());
        member.setLastName(memberDetails.getLastName());
        member.setDateOfBirth(memberDetails.getDateOfBirth());
        member.setEmail(memberDetails.getEmail());
        member.setUpdatedAt(java.time.LocalDateTime.now());
        Member updated = memberRepository.save(member);
        logger.info("Member updated successfully: {}", updated.getId());
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteMember(UUID id) {
        logger.info("Deleting member with ID: {}", id);
        Member member = getMemberById(id); // will throw UserNotFoundException if not found
        memberRepository.delete(member);
        logger.info("Member deleted successfully: {}", id);
    }
}
