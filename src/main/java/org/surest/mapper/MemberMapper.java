package org.surest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.surest.dto.MemberResponseDto;
import org.surest.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MemberMapper {

    MemberResponseDto toResponse(Member member);

    Member toEntity(MemberResponseDto memberDto);

    List<MemberResponseDto> toResponse(List<Member> members);

    // ⭐ Add Page conversion inside the same mapper
    default Page<MemberResponseDto> toResponse(Page<Member> memberPage) {
        List<MemberResponseDto> dtoList = toResponse(memberPage.getContent());
        return new PageImpl<>(
                dtoList,
                memberPage.getPageable(),
                memberPage.getTotalElements()
        );
    }
}
