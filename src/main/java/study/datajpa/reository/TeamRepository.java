package study.datajpa.reository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

//@Repository 어노테이션이 없어도 된다.
public interface TeamRepository extends JpaRepository<Team,Long> {
}
