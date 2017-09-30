package com.hy.base;

import org.aspectj.lang.annotation.Pointcut;
import com.hy.core.Constants;

/**
 * Created by lenovo on 2014/12/8.
 */
public abstract class BaseProxy<T>{

    protected BaseImpl baseImpl = (BaseImpl) Constants.applicationContext.getBean("baseImpl");
    @Pointcut("execution(* org.hy..*(..))")
    public void anyMethod(){}



/*    @Before("anyMethod()")
    public void beforeExcute(JoinPoint joinPoint) {
        Object[] objs = joinPoint.getArgs();
        for(Object obj:objs)
            System.out.println(obj);
        System.out.println("===============================method before excute.........");
    }

    @After("anyMethod()")
    public void afterExcute(){
        System.out.println("=================================method after excute..........");
    }
    @Around("anyMethod()")
    public void aroundMethod(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("================================进入环绕通知........");
        System.out.println(pjp.getArgs().length);
        pjp.proceed();
        System.out.println("=========================离开环绕通知....");
    }*/

    //    @After("within(* org.hy.hessian..*) && @args(rl)")

}
