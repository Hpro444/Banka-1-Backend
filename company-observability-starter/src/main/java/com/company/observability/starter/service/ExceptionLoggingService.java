package com.company.observability.starter.service;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servis za logovanje neobradjenih izuzetaka nastalih tokom obrade HTTP zahteva.
 * <p>
 * Pre logovanja vrsi maskiranje osetljivih podataka iz query string-a
 * i Authorization zaglavlja.
 */
public class ExceptionLoggingService {
    private static final Logger log = LoggerFactory.getLogger(ExceptionLoggingService.class);
    private final SensitiveDataMaskingService maskingService;

    /**
     * Kreira servis za logovanje neobradjenih izuzetaka.
     *
     * @param maskingService servis za maskiranje osetljivih podataka pre logovanja
     */
    public ExceptionLoggingService(SensitiveDataMaskingService maskingService) {
        this.maskingService = maskingService;
    }

    /**
     * Loguje neobradjeni izuzetak zajedno sa osnovnim informacijama o HTTP zahtevu.
     *
     * @param exception izuzetak koji je nastao
     * @param request HTTP zahtev u kome je izuzetak nastao
     */
    public void logUnhandledException(Exception exception, HttpServletRequest request) {
        String maskedQuery = maskingService.maskQuery(request.getQueryString());
        String maskedAuthorization = maskingService.maskAuthorizationHeader(
                request.getHeader("Authorization")
        );
        log.error(
                "Unhandled exception method={} uri={} query={} authorization={}",
                request.getMethod(),
                request.getRequestURI(),
                maskedQuery,
                maskedAuthorization,
                exception // automatski loguje stack trace
        );
    }

    /**
     * Loguje poslovni izuzetak koji je nastao u sloju servisa.
     *
     * @param exception izuzetak koji je nastao
     * @param className naziv klase servisa u kojoj je izuzetak nastao
     * @param methodName naziv metode u kojoj je izuzetak nastao
     */
    public void logBusinessException(Throwable exception, String className, String methodName) {
        log.warn(
                "Exception thrown in service class={} method={} type={} message={}",
                className,
                methodName,
                exception.getClass().getName(),
                exception.getMessage(),
                exception
        );
    }
}
