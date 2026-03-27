package com.company.observability.starter.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.WeakHashMap;

/**
 * AOP aspekt koji presrece sve izuzetke bacene iz Spring @Service komponenti
 * i loguje ih pre nego sto se propagiraju dalje kroz stek poziva.
 * <p>
 * Koristi {@link ThreadLocal} sa {@link WeakHashMap} kako bi sprecio
 * visestruko logovanje istog izuzetka kada servisi pozivaju jedan drugog.
 */
@Aspect
public class ServiceExceptionLoggingAspect {

    private static final ThreadLocal<WeakHashMap<Throwable, Boolean>> LOGGED_EXCEPTIONS =
            ThreadLocal.withInitial(WeakHashMap::new);

    private final ExceptionLoggingService exceptionLoggingService;

    /**
     * Kreira aspekt za logovanje poslovnih izuzetaka.
     *
     * @param exceptionLoggingService servis za logovanje izuzetaka
     */
    public ServiceExceptionLoggingAspect(ExceptionLoggingService exceptionLoggingService) {
        this.exceptionLoggingService = exceptionLoggingService;
    }

    /**
     * Presrece izvrsavanje svih metoda klasa anotiranih sa {@code @Service}.
     * <p>
     * Kada metoda baci izuzetak, aspekt ga loguje na WARN nivou i propagira dalje.
     * Isti izuzetak se loguje samo jednom, cak i ako prolazi kroz vise slojeva servisa.
     *
     * @param joinPoint tacka presretanja u lancu poziva
     * @return rezultat izvrsavanja originalne metode
     * @throws Throwable originalni izuzetak, nepromenjen
     */
    @Around("@within(org.springframework.stereotype.Service)")
    public Object logServiceExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            WeakHashMap<Throwable, Boolean> logged = LOGGED_EXCEPTIONS.get();
            if (!logged.containsKey(e)) {
                logged.put(e, Boolean.TRUE);
                String className = joinPoint.getTarget().getClass().getSimpleName();
                String methodName = joinPoint.getSignature().getName();
                exceptionLoggingService.logBusinessException(e, className, methodName);
            }
            throw e;
        }
    }
}
