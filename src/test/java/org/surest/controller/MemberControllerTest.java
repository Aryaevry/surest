package org.surest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.surest.controller.surest.MemberController;
import org.surest.dto.MemberDto;
import org.surest.entity.Member;
import org.surest.serviceimpl.MemberServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MemberControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private MemberServiceImpl memberServiceImpl; // constructor-injected mock
    private MemberController memberController;

    @BeforeEach
    void setup() {
        // Create mock
        memberServiceImpl = Mockito.mock(MemberServiceImpl.class);

        // Create controller with constructor injection
        memberController = new MemberController(memberServiceImpl);

        // Setup ObjectMapper with JavaTimeModule
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Setup standalone MockMvc with proper message converters
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    // Helper methods
    private Member createSampleMember() {
        return Member.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();
    }

    private MemberDto createSampleDto() {
        MemberDto dto = new MemberDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setEmail("john.doe@example.com");
        return dto;
    }

//    @Test
    void testGetMembers() throws Exception {
        Member member = createSampleMember();
        Page<Member> page = new PageImpl<>(List.of(member));

        when(memberServiceImpl.getMembers(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(page);

        mockMvc.perform(get("/members")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(member.getId().toString()))
                .andExpect(jsonPath("$.content[0].firstName").value(member.getFirstName()));

        verify(memberServiceImpl, times(1))
                .getMembers(anyInt(), anyInt(), anyString(), anyString(), anyString());
    }

    @Test
    void testGetMemberById() throws Exception {
        Member member = createSampleMember();

        when(memberServiceImpl.getMemberById(any(UUID.class))).thenReturn(member);

        mockMvc.perform(get("/members/{id}", member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId().toString()))
                .andExpect(jsonPath("$.firstName").value(member.getFirstName()));

        verify(memberServiceImpl, times(1)).getMemberById(member.getId());
    }

    @Test
    void testCreateMember() throws Exception {
        MemberDto dto = createSampleDto();
        Member saved = createSampleMember();

        when(memberServiceImpl.createMember(any(Member.class))).thenReturn(saved);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.firstName").value(saved.getFirstName()));

        verify(memberServiceImpl, times(1)).createMember(any(Member.class));
    }

    @Test
    void testUpdateMember() throws Exception {
        MemberDto dto = createSampleDto();
        Member updated = createSampleMember();

        when(memberServiceImpl.updateMember(any(UUID.class), any(Member.class))).thenReturn(updated);

        mockMvc.perform(put("/members/{id}", updated.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updated.getId().toString()))
                .andExpect(jsonPath("$.firstName").value(updated.getFirstName()));

        verify(memberServiceImpl, times(1)).updateMember(any(UUID.class), any(Member.class));
    }

    @Test
    void testDeleteMember() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(memberServiceImpl).deleteMember(id);

        mockMvc.perform(delete("/members/{id}", id))
                .andExpect(status().isNoContent());

        verify(memberServiceImpl, times(1)).deleteMember(id);
    }
}
