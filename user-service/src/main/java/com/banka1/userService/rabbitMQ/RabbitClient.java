package com.banka1.userService.rabbitMQ;

import com.banka1.userService.dto.rabbitmq.EmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Klijent za slanje poruka na RabbitMQ.
 * Enkapsulira {@link RabbitTemplate} i konfigurisane vrednosti exchange-a i routing kljuca.
 */
@Component
@RequiredArgsConstructor
public class RabbitClient {

    /** Spring AMQP template koji obavlja stvarno slanje poruka. */
    private final RabbitTemplate rabbitTemplate;

    /** Naziv RabbitMQ exchange-a na koji se poruke salju. */
    @Value("${rabbitmq.exchange}")
    private String exchange;

    /** Routing kljuc koji odredjuje na koji queue ce poruka biti rutirana. */
    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    /**
     * Salje email notifikaciju na RabbitMQ exchange sa konfigurisanom routing putanjom.
     *
     * @param dto payload poruke koja se prosledjuje email servisu
     */
    public void sendEmailNotification(EmailDto dto) {
        rabbitTemplate.convertAndSend(exchange, routingKey, dto);
    }
}
