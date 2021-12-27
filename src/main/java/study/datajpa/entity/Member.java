package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","username","age"})
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = : username"
)
@NamedEntityGraph(name = "Member.all",attributeNodes = @NamedAttributeNode("team"))
public class Member{
    @Id
    @GeneratedValue
    @Column(name = "member_id") //DB테이블에 Member_id로 메핑
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) //실무에서는 반드시 지연로딩
    @JoinColumn(name = "team_id")
    private Team team;

    /*
    protected Member(){
        //JPA 표준 스팩에 엔티티는 기본 생성자가 있어야 한다.
        //JPA가 프록시 객체를 사용할 때 해당 생성자를 사용하기 때문에
    }
    */
    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null)
            changeTeam(team);
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this); // 반대편도 바꿔준다.
    }

}
