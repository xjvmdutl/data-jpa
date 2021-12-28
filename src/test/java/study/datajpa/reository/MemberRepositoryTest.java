package study.datajpa.reository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository; //구현체가 없는데 어떻게 자동으로 코드가 구현되어 있는지?
    //인터페이스를 보고 구현체를 SpringDataJpa가 구현해서 인젝션을 해준다.

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void test(){
        System.out.println("memberRepository = "+memberRepository.getClass());
        Member member = new Member("MemberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    @Rollback(value = false)
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);
        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }
    @Test
    public void findHelloBy(){
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        assertThat(result.get(0)).isEqualTo(m1);
    }
    @Test
    public void testQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA",10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void testFindUsernameList(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        for(String s : result){
            System.out.println(s);
        }
    }

    @Test
    public void testFindMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10, team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();
        for(MemberDto dto : result){
            System.out.println("MemberDto = " + dto);
        }
    }

    @Test
    public void testFindByNames(){

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));
        for(Member member : result){
            System.out.println("member = " + member);
        }
    }

    @Test
    public void testReturnType(){

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result1 = memberRepository.findListByUsername("AAA");
        //컬렉션 반환인데 데이터가 없다면 빈 컬렉션을 반환한다 -> 에러는 안난다.
        Member result2 = memberRepository.findMemberByUsername("뉴ㅠㄴ");
        //단건조회에서 값이 없다면?? null값을 반환
        //JPA는 결과가 없다면 NoResultException 에러를 발생시켰지만, SpringDataJpa는 try-catch 로 감싸서 null 을 반환한다
        System.out.println(result2);
        Optional<Member> result3 = memberRepository.findOptionalByUsername("AAA");
        //자바 8이상부터는 optional로 감싸서 모든 문제를 해결한다.
        //값이 없는 경우는 클라이언트에서 책임을 지어야 된다는 의미


        //단건 조회에서 결과가 2개 이상일 경우
        //에러가 발생한다.
        //참고 : Jpa에서 발생한 예외를 Spring 예외를 한번 추상화 해서 반환해 준다.
    }

    @Test
    public void paging(){
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        //부모 인터페이스가 PageAble이다.

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        //SpringDataJpa가 Page를 반환해야 되서 totalCount까지 계산해서 쿼리를 실행시켜 반환한다.
        //사실 limit로 데이터를 가지고 오는것에 성능이 안나오는 것이 아니라, totalCount 가 성능을 좌지우지 한다.

        //Member를 그냥 반환하면 안되고 Dto로 변환하여 반환해야 한다.
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));


        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void slice(){
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);
        //내가 요청한 limit보다 한개 더많은 데이터를 가지고 와서 값이 있는지를 확인해준다
        //그냥 List로 가지고 와서 동작시킬수도 있다.

        //then
        List<Member> content = page.getContent();
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }


    @Test
    public void testBulkUpdate(){
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        em.flush();
        em.clear(); //벌크 연산후 영속성 컨텍스트를 비워주어야 한다

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        //영속성 컨택스트는 40이 저장되있지만 실제 DB에는 벌크연산으로 적용이 되었기 때문에 41이 저장되어있다(조심)
        //따라서 벌크연산 이후에는 영속성 컨택스트를 초기화 해야한다.


        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when N + 1
        //selectMember 1
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        //쿼리의 결과만큼 추가 쿼리가 나간다.
        for(Member member : members) {
            System.out.println("member = " + member.getUsername());
            //member의 team이 지연로딩으로 되어있기때문에 실제 조회할떄 쿼리를 실행한다.
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }
    @Test
    public void testQueryHint(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();
        
        //when
        //Member findMember = memberRepository.findById(member1.getId()).get();
        //findMember.setUsername("member2"); //더티체크 동작
        //순순히 조회를 할 것인데 더티체크를 하기 위해 복사본을 만드는 메모리 낭비를 한다(비용이 든다)
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2"); //더티체크 동작 자체를 안한다.

        em.flush();
    }

    @Test
    public void testLock(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");
        //Jpa가 제공하는 Lock기능을 SpringDataJpa에서 쉽게 사용할수 있다.
    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic(){
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1",0,teamA);
        Member m2 = new Member("m2",0,teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void QueryByExample(){
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member m1 = new Member("m1",0,teamA);
        Member m2 = new Member("m2",0,teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("m1");//앤티티 자체가 검색조건이 된다.
        Team team = new Team("teamA"); //연관관계까지 고려는 해서 검색을 해준다.
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching() //OUTER 조인은 안된다.
                .withIgnorePaths("age");//age속성은 무시한다.
        Example<Member> example = Example.of(member,matcher); //prev 타입은 무시해야된다.

        List<Member> result = memberRepository.findAll(example);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections(){
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member m1 = new Member("m1",0,teamA);
        Member m2 = new Member("m2",0,teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        //내가 원하는 데이터만 찍어서 가지고 올때 프록시 기술을 사용하여 필요한 값만 가지고 온다
        /*
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");
        for(UsernameOnly usernameOnly : result) //인터페이스 가지고 구현체를 만들어 반환해준다.
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
        */

        //구체적인 클래스를 명시했기 때문에 프록시패턴을 사용하는 것이 아니라 값을 넣어주는것이다.
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1",NestedClosedProjections.class);
        for(NestedClosedProjections nestedClosedProjections : result) {//인터페이스 가지고 구현체를 만들어 반환해준다.
            String username = nestedClosedProjections.getUsername();
            System.out.println("username = " + username);
            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("teamName = " + teamName);
        }
    }

    @Test
    public void nativeQuery(){
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member m1 = new Member("m1",0,teamA);
        Member m2 = new Member("m2",0,teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0,10));
        List<MemberProjection> content = result.getContent();
        for(MemberProjection memberProjection : content){
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
    }
}
