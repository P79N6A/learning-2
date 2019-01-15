package wang.xiaoluobo.jdk8;

import com.alibaba.fastjson.JSON;
import wang.xiaoluobo.vo.Student;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JDK8 {

    public static void main(String[] args) {
        // lambda表达式
        Arrays.asList("a", "b", "c").forEach(e -> System.out.println(e));
        Arrays.asList("a", "b", "c").forEach(e -> {
            System.out.println(e);
            System.out.println(e);
        });

        Arrays.asList("a", "b", "c").sort((e1, e2) -> e1.compareTo(e2));
        Arrays.asList("a", "b", "c").sort((e1, e2) -> {
            int result = e1.compareTo(e2);
            return result;
        });
        System.out.println();

        /**
         * 引用
         * 1. 引用的类型是构造器引用，语法是Class::new，或者更一般的形式：Class<T>::new。注意：这个构造器没有参数
         * 2. 引用的类型是静态方法引用，语法是Class::static_method。注意：这个方法接受一个Car类型的参数
         * 3. 引用的类型是某个类的成员方法的引用，语法是Class::method，注意，这个方法没有定义入参
         * 4. 引用的类型是某个实例对象的成员方法的引用，语法是instance::method。注意：这个方法接受一个Car类型的参数
         */
        // 1
        final Car car = Car.create(Car::new);
        final List<Car> cars = Arrays.asList(car);

        // 2
        cars.forEach(Car::collide);

        // 3
        cars.forEach(Car::repair);

        // 4
        final Car police = Car.create(Car::new);
        cars.forEach(police::follow);
        System.out.println();


        // stream api
        Collection<Streams.MyTask> tasks = Arrays.asList(
                new Streams.MyTask(Streams.MyStatus.OPEN, 35),
                new Streams.MyTask(Streams.MyStatus.OPEN, 15),
                new Streams.MyTask(Streams.MyStatus.CLOSED, 50)
        );

        // 求open状态的和
        long totalPointsOfOpenTasks = tasks
                .stream()
                .filter(myTask -> myTask.getMyStatus() == Streams.MyStatus.OPEN)
                .mapToInt(Streams.MyTask::getPoints)
                .sum();
        // 50
        System.out.println("Total Open points: " + totalPointsOfOpenTasks);

        // 求和
        double totalPoints = tasks
                .stream()
                .parallel()
//                .map(myTask -> myTask.getPoints())
                .map(Streams.MyTask::getPoints)
                .reduce(0, Integer::sum);
        // 100.0
        System.out.println("Total points: " + totalPoints);

        // 分组
        Map<Streams.MyStatus, List<Streams.MyTask>> map = tasks
                .stream()
                .collect(Collectors.groupingBy(Streams.MyTask::getMyStatus));
        // {OPEN=[[OPEN, 35], [OPEN, 15]], CLOSED=[[CLOSED, 50]]}
        System.out.println(map);

        // 计算任务占比
        final Collection<String> result = tasks
                .stream()
                .mapToInt(Streams.MyTask::getPoints)
                .asLongStream()
                .mapToDouble(points -> points / totalPoints)
                .boxed()
                .mapToLong(weigth -> (long) (weigth * 100))
                .mapToObj(percentage -> percentage + "%")
                .collect(Collectors.toList());

        // [35%, 15%, 50%]
        System.out.println(result);

        // 读取文件
        final Path path = new File("/Users/wyd/ws/learning/sourcecode/sourcecode.md").toPath();
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            lines.onClose(() -> System.out.println("Done!")).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();


        // date/datetime
        final Clock clock = Clock.systemUTC();
        System.out.println(clock.instant());
        System.out.println(clock.millis());

        final LocalDate date = LocalDate.now();
        final LocalDate dateFromClock = LocalDate.now(clock);
        System.out.println(date);
        System.out.println(dateFromClock);

        final LocalTime time = LocalTime.now();
        final LocalTime timeFromClock = LocalTime.now(clock);
        System.out.println(time);
        System.out.println(timeFromClock);

        final LocalDateTime datetime = LocalDateTime.now();
        final LocalDateTime datetimeFromClock = LocalDateTime.now(clock);
        System.out.println(datetime);
        System.out.println(datetimeFromClock);

        final ZonedDateTime zonedDatetime = ZonedDateTime.now();
        final ZonedDateTime zonedDatetimeFromClock = ZonedDateTime.now(clock);
        final ZonedDateTime zonedDatetimeFromZone = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        System.out.println(zonedDatetime);
        System.out.println(zonedDatetimeFromClock);
        System.out.println(zonedDatetimeFromZone);

        final LocalDateTime from = LocalDateTime.of(2015, Month.APRIL, 16, 0, 0, 0);
        final LocalDateTime to = LocalDateTime.of(2016, Month.APRIL, 16, 23, 59, 59);
        final Duration duration = Duration.between(from, to);
        System.out.println("Duration in days: " + duration.toDays());
        System.out.println("Duration in hours: " + duration.toHours());
        System.out.println();

        // 并行数组
        long[] arrayOfLong = new long[20000];
        Arrays.parallelSetAll(arrayOfLong, index -> ThreadLocalRandom.current().nextInt(1000000));
        Arrays.stream(arrayOfLong).limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();

        Arrays.parallelSort(arrayOfLong);
        Arrays.stream(arrayOfLong).limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();


        // thread
        Thread t = new Thread(() -> {
            System.out.println("-------------");
            System.out.println("-------------");
            System.out.println("-------------");
        });
        t.start();

        Thread t1 = new Thread(() -> System.out.println("*************"));
        t1.start();
        System.out.println();

        // test
        List<Student> studentList = new ArrayList<>();
        studentList.add(new Student(1, "a", 20));
        studentList.add(new Student(2, "b", 20));
        studentList.add(new Student(3, "c", 18));
        studentList.add(new Student(4, "d", 19));
        studentList.add(new Student(5, "e", 18));
        studentList.add(new Student(6, "f", 18));

        Map<Integer, List<Student>> stuMap = studentList.stream().collect(Collectors.groupingBy(Student::getAge));
        System.out.println(JSON.toJSONString(stuMap));

        int ages = studentList.stream().filter(student -> student.getAge() != 20).mapToInt(Student::getAge).reduce(0, Integer::sum);
        System.out.println(ages);

        // distinct保证输出的流中包含唯一的元素，它是通过Object.equals(Object)来检查是否包含相同的元素。
        System.out.println(Stream.of("a", "b", "c", "d", "e").distinct().collect(Collectors.toList()));

        // filter返回的流中只包含满足断言(predicate)的数据。
        System.out.println(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(i -> i < 6).collect(Collectors.toList()));

        // map方法将流中的元素映射成另外的值，新的值类型可以和原来的元素的类型不同。
        System.out.println(Stream.of("a", "b", "c", "d", "e").map(h -> h.hashCode()).collect(Collectors.toList()));

        // flatmap方法混合了map + flattern的功能，它将映射后的流的元素全部放入到一个新的流中。
        String poetry = "Where, before me, are the ages that have gone?\n" +
                "And where, behind me, are the coming generations?\n" +
                "I think of heaven and earth, without limit, without end,\n" +
                "And I am all alone and my tears fall down.";
        Stream<String> lines = Arrays.stream(poetry.split("\n"));
        Stream<String> words = lines.flatMap(line -> Arrays.stream(line.split(" ")));
        List<String> wordsList = words.map(w -> {
            if (w.endsWith(",") || w.endsWith(".") || w.endsWith("?")) {
                return w.substring(0, w.length() - 1).trim().toLowerCase();
            } else {
                return w.trim().toLowerCase();
            }
        }).distinct().sorted().collect(Collectors.toList());
        // [ages, all, alone, am, and, are, before, behind, coming, down, earth, end, fall, generations, gone, have, heaven, i, limit, me, my, of, tears, that, the, think, where, without]
        System.out.println(wordsList);

        // limit方法指定数量的元素的流。对于串行流，这个方法是有效的，这是因为它只需返回前n个元素即可，但是对于有序的并行流，它可能花费相对较长的时间，如果你不在意有序，可以将有序并行流转换为无序的，可以提高性能。
        System.out.println(IntStream.range(1, 100).limit(5).boxed().collect(Collectors.toList()));

        // peek方法方法会使用一个Consumer消费流中的元素，但是返回的流还是包含原来的流中的元素。
        System.out.println(Stream.of("a", "b", "c", "d", "e").peek(System.out::println).count());

        // [b_123, b#632, c+342, d_123]
        System.out.println(Stream.of("b_123", "c+342", "b#632", "d_123").sorted((c1, c2) -> {
            if (c1.charAt(0) == c2.charAt(0)) {
                return c1.substring(2).compareTo(c2.substring(2));
            } else {
                return c1.charAt(0) - c2.charAt(0);
            }
        }).collect(Collectors.toList()));

        // skip返回丢弃了前n个元素的流，如果流中的元素小于或者等于n，则返回空的流。
        // [d, e]
        System.out.println(Stream.of("a", "b", "c", "d", "e").skip(3).collect(Collectors.toList()));

        /**
         * Match
         * (1) allMatch只有在所有的元素都满足断言时才返回true,否则flase,流为空时总是返回true
         * (2) anyMatch只有在任意一个元素满足断言时就返回true,否则flase
         * (3) noneMatch只有在所有的元素都不满足断言时才返回true,否则flase
         */
        System.out.println(Stream.of(1, 2, 3, 4, 5).allMatch(i -> i > 0));  //true
        System.out.println(Stream.of(1, 2, 3, 4, 5).anyMatch(i -> i > 0));  //true
        System.out.println(Stream.of(1, 2, 3, 4, 5).noneMatch(i -> i > 0)); //false
        System.out.println(Stream.<Integer>empty().allMatch(i -> i > 0));   //true
        System.out.println(Stream.<Integer>empty().anyMatch(i -> i > 0));   //false
        System.out.println(Stream.<Integer>empty().noneMatch(i -> i > 0));  //true

        // count方法返回流中的元素的数量
        System.out.println(Stream.of("a", "b", "c", "d", "e").count());

        /**
         * collect使用一个collector执行mutable reduction操作。
         * 辅助类Collectors提供了很多的collector，可以满足我们日常的需求，
         * 你也可以创建新的collector实现特定的需求。它是一个值得关注的类，
         * 你需要熟悉这些特定的收集器，如聚合类averagingInt、最大最小值maxBy minBy、
         * 计数counting、分组groupingBy、字符串连接joining、分区partitioningBy、
         * 汇总summarizingInt、化简reducing、转换toXXX等。
         */
        // ["a","b","c","d","e"]
        System.out.println(JSON.toJSONString(Stream.of("a", "b", "c", "d", "e").collect(ArrayList::new, ArrayList::add, ArrayList::addAll)));

        // abcde
        System.out.println(Stream.of("a", "b", "c", "d", "e").collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString());

        /**
         * find
         * (1) findAny()返回任意一个元素，如果流为空，返回空的Optional，对于并行流来说，它只需要返回任意一个元素即可，所以性能可能要好于findFirst()，但是有可能多次执行的时候返回的结果不一样。
         * (2) findFirst()返回第一个元素，如果流为空，返回空的Optional。
         */
        System.out.println(Stream.of("a", "b", "c", "d", "e").findAny());
        System.out.println(Stream.of("a", "b", "c", "d", "e").findFirst());

        /**
         * forEach、forEachOrdered
         * forEach遍历流的每一个元素，执行指定的action。它是一个终点操作，和peek方法不同。
         * 这个方法不担保按照流的encounter order顺序执行，如果对于有序流按照它的encounter order顺序执行，
         * 你可以使用forEachOrdered方法。
         */
        Stream.of("a", "b", "c", "d", "e").forEach(s -> System.out.println(s));
        Stream.of("a", "b", "c", "d", "e").forEachOrdered(s -> System.out.println(s));

        // max、min
        System.out.println(Stream.of(1, 2, 3, 4, 5).max(((o1, o2) -> o1 - o2)).get());
        System.out.println(Stream.of(1, 2, 3, 4, 5).min(Comparator.comparingInt(Integer::intValue)).get());

        // reduce是常用的一个方法，事实上很多操作都是基于它实现的。
        System.out.println(Stream.of(1, 2, 3, 4, 5).reduce((x, y) -> x + y).get());
        System.out.println(Stream.of(1, 2, 3, 4, 5).reduce(100, (x, y) -> x + y));

        // toArray()
        System.out.println(JSON.toJSONString(Stream.of(1, 2, 3, 4, 5).toArray()));
        System.out.println();

        MyInterface myInterface = (s) -> System.out.println(s);
        myInterface.test("myInterface");
    }

    public static class Car {
        public static Car create(final Supplier<Car> supplier) {
            return supplier.get();
        }

        public static void collide(final Car car) {
            System.out.println("Collided " + car.toString());
        }

        public void follow(final Car another) {
            System.out.println("Following " + another.toString());
        }

        public void repair() {
            System.out.println("Repaired " + this.toString());
        }
    }

    interface MyInterface {
        void test(String s);
    }
}
