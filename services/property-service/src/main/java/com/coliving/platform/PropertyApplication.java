package com.coliving.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * property-service를 기동한다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
@SpringBootApplication
public class PropertyApplication {

	/** property-service를 기동한다. */
	public static void main(String[] args) {
		SpringApplication.run(PropertyApplication.class, args);
	}

}
