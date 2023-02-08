# ttl

thread中有两个成员变量 threadLocals和inheritableThreadLocals
inheritableThreadLocals适用于异步的场景，创建新线程时会检查父线程的inheritableThreadLocals变量
是否为空。如果不是则复制一份到子线程中
看看线程池中的应用可能会出错
```java
class test{
    static InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        inheritableThreadLocal.set("i am a inherit parent");
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                System.out.println(inheritableThreadLocal.get());
            }
        });

        TimeUnit.SECONDS.sleep(1);
        inheritableThreadLocal.set("i am a new inherit parent");// 设置新的值

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                System.out.println(inheritableThreadLocal.get());
            }
        });
    }
}
```
输出结果为
<br>i am a inherit parent; 
<br> i am a inherit parent 
<br> 因为线程池中只有一个线程，所以第二个任务复用了第一个线程，就算主线程修改了变量的值，也传递不到子线程

## TransmittableThreadLocal
```java
class test{
    static TransmittableThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();// 使用TransmittableThreadLocal


    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService = TtlExecutors.getTtlExecutorService(executorService); // 用TtlExecutors装饰线程池

        transmittableThreadLocal.set("i am a transmittable parent");
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                System.out.println(transmittableThreadLocal.get());
                transmittableThreadLocal.set("i am a old transmittable parent");// 子线程设置新的值

            }
        });
        System.out.println(transmittableThreadLocal.get());

        TimeUnit.SECONDS.sleep(1);
        transmittableThreadLocal.set("i am a new transmittable parent");// 主线程设置新的值

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                System.out.println(transmittableThreadLocal.get());
            }
        });
    }
}
```
执行代码后发现，使用TransmittableThreadLocal和TtlExecutors.getTtlExecutorService(executorService)
装饰线程池之后， 在每次调用任务的时，都会将当前的主线程的TransmittableThreadLocal数据copy到子线程里面， 
执行完成后，再清除掉。 同时子线程里面的修改回到主线程时其实并没有生效。
这样可以保证每次任务执行的时候都是互不干涉的。
