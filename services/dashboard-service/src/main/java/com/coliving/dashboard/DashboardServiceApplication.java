package com.coliving.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 대시보드 서비스 애플리케이션 시작점 (스케줄링 활성화)
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
@SpringBootApplication
@EnableScheduling
public class DashboardServiceApplication {

	/* 애플리케이션 실행 */
	public static void main(String[] args) {
		SpringApplication.run(DashboardServiceApplication.class, args);
	}

}
