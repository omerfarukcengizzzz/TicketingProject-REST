package com.cybertek.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Aspect
@Configuration
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.cybertek.controller.ProjectController.*()) || execution(* com.cybertek.controller.TaskController.*())")
    private void anyControllerOperation() {}

    @Before("anyControllerOperation()")
    public void anyBeforeControllerOperationAdvice(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Before(User : {} - Method : {} - Parameters : {})", authentication.getName(), joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "anyControllerOperation()", returning = "results")
    public void anyAfterReturningControllerOperationAdvice(JoinPoint joinPoint, Object results) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("AfterReturning(User : {} - Method : {} - Results : {})", authentication.getName(), joinPoint.getSignature().toShortString(), results.toString());
    }

    @AfterThrowing(pointcut = "anyControllerOperation()", throwing = "exception")
    public void anyAfterThrowingControllerOperationAdvice(JoinPoint joinPoint, RuntimeException exception) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("AfterReturning(User : {} - Method : {} - Exception : {})", authentication.getName(), joinPoint.getSignature().toShortString(), exception.getLocalizedMessage());
    }
    
}