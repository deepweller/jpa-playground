package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.serivce.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 엔티티를 dto로 사용하면 안됨. 엔티티(디비)와 dto(api)는 스펙이기 때문에 서로 종속적이면 안됨.
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // 개선
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        // 엔티티를 외부에 노출하면 좋지 않음.
        // @JsonIgnore 로 노출하고 싶지 않은 필드는 가릴 수 있지만, 엔티티는 다른 곳에서도 사용할 수 있기 때문에 좋지 않은 방법임.
        return memberService.findAll();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findAll();
        List<MemberDto> members = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(members, members.size());
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
        private int count;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }
}
