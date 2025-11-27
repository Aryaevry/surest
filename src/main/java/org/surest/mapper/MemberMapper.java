package org.surest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.surest.dto.MemberReqResDto;
import org.surest.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MemberMapper {

    MemberReqResDto toResponse(Member member);

    Member toEntity(MemberReqResDto memberDto);

    List<MemberReqResDto> toResponse(List<Member> members);

    // ⭐ Add Page conversion inside the same mapper
    default Page<MemberReqResDto> toResponse(Page<Member> memberPage) {
        List<MemberReqResDto> dtoList = toResponse(memberPage.getContent());
        return new PageImpl<>(
                dtoList,
                memberPage.getPageable(),
                memberPage.getTotalElements()
        );
    }
}
