package org.surest.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.surest.dto.MemberResponseDto;
import org.surest.entity.Member;
import org.surest.exception.DuplicateDataException;
import org.surest.exception.UserNotFoundException;
import org.surest.mapper.MemberMapper;
import org.surest.repository.MemberRepository;
import org.surest.service.MemberService;

import java.util.UUID;

/**
 * Implementation of {@link MemberService} for managing {@link Member} entities.
 * <p>
 * Provides CRUD operations, pagination, and optional filtering by first name or last name.
 * SLF4J logging is used to track all operations for auditing and debugging purposes.
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<MemberResponseDto> getMembers(int page, int size, String sort, String firstName, String lastName) {
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
        return memberMapper.toResponse(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "members", key = "#id")
    public MemberResponseDto getMemberById(UUID id) {
        logger.info("Fetching member by ID: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Member not found with id: " + id));
        logger.info("Member found: {}", member.getId());
        return memberMapper.toResponse(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemberResponseDto createMember(Member member) {
        // Log the process of creating a new member
        logger.info("Creating new member: {} {}", member.getFirstName(), member.getLastName());

        // Create a Specification to check if the email already exists
        Specification<Member> emailSpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("email"), member.getEmail());

        // Check if the email already exists in the database
        boolean emailExists = memberRepository.findAll(emailSpecification).size() > 0;

        // If email exists, throw BusinessServiceException with a conflict status
        if (emailExists) {
            logger.error("Email already exists: {}", member.getEmail());
            throw new DuplicateDataException("Email already exists");
        }

        // If email is unique, save the new member
        Member createdMember = memberRepository.save(member);
        logger.info("Member created successfully with ID: {}", createdMember.getId());
        return memberMapper.toResponse(createdMember);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CachePut(value = "members", key = "#id")
    public MemberResponseDto updateMember(UUID id, Member memberDetails) {
        logger.info("Updating member with ID: {}", id);
        Member member = memberMapper.toEntity(getMemberById(id)); // will throw UserNotFoundException if not found
        member.setFirstName(memberDetails.getFirstName());
        member.setLastName(memberDetails.getLastName());
        member.setDateOfBirth(memberDetails.getDateOfBirth());
        member.setEmail(memberDetails.getEmail());
        member.setUpdatedAt(java.time.LocalDateTime.now());
        Member updated = memberRepository.save(member);
        logger.info("Member updated successfully: {}", updated.getId());
        return memberMapper.toResponse(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "members", key = "#id")
    public void deleteMember(UUID id) {
        logger.info("Deleting member with ID: {}", id);
        MemberResponseDto member = getMemberById(id); // will throw UserNotFoundException if not found
        memberRepository.delete( memberMapper.toEntity(member));
        logger.info("Member deleted successfully: {}", id);
    }
}
