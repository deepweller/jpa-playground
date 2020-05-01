package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional //test code 에서의 트랜젝션은 실행 후 rollback 됨.
    @Rollback(false) //test 에서 롤백되지 않도록 하는 옵션
    public void testMember() throws Exception {
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long saveId = memberRepository.save(member);
        Member fineMember = memberRepository.find(saveId);

        //then
        Assertions.assertThat(fineMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(fineMember.getUsername()).isEqualTo(member.getUsername());

        //같은 트랜젝션 안에서 실행되기 때문에 영속성 컨텍스트가 같음. 그래서 같은 식별자로 조회가 되기 때문에 동일한 1차캐시에 있는 객체로 조회됨. (select 쿼리가 실행되지도 않음)
        Assertions.assertThat(fineMember).isEqualTo(member);
    }
}