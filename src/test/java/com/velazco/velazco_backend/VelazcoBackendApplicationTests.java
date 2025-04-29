package com.velazco.velazco_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"base.url=http://localhost:8080"
})
class VelazcoBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
