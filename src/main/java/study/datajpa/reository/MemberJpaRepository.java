package study.datajpa.reository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public Member find(Long id){
        return em.find(Member.class,id);
    }
    public void delete(Member member){
        em.remove(member);
    }
    public List<Member> findAll(){
        //JPQL
        return em.createQuery("select m from Member m",Member.class)
                .getResultList();
    }
    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }
    public long count(){
        return em.createQuery("select count(m) from Member m",Long.class)
                .getSingleResult();//단건 조회
    }
    //JPA같은 경우 해당 메소드를 구현해야된다
    //SpringDataJpa는 어떻게 할까??
    public List<Member> findByUsernameAndAgeGreaterThan(String username,int age){
        return em.createQuery("select m from Member m where m.username = :username and age > :age",Member.class)
                .setParameter("username",username)
                .setParameter("age",age)
                .getResultList();
    }
    //해당 메소드를 부르는 것을 SpringDataJpa에서는 어떻게 할까?
    public List<Member> findByUsername(String username){
        return em.createNamedQuery("Member.findByUsername",Member.class)
                .setParameter("username",username)
                .getResultList();
    }

    public List<Member> findByPage(int age,int offset,int limit){
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc", Member.class)
                .setParameter("age",age)
                .setFirstResult(offset)   //페이징 기능이 이렇게 작성하면 동작이 끝난다
                .setMaxResults(limit)
                .getResultList();
    }


    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age = :age",Long.class)
                .setParameter("age",age)
                .getSingleResult();
    }

    public int bulkAgePlus(int age){
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age",age)
                .executeUpdate();
    }
}
