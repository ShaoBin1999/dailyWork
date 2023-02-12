# ApplicationRunner介绍
ApplicationRunner是在应用容器启动之后会回调到，
在ApplicationStartedListener调用之后被调用。
如果说ApplicationListener中有阻塞，那么ApplicationRunner也都不会被调用
```text
@Component
public class ApplicationRunnerTest implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(args);
    }
}
```
我们可以在applicationRunner中做一些异步启动的操作，比如实例化Kafka客户端，异步加载缓存等等在服务启动之后不立马提供服务的操作。