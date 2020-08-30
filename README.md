# 接口幂等请求
代码中用了使用标注注解的方式，用俩种方式来实现接口幂等请求的实现。

## 实现方式

- 基于拦截器的方式

> 需要先定义拦截器，实现拦截规则,核心逻辑如下，具体在包`interceptor`下

```java
    @Autowired
        private TokenService tokenService;
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            //进行方法的拦截，如若不是直接通过
            if (!(handler instanceof HandlerMethod)) {
                return true;
            }
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取方法
            Method method = handlerMethod.getMethod();
            //获取方法上是否标注注解
            Idempotent idempotent = method.getAnnotation(Idempotent.class);
            if (idempotent != null) {
                //检查Token
               try{
                   return tokenService.checkToken(request);
               }catch (IdempotentException e){
                   throw e;
               }
    
            }
            return true;
        }
```

- 基于Aop实现

  需要定义切面，切点已经前置通知，核心代码如下,具体在包`aop`下
  
```java
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
```

## 测试注意
  
  代码中是用的俩种方式实现的接口幂等请求拦截，如果测试aop的方式，需要将config目录下的
  类`WebConfig`的代码注释掉
  
## 小结
   接口请求幂等操作是我们经常会遇到的需求，这里用俩种实现方式，小伙伴们可以根据需要自行选择
   
   ![](http://cdn.triumphxx.com.cn/img/%E6%89%AB%E7%A0%81_%E6%90%9C%E7%B4%A2%E8%81%94%E5%90%88%E4%BC%A0%E6%92%AD%E6%A0%B7%E5%BC%8F-%E6%A0%87%E5%87%86%E8%89%B2%E7%89%88.png)