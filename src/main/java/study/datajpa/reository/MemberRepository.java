package study.datajpa.reository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> { //<엔티티, id 타입>
    //JpaRepository 인터페이스를 상속 받는다.
    //도메인 특화된 기능 구현
    List<Member> findByUsername(String username);
}
