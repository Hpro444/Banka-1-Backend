package com.banka1.transaction_service.rabbitMQ;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Klijent za slanje email notifikacija na RabbitMQ message broker.
 * Enkapsulira {@link RabbitTemplate} i konfigurisane vrednosti exchange-a i routing ključa.
 */
@Component
@RequiredArgsConstructor
public class RabbitClient {

    /** Spring AMQP template koji obavlja stvarno slanje poruka na RabbitMQ */
    private final RabbitTemplate rabbitTemplate;

    /** Naziv RabbitMQ exchange-a na koji se poruke šalju */
    @Value("${rabbitmq.exchange}")
    private String exchange;

    /**
     * Šalje email notifikaciju na RabbitMQ exchange koristeći routing key iz tipa poruke.
     * <p>
     * Email servis će primiti poruku i obraditi je prema tipu (TRANSACTION_COMPLETED ili TRANSACTION_DENIED).
     *
     * @param dto payload poruke sa detaljima notifikacije koji se prosleđuje email servisu
     */
    //TODO FIX
    public void sendEmailNotification(EmailDto dto) {
        rabbitTemplate.convertAndSend(exchange, dto.getEmailType().getRoutingKey(), dto);
    }


}
