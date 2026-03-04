package com.vitorsaucedo.wordle;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class WordleApplicationTests {

	@Test
	void contextLoads() {
	}

}
