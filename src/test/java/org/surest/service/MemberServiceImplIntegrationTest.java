package org.surest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.surest.dto.MemberReqResDto;
import org.surest.entity.Member;
import org.surest.exception.DuplicateDataException;
import org.surest.exception.UserNotFoundException;
import org.surest.integration.AbstractIntegrationTest;
import org.surest.repository.MemberRepository;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@EnableCaching
public class MemberServiceImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CacheManager cacheManager;  // Redis CacheManager

    private Member member;

    @BeforeEach
    void setUp() {
        // Clear the database and set up a test member
        memberRepository.deleteAll();
        member = new Member();
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("john@example.com");
        member.setDateOfBirth(LocalDate.of(1990, 1, 1));

        member = memberRepository.save(member);  // Save to DB
    }

    @Test
    void testGetMembers() {
        // Create a page request with pagination and sorting
        Pageable pageable = PageRequest.of(0, 10, Sort.by("firstName"));
        Page<MemberReqResDto> result = memberService.getMembers(0, 10, "firstName,asc", null, null);

        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).firstName());
    }

    @Test
    void testGetMemberById() {
        // Fetch member by ID and assert the result
        MemberReqResDto result = memberService.getMemberById(member.getId());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
    }

    @Test
    void testGetMemberByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(UserNotFoundException.class, () -> memberService.getMemberById(nonExistentId));
    }

    @Test
    void testCreateMember() {
        // Create a new member and save to the database
        Member newMember = new Member();
        newMember.setFirstName("Jane");
        newMember.setLastName("Smith");
        newMember.setEmail("jane@example.com");
        newMember.setDateOfBirth(LocalDate.of(1995, 5, 5));

        MemberReqResDto savedMember = memberService.createMember(newMember);

        assertNotNull(savedMember.id());
        assertEquals("Jane", savedMember.firstName());
        assertEquals("Smith", savedMember.lastName());
        assertEquals("jane@example.com", savedMember.email());
    }


    @Test
    void testCreateMemberWithDuplicateEmail() {
        // Create a new member with a duplicate email
        Member newMember = new Member();
        newMember.setFirstName("Jane");
        newMember.setLastName("Smith");
        newMember.setEmail("john@example.com");  // Using the same email as the existing member
        newMember.setDateOfBirth(LocalDate.of(1995, 5, 5));

        // Expect DuplicateDataException to be thrown
        assertThrows(DuplicateDataException.class, () -> memberService.createMember(newMember));
    }

    @Test
    void testUpdateMember() {
        // Update an existing member's details
        member.setFirstName("UpdatedJohn");
        member.setLastName("UpdatedDoe");
        member.setEmail("updated@example.com");

        MemberReqResDto updatedMember = memberService.updateMember(member.getId(), member);

        assertEquals("UpdatedJohn", updatedMember.firstName());
        assertEquals("UpdatedDoe", updatedMember.lastName());
        assertEquals("updated@example.com", updatedMember.email());
    }

    @Test
    void testUpdateMemberNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        member.setFirstName("UpdatedJohn");
        member.setLastName("UpdatedDoe");

        assertThrows(UserNotFoundException.class, () -> memberService.updateMember(nonExistentId, member));
    }

    @Test
    void testDeleteMember() {
        // Test deleting the member
        memberService.deleteMember(member.getId());

        assertFalse(memberRepository.existsById(member.getId()));
    }

    @Test
    void testDeleteMemberNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(UserNotFoundException.class, () -> memberService.deleteMember(nonExistentId));
    }
}
