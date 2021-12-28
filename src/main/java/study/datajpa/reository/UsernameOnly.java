package study.datajpa.reository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    //CloseProjection같은 경우 select 절에서 필요한 데이터만 가지고 온다.
    //@Value("#{target.username + ' ' + target.age}") //OpenProjection : entity를 select절에서 가지고 온뒤, 애플리케이션에서 계산해서 출력해준다.
    String getUsername(); //구현체가 아닌 인터페이스만 만든다.
}
