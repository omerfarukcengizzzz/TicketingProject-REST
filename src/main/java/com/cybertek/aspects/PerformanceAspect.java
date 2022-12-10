package com.cybertek.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@Slf4j
public class PerformanceAspect {

    @Pointcut("@annotation(com.cybertek.annotation.ExecutionTime)")
    private void anyExecutionTimeOperation() {}

    @Around("anyExecutionTimeOperation()")
    public Object anyExecutionTimeOperationAdvice(ProceedingJoinPoint proceedingJoinPoint){
        long beforeTime = System.currentTimeMillis();

        Object result;

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
