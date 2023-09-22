package com.cq.aspect;

import cn.hutool.core.date.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
public class AspectBenchmark {

    @Around("@annotation(Benchmark)")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start(jp.getTarget().getClass().getName());

        Object res = jp.proceed();

        sw.stop();
        log.info(sw.prettyPrint(TimeUnit.MILLISECONDS));

        return res;
    }
}
