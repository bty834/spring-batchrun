# 背景
在业务开发过程中，经常需要批量调用方法（如feign调用其他服务），但批量一般考虑到调用方法性能、接口响应超时等因素需要做分批调用，常常利用guava的Lists.partition方法对入参做分批，很多冗余重复代码。该组件利用方法注解透明地对方法做了分批调用，极大地加快了开发效率。

# QuickStart

第一步：启用分批执行

```java
// 启用分批执行
@EnableBatchRun
@SpringBootApplication
public class Example implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(Example.class);
    }
}
```

第二步：注解分批执行方法

```java
import site.btyhub.batchrun.annotation.BatchParam;

@Service
public class Test {

    @BatchRun(batchSize = 2)
    public List<String> t1(List<String> input) {...};

    @BatchRun(batchSize = 2)
    public List<Object> t2(Object[] input) {...};

    @BatchRun(batchSize = 2)
    public Object[] t3(List<Object> input) {...};

    public List<Object> t4(Object a, List<Object> b, @BatchParam List<Object> input){...};
}
```
方法有一个入参时，无需注解`@BatchParam`;

方法有多个入参时，需指定哪个参数进行分批，有且仅有一个参数能注解`@BatchParam` ;

分批执行的类型只能是 `Object[]` 包装类型数组 或 `List` 列表

TODO: `@BatchRun(batchSize= "${batch.size}") `
