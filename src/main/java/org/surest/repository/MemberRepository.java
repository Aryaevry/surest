package org.surest.repository;

import org.surest.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * Repository interface for Member entity.
 *
 * Extends:
 * - JpaRepository: provides basic CRUD operations.
 * - JpaSpecificationExecutor: provides support for dynamic filtering using Specifications.
 */
public interface MemberRepository extends JpaRepository<Member, UUID>, JpaSpecificationExecutor<Member> {
    // No additional methods required.
    // JpaSpecificationExecutor allows dynamic filtering (used in MemberService#getMembers).
}
