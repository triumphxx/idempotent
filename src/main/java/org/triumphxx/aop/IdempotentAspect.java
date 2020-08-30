package org.triumphxx.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.triumphxx.exception.IdempotentException;
import org.triumphxx.token.TokenService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author:triumphxx
 * @Date:2020/8/30
 * @Time:9:28 上午
 * @微信公众号：北漂码农有话说
 * @网站：http://blog.triumphxx.com.cn
 * @GitHub https://github.com/triumphxx
 * @Desc: 使用Aop的方式实现拦截
 **/
@Component
@Aspect
public class IdempotentAspect {

    @Autowired
    TokenService tokenService;

    @Pointcut(value = "@annotation(org.triumphxx.annotation.Idempotent)")
    public void pointcut(){

    }
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) throws IdempotentException {
        //通过RequestContextHolder工具类来获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.
                getRequestAttributes()).getRequest();
        try{
            tokenService.checkToken(request);
        }catch (IdempotentException e){
            throw e;
        }
    }
}
