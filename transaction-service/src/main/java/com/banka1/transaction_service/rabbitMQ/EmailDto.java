package com.banka1.transaction_service.rabbitMQ;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO koji se šalje RabbitMQ email servisu.
 * Sadrži sve potrebne podatke za generisanje i slanje email notifikacija.
 * Polja sa {@code null} vrednoscu se isključuju iz JSON serijalizacije.
 */
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailDto {

    /** Email adresa primaoca notifikacije */
    private String userEmail;

    /** Ime ili korisničko ime primaoca (koristi se u tekstu mejla) */
    private String username;

    /** Tip email notifikacije koji određuje sadržaj i šablonu mejla */
    private EmailType emailType;

    /**
     * Kreira payload za email koji je namljen korisniku za notifikaciju o transakciji.
     *
     * @param username korisničko ime ili ime za prikaz u mejlu
     * @param userEmail email adresa primaoca
     * @param emailType tip notifikacije (TRANSACTION_COMPLETED ili TRANSACTION_DENIED)
     */
    public EmailDto(String username, String userEmail, EmailType emailType) {
        this.userEmail = userEmail;
        this.username = username;
        this.emailType = emailType;
    }
}
