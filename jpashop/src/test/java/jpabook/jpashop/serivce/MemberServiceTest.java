package jpabook.jpashop.serivce;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
//    @Rollback(false) //스프링 테스트에서는 끝나고 롤백하므로 실제로 데이터가 들어간 것을 보려면 설정해야함 (em.flush();넣는 것과 동일)
    public void 회원가입() throws Exception {
        //given - 조건
        Member member = new Member();
        member.setName("park");

        //when - 액션
        Long savedId = memberService.join(member);

        //then - 결과
//        em.flush(); // @Rollback(false)와 동일
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test(expected = IllegalStateException.class) //try-catch와 동일한 역할
    public void 중복회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("park");

        Member member2 = new Member();
        member2.setName("park");

        //when
        memberService.join(member1);
        memberService.join(member2);
//        try {
//            memberService.join(member2);
//        } catch (IllegalStateException e) {
//            return;
//        }

        //then
        fail("예외 발생"); //테스트 fail을 발생시킴 > 이 라인까지 오게되면 테스가 fail 처리됨.
    }

}