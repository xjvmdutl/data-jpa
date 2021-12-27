package study.datajpa.reository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor //규칙: 리포지토리 인터페이스 이름 + Impl //스프링 데이터 JPA가 인식해서 스프링 빈으로 등록
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m",Member.class)
                .getResultList();
    }
}
