package org.surest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.surest.dto.MemberReqResDto;
import org.surest.entity.Member;
import org.surest.integration.AbstractIntegrationTest;
import org.surest.repository.MemberRepository;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class MemberControllerIntegrationTest  extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member savedMember;

    @BeforeEach
    void setup() {
        memberRepository.deleteAll();

        savedMember = memberRepository.save(
                Member.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .email("john123@example.com")
                        .dateOfBirth(LocalDate.of(1995, 5, 10))
                        .build()
        );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetMembers() throws Exception {
        mockMvc.perform(get("/api/v1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetMemberById() throws Exception {
        mockMvc.perform(get("/api/v1/members/" + savedMember.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateMember() throws Exception {
        MemberReqResDto dto = new MemberReqResDto(
                null,                     // id
                "Alice",                  // firstName
                "Smith",                  // lastName
                LocalDate.of(1990, 1, 1), // dateOfBirth
                "alice@example.com",      // email
                null,                     // createdAt
                null,                     // updatedAt
                null                      // lastUpdated
        );

        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateMember() throws Exception {
        MemberReqResDto dto = new MemberReqResDto(
                null,                     // id
                "Alice",                  // firstName
                "Smith",                  // lastName
                LocalDate.of(1990, 1, 1), // dateOfBirth
                "alice@example.com",      // email
                null,                     // createdAt
                null,                     // updatedAt
                null                      // lastUpdated
        );

        mockMvc.perform(put("/api/v1/members/" + savedMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteMember() throws Exception {
        mockMvc.perform(delete("/api/v1/members/" + savedMember.getId()))
                .andExpect(status().isNoContent());
    }
}
