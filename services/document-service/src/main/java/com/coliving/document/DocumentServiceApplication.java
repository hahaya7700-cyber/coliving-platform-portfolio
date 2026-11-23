package com.coliving.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 문서 서비스(계약서 양식 관리) 애플리케이션 시작점
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
@SpringBootApplication
public class DocumentServiceApplication {

	/* 애플리케이션 실행 */
	public static void main(String[] args) {
		SpringApplication.run(DocumentServiceApplication.class, args);
	}

}
