## UncaughtException
```text
try
		{
			Thread thread = new Thread(new Task());
			thread.start();
		}
		catch (Exception e)
		{
			System.out.println("==Exception: "+e.getMessage());
		}
```
这样的异常是不会被捕获的，只能把异常捕获在子线程中
```java

public class WitchCaughtThread
{
    public static void main(String args[])
    {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        Thread thread = new Thread(new Task());
        thread.start();
    }
}

class ExceptionHandler implements UncaughtExceptionHandler
{
    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        System.out.println("==Exception: "+e.getMessage());
    }
}
```
这样可以捕获异常

但是如果通过线程池调提交线程任务，则并不会捕获，需要在runnable方法中主动设置
```text
Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler());
```
如果是submit提交的任务因为异常结束，则会封装在future.get中





