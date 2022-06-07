package JavaSE.steam;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @program: dailyWork
 * @description:
 * @author: RenShaobin
 * @create:2022--06--07--11:08
 **/
//Java Stream提供了提供了串行和并行两种类型的流，保持一致的接口，提供函数式编程方式，以管道方式提供中间操作和最终执行操作，为Java语言的集合提供了现代语言提供的类似的高阶函数操作，
// 简化和提高了Java集合的功能。
// 介绍
// 1.不存储数据：流是基于数据源的对象，它本身不存储数据元素，而是通过管道将数据源的元素传递给操作。
// 2.函数式编程：流的操作不会修改数据源，例如filter不会将数据源中的数据删除
// 3.延迟操作： 惰性求值，流在中间处理过程中，只是对操作进行了记录，并不会立即执行，需要等到执行终止操作的时候才会进行实际的计算。
// 4.可以解绑：对于无限数量的流，有些操作是可以在有限的时间完成的，比如limit(n) 或 findFirst()，这些操作可是实现"短路"(Short-circuiting)，访问到有限的元素后就可以返回。
// 5.纯消费：流的元素只能访问一次，类似iterator，操作没有回头路，如果你想从头访问一遍流的元素，那必须重新生成一个流。
// 6.流的操作是以管道方式串起来的，流管道包含一个数据源，接着包含0-n个中间操作，最后包含一个终点操作结束。
public class steamTest {
    public static void main(String[] args) throws FileNotFoundException {

        //1.流的创建方法
        //1.1 使用Collection下的stream和parallelStream方法
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        Stream<Integer> stream = list.stream();
        Stream<Integer> parallelStream = list.parallelStream();

        //1.2 使用Arrays的stream方法，将数组转化为流
        int[] ints = new int[]{1,2};
        IntStream intStream = Arrays.stream(ints);

        //1.3 使用stream中的静态方法，of,iterate,generate
        Stream<Integer> stream1 = Stream.of(1, 2, 3, 4, 5, 6);
        Stream<Integer> stream2 = Stream.iterate(0, (x) -> x + 2).limit(6); //0,2,4,6,8,10
        Stream<Double> stream3 = Stream.generate(Math::random).limit(2);

        //1.4 使用BufferedReader.lines()方法，将每行内容转成流
//        BufferedReader reader = new BufferedReader(new FileReader("rsb"));
//        Stream<String> lines = reader.lines();
//        lines.forEach(System.out::println);

        //1.5 使用Pattern.splitAsStream方法，将字符串分割为流
        Pattern pattern = Pattern.compile(",");
        Stream<String> stream4 = pattern.splitAsStream("a,b,c,d");
        stream4.forEach(System.out::println);

        //2.流的中间操作
        Stream<Integer> stream5 = Stream.of(6, 4, 6, 7, 3, 9, 10, 1, 2, 12, 14, 14);
        Stream<Integer> stream8 = Stream.of(6, 4, 6, 7, 3, 9, 10, 1, 2, 12, 14, 14);
        Stream<Integer> stream7 = stream8.filter(x -> x > 5)
                .distinct();

        Stream<Integer> stream6 = stream5.filter(x -> x > 5)
                .distinct()
                .skip(2)
                .limit(2);
        stream7.forEach(System.out::println);
        System.out.println("---------------");
        stream6.forEach(System.out::println);


    }
}
