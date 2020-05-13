package jpabook.jpashop.serivce;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //jpa의 모든 데이터변경, 로직은 트랜젝션안에서 실행되어야함.
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional //readonly 예외 메서드는 별도로 추가
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId(); //em.persist 에서 id를 생성하고 엔티티 객체에 할당함
    }

    /**
     * 중복 회원 검증
     * 이렇게 해도 동시에 실행될 가능성이 있어 중복으로 들어갈 수 있기때문에 db에도 해당 컬럼에 unique 제약조건을 넣는 것이 좋음
     */
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원전체조회
     */
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /**
     * 회원 단건 조회
     */
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        // 변경감지로 데이터 업데이트하기
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
