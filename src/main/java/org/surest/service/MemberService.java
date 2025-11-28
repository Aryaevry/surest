package org.surest.service;

import org.surest.dto.MemberReqResDto;
import org.surest.entity.Member;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * Service interface for managing {@link Member} entities.
 * <p>
 * Provides methods for CRUD operations, pagination, and optional filtering.
 */
public interface MemberService {

    /**
     * Retrieves a paginated list of members, optionally filtered by first name and/or last name.
     *
     * @param page      the page number to retrieve
     * @param size      the number of records per page
     * @param sort      optional sort criteria in the format "field,direction"
     * @param firstName optional first name filter
     * @param lastName  optional last name filter
     * @return a {@link Page} of {@link Member} objects matching the criteria
     */
    Page<MemberReqResDto> getMembers(int page, int size, String sort, String firstName, String lastName);

    /**
     * Retrieves a member by its unique ID.
     *
     * @param id the UUID of the member
     * @return the {@link Member} with the given ID
     * @throws RuntimeException if the member is not found
     */
    MemberReqResDto getMemberById(UUID id);

    /**
     * Creates a new member.
     *
     * @param member the {@link Member} to create
     * @return the created {@link Member}
     */
    MemberReqResDto createMember(Member member);

    /**
     * Updates an existing member by ID.
     *
     * @param id            the UUID of the member to update
     * @param memberDetails the updated member details
     * @return the updated {@link Member}
     * @throws RuntimeException if the member is not found
     */
    MemberReqResDto updateMember(UUID id, Member memberDetails);

    /**
     * Deletes a member by ID.
     *
     * @param id the UUID of the member to delete
     * @throws RuntimeException if the member is not found
     */
    void deleteMember(UUID id);
}
