package study.datajpa.reository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> { //<엔티티, id 타입>
    //JpaRepository 인터페이스를 상속 받는다.
    //도메인 특화된 기능 구현
    //메소드 명을 보고 JPQL을 만들어 준다.
    //관례를 따라서 메소드명을 보고 SpringDataJPA가 알아서 쿼리를 만들어 주는것이다.
    //단점: 조건이 증가할 수록 메소드명이 너무 길어진다.
    List<Member> findByUsernameAndAgeGreaterThan(String username,int age);

    List<Member> findTop3HelloBy(); //전체조회

    //@Query(name = "Member.findByUsername") //없어도 동작이 된다.
    //WHY? SpringDataJPA가 해당 우선순위를 가지고 있기 때문에
    //1.메소드 명을 가지고 해당 엔티티에 네임드쿼리를 찾는다.
    //2.메소드 관례에 따라 작성하는 방식으로 찾아서 생성해준다.
    //NamedQuery기능은 실무에서 거의 사용하지 않는다. -> 메소드에 쿼리를 동작시키는 기능이 너무 막강하여 이기능을 많이 사용하지 않는다.
    //NamedQuery 장점은 가장 큰 장점인 어플리케이션 로딩 시점에 쿼리를 파싱하여, 에러를 발생시켜준다
    //고객이 기능을 눌러서 오류가 발생하는것이 아닌, 로딩시점에 에러가 발생하니까 미리 오류를 수정할 수 있다
    List<Member> findByUsername(@Param("username") String username); //@Param을 JQPL을 네이밍 바인딩이 있으면 사용한다.


    //메소드에 JQPL을 바로 작성해 줄수 있다.
    //JPQL이 문자열이라서 오타를 치게 될 경우 어플리케이션이 오류를 발생시켜준다.
    //@Query에 작성된 쿼리는 이름이 없는 네임드 쿼리로 작성되어 어플리케이션 로딩 시점에 파싱이되는데, 이때 에러가 발생되면 알려주는 것이다.
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username,@Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList(); //String 하나 조회

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    //반환타입을 유연하게 작성할 수가 있다.
    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);
    
    @Query(value = "select m from Member m left join m.team t",
    countQuery = "select count(m.username) from Member m") //카운트 쿼리를 분리시킬수 있다.
    Page<Member> findByAge(int age, Pageable pageable); //페이징 동작
    Slice<Member> findSliceByAge(int age, Pageable pageable); //페이징 동작

    @Modifying(clearAutomatically = true) //해당 어노테이션을 넣어주어야된다(순순JPA을 executeUpdate와 같은 동작) 
    //벌크연산 뒤 영속성 컨택스트를 clear 해준는 옵션을 반드시 켜주자
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team") //멤버를 조회할때 다 가지고 온다.
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) //내부적으로 패치조인을 사용해서 JPQL없이 성능최적화해서 가지고 온다.
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"}) //JPQL과 같이 사용이된다
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //@EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);


    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly",value = "true"))
    Member findReadOnlyByUsername(String username);

    //select for update 
    @Lock(LockModeType.PESSIMISTIC_WRITE) //JPA에서 제공하는 것이다
    List<Member> findLockByUsername(String username);
}
