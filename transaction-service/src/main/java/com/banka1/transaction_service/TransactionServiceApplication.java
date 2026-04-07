package com.banka1.transaction_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Glavna Spring Boot aplikacija za Transaction Service.
 * Omogućava upravljanje finansijskim transakcijama i transferima novca između računa.
 * <p>
 * Uključuje zakazane taskove putem @EnableScheduling anotacije.
 */
@SpringBootApplication
@EnableScheduling
public class TransactionServiceApplication {

	/**
	 * Početna tačka aplikacije.
	 *
	 * @param args argumenti komandne linije prosleđeni aplikaciji
	 */
	public static void main(String[] args) {
		SpringApplication.run(TransactionServiceApplication.class, args);
	}

}
