package study.datajpa.reository;

import lombok.Getter;

@Getter
public class UsernameOnlyDto {
    private final String username;

    public UsernameOnlyDto(String username) { //생성자의 파라미터 이름으로 매칭도 된다.
        this.username = username;
    }
}
