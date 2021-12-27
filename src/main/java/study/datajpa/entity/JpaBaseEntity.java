package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass //실제 상속이아니라, 데이터만 받는어노테이션
public class JpaBaseEntity {
    @Column(updatable = false) //변경되지 못하게 막는다
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist //jpa가 제공한다.//persitence 전에 동작
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate(){
        updatedDate = LocalDateTime.now();
    }
}
