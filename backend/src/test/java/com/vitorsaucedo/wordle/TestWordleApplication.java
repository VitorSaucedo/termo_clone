package com.vitorsaucedo.wordle;

import org.springframework.boot.SpringApplication;

public class TestWordleApplication {

	public static void main(String[] args) {
		SpringApplication.from(WordleApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
