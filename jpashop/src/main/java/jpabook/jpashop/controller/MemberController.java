package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.serivce.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        log.info("MemberController createForm");
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    //@Valid > MemberForm안에 valid를 사용하겠다고 명시
    //BindingResult > error 처리(타임리프+스프링에서 지원하는 기본 에러처리로 valid에러를 화면에서 보기 좋게 표시해줌)
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        log.info("MemberController create");

        if (result.hasErrors()) {
            log.error("MemberController create error");
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);
        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        log.info("MemberController list");
        List<Member> members = memberService.findAll();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
