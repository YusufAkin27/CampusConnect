package user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.cloud.consul.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:user_service_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=none",
		"spring.sql.init.mode=always"
})
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
