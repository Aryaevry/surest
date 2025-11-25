package org.surest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.surest.dto.MemberResponseDto;
import org.surest.entity.Member;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MemberMapper {

    MemberResponseDto toResponse(Member member);
}
