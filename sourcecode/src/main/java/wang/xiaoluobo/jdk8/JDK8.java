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
        studentList.add(new Student(4, "d",19));
        studentList.add(new Student(5, "e",18));
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
        System.out.println(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(i -> i < 6).collect(Collectors.toList()));


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
}
