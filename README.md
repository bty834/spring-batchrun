# Background
when developing some business logic, it's common to call other service interfaces through Feign or RPC in one user request, 
and there are many batch-operation calls. To avoid timeout and performance, we usually limit the batch size in one remote call.
Typically, we use guava's `Lists.partition(...)` to achieve that goal, instead we use annotation-based way to accomplish.

# QuickStart

STEP 1：Enable it

```java
// enable it 
@EnableBatchRun
@SpringBootApplication
public class Example implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(Example.class);
    }
}
```

STEP 2：Annotate methods 

```java
@Service
public class TestService {

    // supports numbers in String type
    @BatchRun(batchSize = "2")
    public List<String> test1(List<String> input){
        // ...
    }
    
    // ${} ，supports value injection, input 
    @BatchRun(batchSize = "${test.batchSize}")
    public String[] test2(String[] input){
        // ...
    }
    
    // supports spring EL, use @BatchParam to designate which parameter to split its size when multiple parameters
    @BatchRun(batchSize = "#{5-4}")
    public List<String> test3(List<String> a,@BatchParam List<String> input){
        // ...
    }

}
```
- When method has only one parameter，no need to be annotated with `@BatchParam`;

- When method has many parameters，only one parameter can be annotated with `@BatchParam`;

- Parameter type & return type  can only be  `Object[]` or `List`;

PS:

The order of this AOP is `Ordered.LOWEST_PRECEDENCE - 100`.


When method annotated with  `@Transactional` or `@Retryable` , `@BatchRun` will surround the running of transaction or retry logic

you should not call method within the same class.

