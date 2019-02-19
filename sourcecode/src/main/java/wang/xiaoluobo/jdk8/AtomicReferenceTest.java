package wang.xiaoluobo.jdk8;

import java.util.concurrent.atomic.AtomicReference;

/**
 * AtomicReference可以保证对象引用的原子性
 */
public class AtomicReferenceTest {

    private static Person person;

    private static AtomicReference<Person> ar;


    public static void main(String[] args) throws Exception {
        person = new Person("Tom", 18);
        ar = new AtomicReference<>(person);
        System.out.println("Atomic Person is " + ar.get().toString());

        Thread t1 = new Thread(new Task1());
        Thread t2 = new Thread(new Task2());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Now Atomic Person is " + ar.get().toString());

//        Atomic Person is [name: Tom, age: 18]
//        Thread1 Atomic References [name: Tom1, age: 19]
//        Thread2 Atomic References [name: Tom2, age: 21]
//        Now Atomic Person is [name: Tom2, age: 21]
    }

    static class Task1 implements Runnable {
        public void run() {
            ar.getAndSet(new Person("Tom1", ar.get().getAge() + 1));
            System.out.println("Thread1 Atomic References " + ar.get().toString());
        }
    }

    static class Task2 implements Runnable {
        public void run() {
            ar.getAndSet(new Person("Tom2", ar.get().getAge() + 2));
            System.out.println("Thread2 Atomic References " + ar.get().toString());
        }
    }
}

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String toString() {
        return "[name: " + this.name + ", age: " + this.age + "]";
    }
}
