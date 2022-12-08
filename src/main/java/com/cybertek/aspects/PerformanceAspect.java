package com.cybertek.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class PerformanceAspect {

    Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    @Pointcut("@annotation(com.cybertek.annotation.ExecutionTime)")
    private void anyExecutionTimeOperation() {}

    @Around("anyExecutionTimeOperation()")
    public Object anyExecutionTimeOperationAdvice(ProceedingJoinPoint proceedingJoinPoint){
        long beforeTime = System.currentTimeMillis();

        Object result = null;

        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        long afterTime = System.currentTimeMillis();

        log.info("Time taken to execute : {} ms (Method : {} - Parameter : {})", (afterTime - beforeTime), proceedingJoinPoint.getSignature().toShortString(), proceedingJoinPoint.getArgs());

        return result;
    }

}
