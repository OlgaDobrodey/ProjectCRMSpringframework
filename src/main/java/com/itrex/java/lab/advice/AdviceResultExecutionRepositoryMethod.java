package com.itrex.java.lab.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AdviceResultExecutionRepositoryMethod {

    private static final String RESULT_REPOSITORY_METHOD_NAME_START_SELECT =
            "\nREPOSITORY'S METHOD:\n %s\n result: %s\n";

    @AfterReturning(value = "execution(* com.itrex.java.lab.repository.*.select*(..))", returning = "entity")
    public void repMethodStartNameSelect(JoinPoint jp, Object entity) {
        log.info(String.format(RESULT_REPOSITORY_METHOD_NAME_START_SELECT, jp.getSignature().toLongString(), entity));
    }

}
