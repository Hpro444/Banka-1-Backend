package com.banka1.userService.dto.rabbitmq;

/**
 * Enum koji definise tipove email notifikacija koje user-service salje putem RabbitMQ-a.
 */
public enum EmailType {

    /** Aktivacioni mejl koji se salje novom zaposlenom kako bi postavio lozinku i aktivirao nalog. */
    ACTIVATION,

    /** Mejl sa linkom za reset zaboravljene lozinke. */
    PASSWORD_RESET,

    /** Obaveštenje o deaktivaciji korisnickog naloga. */
    ACCOUNT_DEACTIVATION
}
