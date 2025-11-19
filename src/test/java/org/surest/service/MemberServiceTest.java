package org.surest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.surest.entity.Member;
import org.surest.exception.UserNotFoundException;
import org.surest.repository.MemberRepository;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = Member.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    // Test getMembers with no filter
    @Test
    void testGetMembersNoFilter() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Member> result = memberService.getMembers(0, 10, null, null, null);
        assertEquals(1, result.getContent().size());
        verify(memberRepository, times(1)).findAll(any(Pageable.class));
    }

    // Test getMemberById when member exists
    @Test
    void testGetMemberByIdFound() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        Member result = memberService.getMemberById(member.getId());
        assertEquals("John", result.getFirstName());
    }

    // Test getMemberById when member does not exist
    @Test
    void testGetMemberByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> memberService.getMemberById(id));
    }

    // Test createMember
    @Test
    void testCreateMember() {
        when(memberRepository.save(member)).thenReturn(member);
        Member result = memberService.createMember(member);
        assertEquals("John", result.getFirstName());
    }

    // Test updateMember when member exists
    @Test
    void testUpdateMemberFound() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member updatedDetails = Member.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .build();

        Member result = memberService.updateMember(member.getId(), updatedDetails);
        assertEquals("Jane", result.getFirstName());
    }

    // Test updateMember when member not found
    @Test
    void testUpdateMemberNotFound() {
        UUID id = UUID.randomUUID();
        when(memberRepository.findById(id)).thenReturn(Optional.empty());
        Member updatedDetails = Member.builder().firstName("Jane").build();

        assertThrows(UserNotFoundException.class, () -> memberService.updateMember(id, updatedDetails));
    }

    // Test deleteMember when member exists
    @Test
    void testDeleteMemberFound() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        doNothing().when(memberRepository).delete(member);

        memberService.deleteMember(member.getId());
        verify(memberRepository, times(1)).delete(member);
    }

    // Test deleteMember when member not found
    @Test
    void testDeleteMemberNotFound() {
        UUID id = UUID.randomUUID();
        when(memberRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> memberService.deleteMember(id));
    }
}
