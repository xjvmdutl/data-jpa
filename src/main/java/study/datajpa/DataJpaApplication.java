package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") //스프링을 사용하면 해당 위치를 잡아 주어야 되지만, boot를 사용하므로 안해도 된다.
@EnableJpaAuditing //해당 어노테이션이 있어야된다 //만약 업데이트시 null값을 하고 싶다면 modifyOnCreate = false로 하면 된다
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider(){
		//실제로는 스프링 시큐리티에서 값을 꺼내서 해야된다.
		return () ->Optional.of(UUID.randomUUID().toString());
	}
}
