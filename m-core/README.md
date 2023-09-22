# m-core

## aspect

pom.xml

```xml

<project>
    <dependencies>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

proxy the object

**Spring by default will create proxy based on this interface.**

```
AspectJProxyFactory pf= new AspectJProxyFactory(g);
pf.addAspect(AspectBenchmark.class);
// use interface
Runnable gs=pf.getProxy();
```
