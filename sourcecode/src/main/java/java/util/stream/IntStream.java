/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package java.util.stream;

import java.util.*;
import java.util.function.*;

/**
 * A sequence of primitive int-valued elements supporting sequential and parallel
 * aggregate operations.  This is the {@code int} primitive specialization of
 * {@link Stream}.
 *
 * 支持顺序和并行聚合操作的一系列原始int值元素。这是{@link Stream}的{@code int}原始特化。
 *
 * <p>The following example illustrates an aggregate operation using
 * {@link Stream} and {@link IntStream}, computing the sum of the weights of the
 * red widgets:
 *
 * 以下示例说明了使用{@link Stream}和{@link IntStream}的聚合操作，计算红色小部件的权重总和：
 *
 * <pre>{@code
 *     int sum = widgets.stream()
 *                      .filter(w -> w.getColor() == RED)
 *                      .mapToInt(w -> w.getWeight())
 *                      .sum();
 * }</pre>
 *
 * See the class documentation for {@link Stream} and the package documentation
 * for <a href="package-summary.html">java.util.stream</a> for additional
 * specification of streams, stream operations, stream pipelines, and
 * parallelism.
 *
 * 有关流，流操作，流管道和并行的其他规范，请参阅{@link Stream}的类文档和java.util.stream的包文档。
 *
 * @since 1.8
 * @see Stream
 * @see <a href="package-summary.html">java.util.stream</a>
 */
public interface IntStream extends BaseStream<Integer, IntStream> {

    /**
     * Returns a stream consisting of the elements of this stream that match
     * the given predicate.
     *
     * 返回由与给定谓词匹配的流的元素组成的流。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to each element to determine if it
     *                  should be included
     * @return the new stream
     */
    IntStream filter(IntPredicate predicate);

    /**
     * Returns a stream consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * 返回一个流，其中包含将给定函数应用于该流的元素的结果。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @return the new stream
     */
    IntStream map(IntUnaryOperator mapper);

    /**
     * Returns an object-valued {@code Stream} consisting of the results of
     * applying the given function to the elements of this stream.
     *
     * 返回一个对象值{@code Stream}，其中包含将给定函数应用于此流元素的结果。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">
     *     intermediate operation</a>.
     *
     * @param <U> the element type of the new stream
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @return the new stream
     */
    <U> Stream<U> mapToObj(IntFunction<? extends U> mapper);

    /**
     * Returns a {@code LongStream} consisting of the results of applying the
     * given function to the elements of this stream.
     *
     * 返回一个{@code LongStream}，其中包含将给定函数应用到该流元素的结果。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @return the new stream
     */
    LongStream mapToLong(IntToLongFunction mapper);

    /**
     * Returns a {@code DoubleStream} consisting of the results of applying the
     * given function to the elements of this stream.
     *
     * 返回一个{@code DoubleStream}，其中包含将给定函数应用于此流元素的结果。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @return the new stream
     */
    DoubleStream mapToDouble(IntToDoubleFunction mapper);

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of a mapped stream produced by applying
     * the provided mapping function to each element.  Each mapped stream is
     * {@link BaseStream#close() closed} after its contents
     * have been placed into this stream.  (If a mapped stream is {@code null}
     * an empty stream is used, instead.)
     *
     * 返回一个流，该流包含将此流的每个元素替换为通过将提供的映射函数应用于每个元素而生成的映射流的内容的结果。
     * 将其内容放入此流后，每个映射的流都为{@link BaseStream#close() closed}。(如果映射的流是{@code null}，则使用空流。)
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element which produces an
     *               {@code IntStream} of new values
     * @return the new stream
     * @see Stream#flatMap(Function)
     */
    IntStream flatMap(IntFunction<? extends IntStream> mapper);

    /**
     * Returns a stream consisting of the distinct elements of this stream.
     *
     * 返回由此流的不同元素组成的流。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">stateful
     * intermediate operation</a>.
     *
     * @return the new stream
     */
    IntStream distinct();

    /**
     * Returns a stream consisting of the elements of this stream in sorted
     * order.
     *
     * 以排序顺序返回由此流的元素组成的流。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">stateful
     * intermediate operation</a>.
     *
     * @return the new stream
     */
    IntStream sorted();

    /**
     * Returns a stream consisting of the elements of this stream, additionally
     * performing the provided action on each element as elements are consumed
     * from the resulting stream.
     *
     * 返回由此流的元素组成的流，另外在每个元素上执行提供的操作，因为元素是从结果流中消耗的。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * <p>For parallel stream pipelines, the action may be called at
     * whatever time and in whatever thread the element is made available by the
     * upstream operation.  If the action modifies shared state,
     * it is responsible for providing the required synchronization.
     *
     * 对于并行流管道，可以在任何时间以及上游操作使元素可用的任何线程中调用该动作。
     * 如果操作修改共享状态，则它负责提供所需的同步。
     *
     * @apiNote This method exists mainly to support debugging, where you want
     * to see the elements as they flow past a certain point in a pipeline:
     *
     * @apiNote 此方法主要用于支持调试，您希望在元素流经管道中的某个点时查看这些元素：
     *
     * <pre>{@code
     *     IntStream.of(1, 2, 3, 4)
     *         .filter(e -> e > 2)
     *         .peek(e -> System.out.println("Filtered value: " + e))
     *         .map(e -> e * e)
     *         .peek(e -> System.out.println("Mapped value: " + e))
     *         .sum();
     * }</pre>
     *
     * @param action a <a href="package-summary.html#NonInterference">
     *               non-interfering</a> action to perform on the elements as
     *               they are consumed from the stream
     * @return the new stream
     */
    IntStream peek(IntConsumer action);

    /**
     * Returns a stream consisting of the elements of this stream, truncated
     * to be no longer than {@code maxSize} in length.
     *
     * 返回由此流的元素组成的流，截断长度不超过{@code maxSize}。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * stateful intermediate operation</a>.
     *
     * @apiNote
     * While {@code limit()} is generally a cheap operation on sequential
     * stream pipelines, it can be quite expensive on ordered parallel pipelines,
     * especially for large values of {@code maxSize}, since {@code limit(n)}
     * is constrained to return not just any <em>n</em> elements, but the
     * <em>first n</em> elements in the encounter order.  Using an unordered
     * stream source (such as {@link #generate(IntSupplier)}) or removing the
     * ordering constraint with {@link #unordered()} may result in significant
     * speedups of {@code limit()} in parallel pipelines, if the semantics of
     * your situation permit.  If consistency with encounter order is required,
     * and you are experiencing poor performance or memory utilization with
     * {@code limit()} in parallel pipelines, switching to sequential execution
     * with {@link #sequential()} may improve performance.
     *
     * @apiNote
     * 虽然{@code limit()}通常是对串行流管道的廉价操作，但在有序并行管道上可能非常昂贵，
     * 特别是对于{@code maxSize}最大值，因为{@code limit(n)}受到限制不仅返回任何n个元素，
     * 而且返回遭遇顺序中的前n个元素。使用无序流源(例如{@link #generate(IntSupplier)})
     * 或使用{@link #unordered()}删除排序约束可能会导致并行管道中{@code limit()}的显着加速，
     * 如果你的情况允许的语义。如果需要与遇到顺序的一致性，并且您在并行管道中使用{@code limit()}
     * 遇到性能不佳或内存利用率较低，则使用{@link #sequential()}切换到串行执行可能会提高性能。
     *
     * @param maxSize the number of elements the stream should be limited to
     * @return the new stream
     * @throws IllegalArgumentException if {@code maxSize} is negative
     */
    IntStream limit(long maxSize);

    /**
     * Returns a stream consisting of the remaining elements of this stream
     * after discarding the first {@code n} elements of the stream.
     * If this stream contains fewer than {@code n} elements then an
     * empty stream will be returned.
     *
     * 在丢弃流的第一个{@code n}元素后，返回由此流的其余元素组成的流。如果此流包含少于{@code n}个元素，则将返回空流。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">stateful
     * intermediate operation</a>.
     *
     * @apiNote
     * While {@code skip()} is generally a cheap operation on sequential
     * stream pipelines, it can be quite expensive on ordered parallel pipelines,
     * especially for large values of {@code n}, since {@code skip(n)}
     * is constrained to skip not just any <em>n</em> elements, but the
     * <em>first n</em> elements in the encounter order.  Using an unordered
     * stream source (such as {@link #generate(IntSupplier)}) or removing the
     * ordering constraint with {@link #unordered()} may result in significant
     * speedups of {@code skip()} in parallel pipelines, if the semantics of
     * your situation permit.  If consistency with encounter order is required,
     * and you are experiencing poor performance or memory utilization with
     * {@code skip()} in parallel pipelines, switching to sequential execution
     * with {@link #sequential()} may improve performance.
     *
     * @apiNote
     * 虽然{@code skip()}通常是串行流管道上的廉价操作，但在有序并行管道上可能非常昂贵，
     * 特别是对于{@code n}的大值，因为{@code skip(n)}受到限制 不仅要跳过任何n个元素，
     * 还要跳过遇到顺序中的前n个元素。使用无序流源(例如{@link #generate(IntSupplier)})
     * 或使用{@link #unordered()}删除排序约束可能会导致并行管道中{@code skip()}的显着加速，
     * 如果你的情况允许的语义。如果需要与遇到顺序的一致性，并且您在并行管道中使用{@code skip()}
     * 遇到性能或内存利用率较低，则使用{@link #sequential()}切换到串行执行可能会提高性能。
     *
     * @param n the number of leading elements to skip
     * @return the new stream
     * @throws IllegalArgumentException if {@code n} is negative
     */
    IntStream skip(long n);

    /**
     * Performs an action for each element of this stream.
     *
     * 对此流的每个元素执行操作。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * <p>For parallel stream pipelines, this operation does <em>not</em>
     * guarantee to respect the encounter order of the stream, as doing so
     * would sacrifice the benefit of parallelism.  For any given element, the
     * action may be performed at whatever time and in whatever thread the
     * library chooses.  If the action accesses shared state, it is
     * responsible for providing the required synchronization.
     *
     * 对于并行流管道，此操作不保证遵守流的遭遇顺序，因为这样做会牺牲并行性的好处。对于任何给定元素，
     * 可以在任何时间以及库选择的任何线程中执行该动作。如果操作访问共享状态，则它负责提供所需的同步。
     *
     * @param action a <a href="package-summary.html#NonInterference">
     *               non-interfering</a> action to perform on the elements
     */
    void forEach(IntConsumer action);

    /**
     * Performs an action for each element of this stream, guaranteeing that
     * each element is processed in encounter order for streams that have a
     * defined encounter order.
     *
     * 对此流的每个元素执行操作，确保以具有已定义的遭遇顺序的流的遭遇顺序处理每个元素。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @param action a <a href="package-summary.html#NonInterference">
     *               non-interfering</a> action to perform on the elements
     * @see #forEach(IntConsumer)
     */
    void forEachOrdered(IntConsumer action);

    /**
     * Returns an array containing the elements of this stream.
     *
     * 返回包含此流的元素的数组。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @return an array containing the elements of this stream
     */
    int[] toArray();

    /**
     * Performs a <a href="package-summary.html#Reduction">reduction</a> on the
     * elements of this stream, using the provided identity value and an
     * <a href="package-summary.html#Associativity">associative</a>
     * accumulation function, and returns the reduced value.  This is equivalent
     * to:
     *
     * 使用提供的标识值和关联累加函数执行此流的元素的减少，并返回减少的值。这相当于：
     *
     * <pre>{@code
     *     int result = identity;
     *     for (int element : this stream)
     *         result = accumulator.applyAsInt(result, element)
     *     return result;
     * }</pre>
     *
     * but is not constrained to execute sequentially.
     *
     * 但不限于按串行执行。
     *
     * <p>The {@code identity} value must be an identity for the accumulator
     * function. This means that for all {@code x},
     * {@code accumulator.apply(identity, x)} is equal to {@code x}.
     * The {@code accumulator} function must be an
     * <a href="package-summary.html#Associativity">associative</a> function.
     *
     * {@code identity}值必须是累加器函数的标识。这意味着对于所有{@code x}，
     * {@code accumulator.apply(identity，x)}等于{@code x}。
     * {@code accumulator}函数必须是关联函数。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @apiNote Sum, min, max, and average are all special cases of reduction.
     * Summing a stream of numbers can be expressed as:
     *
     * @apiNote 总和，最小值，最大值和平均值都是减少的特殊情况。汇总数字流可以表示为：
     *
     * <pre>{@code
     *     int sum = integers.reduce(0, (a, b) -> a+b);
     * }</pre>
     *
     * or more compactly:
     *
     * 或者更紧凑：
     *
     * <pre>{@code
     *     int sum = integers.reduce(0, Integer::sum);
     * }</pre>
     *
     * <p>While this may seem a more roundabout way to perform an aggregation
     * compared to simply mutating a running total in a loop, reduction
     * operations parallelize more gracefully, without needing additional
     * synchronization and with greatly reduced risk of data races.
     *
     * 虽然与简单地在循环中改变运行总计相比，这似乎是更加迂回的执行聚合的方式，
     * 但是还原操作可以更优雅地并行化，而无需额外的同步并且大大降低了数据竞争的风险。
     *
     * @param identity the identity value for the accumulating function
     * @param op an <a href="package-summary.html#Associativity">associative</a>,
     *           <a href="package-summary.html#NonInterference">non-interfering</a>,
     *           <a href="package-summary.html#Statelessness">stateless</a>
     *           function for combining two values
     * @return the result of the reduction
     * @see #sum()
     * @see #min()
     * @see #max()
     * @see #average()
     */
    int reduce(int identity, IntBinaryOperator op);

    /**
     * Performs a <a href="package-summary.html#Reduction">reduction</a> on the
     * elements of this stream, using an
     * <a href="package-summary.html#Associativity">associative</a> accumulation
     * function, and returns an {@code OptionalInt} describing the reduced value,
     * if any. This is equivalent to:
     *
     * 使用关联累加函数对该流的元素执行约简，并返回一个{@code OptionalInt}，描述约简后的值(如果有)。这相当于:
     *
     * <pre>{@code
     *     boolean foundAny = false;
     *     int result = null;
     *     for (int element : this stream) {
     *         if (!foundAny) {
     *             foundAny = true;
     *             result = element;
     *         }
     *         else
     *             result = accumulator.applyAsInt(result, element);
     *     }
     *     return foundAny ? OptionalInt.of(result) : OptionalInt.empty();
     * }</pre>
     *
     * but is not constrained to execute sequentially.
     *
     * 但不受串行执行的约束。
     *
     * <p>The {@code accumulator} function must be an
     * <a href="package-summary.html#Associativity">associative</a> function.
     *
     * {@code accumulator}函数必须是一个关联函数。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @param op an <a href="package-summary.html#Associativity">associative</a>,
     *           <a href="package-summary.html#NonInterference">non-interfering</a>,
     *           <a href="package-summary.html#Statelessness">stateless</a>
     *           function for combining two values
     * @return the result of the reduction
     * @see #reduce(int, IntBinaryOperator)
     */
    OptionalInt reduce(IntBinaryOperator op);

    /**
     * Performs a <a href="package-summary.html#MutableReduction">mutable
     * reduction</a> operation on the elements of this stream.  A mutable
     * reduction is one in which the reduced value is a mutable result container,
     * such as an {@code ArrayList}, and elements are incorporated by updating
     * the state of the result rather than by replacing the result.  This
     * produces a result equivalent to:
     *
     * 对该流的元素执行可变还原操作。可变约简是这样一种情况:减值是一个可变的结果容器，
     * 例如{@code ArrayList}，通过更新结果的状态而不是替换结果来合并元素。产生的结果相当于:
     *
     * <pre>{@code
     *     R result = supplier.get();
     *     for (int element : this stream)
     *         accumulator.accept(result, element);
     *     return result;
     * }</pre>
     *
     * <p>Like {@link #reduce(int, IntBinaryOperator)}, {@code collect} operations
     * can be parallelized without requiring additional synchronization.
     *
     * 与{@link #reduce(int, IntBinaryOperator)}类似，{@code collect}操作可以并行化，不需要额外的同步。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @param <R> type of the result
     * @param supplier a function that creates a new result container. For a
     *                 parallel execution, this function may be called
     *                 multiple times and must return a fresh value each time.
     * @param accumulator an <a href="package-summary.html#Associativity">associative</a>,
     *                    <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                    <a href="package-summary.html#Statelessness">stateless</a>
     *                    function for incorporating an additional element into a result
     * @param combiner an <a href="package-summary.html#Associativity">associative</a>,
     *                    <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                    <a href="package-summary.html#Statelessness">stateless</a>
     *                    function for combining two values, which must be
     *                    compatible with the accumulator function
     * @return the result of the reduction
     * @see Stream#collect(Supplier, BiConsumer, BiConsumer)
     */
    <R> R collect(Supplier<R> supplier,
                  ObjIntConsumer<R> accumulator,
                  BiConsumer<R, R> combiner);

    /**
     * Returns the sum of elements in this stream.  This is a special case
     * of a <a href="package-summary.html#Reduction">reduction</a>
     * and is equivalent to:
     * <pre>{@code
     *     return reduce(0, Integer::sum);
     * }</pre>
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * 返回此流中的元素的和。
     *
     * @return the sum of elements in this stream
     */
    int sum();

    /**
     * Returns an {@code OptionalInt} describing the minimum element of this
     * stream, or an empty optional if this stream is empty.  This is a special
     * case of a <a href="package-summary.html#Reduction">reduction</a>
     * and is equivalent to:
     * <pre>{@code
     *     return reduce(Integer::min);
     * }</pre>
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal operation</a>.
     *
     * 返回描述该流的最小元素的{@code OptionalInt}，如果该流为空，则返回空的可选元素。
     *
     * @return an {@code OptionalInt} containing the minimum element of this
     * stream, or an empty {@code OptionalInt} if the stream is empty
     */
    OptionalInt min();

    /**
     * Returns an {@code OptionalInt} describing the maximum element of this
     * stream, or an empty optional if this stream is empty.  This is a special
     * case of a <a href="package-summary.html#Reduction">reduction</a>
     * and is equivalent to:
     * <pre>{@code
     *     return reduce(Integer::max);
     * }</pre>
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * 返回描述该流的最大元素的{@code OptionalInt}，如果该流为空，则返回空的可选元素。
     *
     * @return an {@code OptionalInt} containing the maximum element of this
     * stream, or an empty {@code OptionalInt} if the stream is empty
     */
    OptionalInt max();

    /**
     * Returns the count of elements in this stream.  This is a special case of
     * a <a href="package-summary.html#Reduction">reduction</a> and is
     * equivalent to:
     * <pre>{@code
     *     return mapToLong(e -> 1L).sum();
     * }</pre>
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal operation</a>.
     *
     * 返回此流中的元素计数。
     *
     * @return the count of elements in this stream
     */
    long count();

    /**
     * Returns an {@code OptionalDouble} describing the arithmetic mean of elements of
     * this stream, or an empty optional if this stream is empty.  This is a
     * special case of a
     * <a href="package-summary.html#Reduction">reduction</a>.
     *
     * 返回描述此流元素的算术平均值的{@code OptionalDouble}，如果该流为空，则返回可选的空值。这是减价的特例。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @return an {@code OptionalDouble} containing the average element of this
     * stream, or an empty optional if the stream is empty
     */
    OptionalDouble average();

    /**
     * Returns an {@code IntSummaryStatistics} describing various
     * summary data about the elements of this stream.  This is a special
     * case of a <a href="package-summary.html#Reduction">reduction</a>.
     *
     * 返回一个{@code IntSummaryStatistics}，描述关于此流元素的各种汇总数据。这是减价的特例。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @return an {@code IntSummaryStatistics} describing various summary data
     * about the elements of this stream
     */
    IntSummaryStatistics summaryStatistics();

    /**
     * Returns whether any elements of this stream match the provided
     * predicate.  May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the stream is empty then
     * {@code false} is returned and the predicate is not evaluated.
     *
     * 返回此流的任何元素是否与提供的谓词匹配。如果不需要确定结果，则不能对所有元素求谓词的值。
     * 如果流为空，则返回{@code false}，不计算谓词。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * @apiNote
     * This method evaluates the <em>existential quantification</em> of the
     * predicate over the elements of the stream (for some x P(x)).
     *
     * @apiNote
     * 该方法对流的元素上谓词的存在量化进行评估(对于某些x P(x))。
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to elements of this stream
     * @return {@code true} if any elements of the stream match the provided
     * predicate, otherwise {@code false}
     */
    boolean anyMatch(IntPredicate predicate);

    /**
     * Returns whether all elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     *
     * 返回此流的所有元素是否与提供的谓词匹配。如果不需要确定结果，则不能对所有元素求谓词的值。
     * 如果流为空，则返回{@code true}，谓词不计算。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * @apiNote
     * This method evaluates the <em>universal quantification</em> of the
     * predicate over the elements of the stream (for all x P(x)).  If the
     * stream is empty, the quantification is said to be <em>vacuously
     * satisfied</em> and is always {@code true} (regardless of P(x)).
     *
     * 该方法计算流元素上谓词的通用量化(对于所有x P(x))。如果流为空，则量化被认为是空的，
     * 并且总是{@code true}(不管P(x)是什么)。
     *
     * @apiNote
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to elements of this stream
     * @return {@code true} if either all elements of the stream match the
     * provided predicate or the stream is empty, otherwise {@code false}
     */
    boolean allMatch(IntPredicate predicate);

    /**
     * Returns whether no elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     *
     * 返回此流的元素是否与提供的谓词匹配。如果不需要确定结果，则不能对所有元素求谓词的值。
     * 如果流为空，则返回{@code true}，谓词不计算。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * @apiNote
     * This method evaluates the <em>universal quantification</em> of the
     * negated predicate over the elements of the stream (for all x ~P(x)).  If
     * the stream is empty, the quantification is said to be vacuously satisfied
     * and is always {@code true}, regardless of P(x).
     *
     * @apiNote
     * 该方法计算流元素(对于所有x ~P(x))上的否定谓词的通用量化。如果流为空，
     * 则量化被认为是空的，且无论P(x)如何，量化总是{@code true}。
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to elements of this stream
     * @return {@code true} if either no elements of the stream match the
     * provided predicate or the stream is empty, otherwise {@code false}
     */
    boolean noneMatch(IntPredicate predicate);

    /**
     * Returns an {@link OptionalInt} describing the first element of this
     * stream, or an empty {@code OptionalInt} if the stream is empty.  If the
     * stream has no encounter order, then any element may be returned.
     *
     * 返回描述该流的第一个元素的{@link OptionalInt}，如果该流为空，
     * 则返回空的{@code OptionalInt}。如果流没有遇到顺序，则可以返回任何元素。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * @return an {@code OptionalInt} describing the first element of this stream,
     * or an empty {@code OptionalInt} if the stream is empty
     */
    OptionalInt findFirst();

    /**
     * Returns an {@link OptionalInt} describing some element of the stream, or
     * an empty {@code OptionalInt} if the stream is empty.
     *
     * 返回描述流的某些元素的{@link OptionalInt}，如果流为空，则返回空的{@code OptionalInt}。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * <p>The behavior of this operation is explicitly nondeterministic; it is
     * free to select any element in the stream.  This is to allow for maximal
     * performance in parallel operations; the cost is that multiple invocations
     * on the same source may not return the same result.  (If a stable result
     * is desired, use {@link #findFirst()} instead.)
     *
     * 这个操作的行为是明确的不确定性的;可以自由选择流中的任何元素。这是为了在并行操作中实现最大性能;
     * 代价是对同一个源的多次调用可能不会返回相同的结果。(如果需要稳定的结果，则使用{@link #findFirst()})
     *
     * @return an {@code OptionalInt} describing some element of this stream, or
     * an empty {@code OptionalInt} if the stream is empty
     * @see #findFirst()
     */
    OptionalInt findAny();

    /**
     * Returns a {@code LongStream} consisting of the elements of this stream,
     * converted to {@code long}.
     *
     * 返回一个{@code LongStream}，包含这个流的元素，转换为{@code long}。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @return a {@code LongStream} consisting of the elements of this stream,
     * converted to {@code long}
     */
    LongStream asLongStream();

    /**
     * Returns a {@code DoubleStream} consisting of the elements of this stream,
     * converted to {@code double}.
     *
     * 返回由该流的元素组成的{@code DoubleStream}，转换为{@code double}。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @return a {@code DoubleStream} consisting of the elements of this stream,
     * converted to {@code double}
     */
    DoubleStream asDoubleStream();

    /**
     * Returns a {@code Stream} consisting of the elements of this stream,
     * each boxed to an {@code Integer}.
     *
     * 返回由该流的元素组成的{@code Stream}，每个元素被装箱为{@code Integer}。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @return a {@code Stream} consistent of the elements of this stream,
     * each boxed to an {@code Integer}
     */
    Stream<Integer> boxed();

    @Override
    IntStream sequential();

    @Override
    IntStream parallel();

    @Override
    PrimitiveIterator.OfInt iterator();

    @Override
    Spliterator.OfInt spliterator();

    // Static factories

    /**
     * Returns a builder for an {@code IntStream}.
     *
     * 返回{@code IntStream}的生成器。
     *
     * @return a stream builder
     */
    public static Builder builder() {
        return new Streams.IntStreamBuilderImpl();
    }

    /**
     * Returns an empty sequential {@code IntStream}.
     *
     * 返回一个空的序列{@code IntStream}。
     *
     * @return an empty sequential stream
     */
    public static IntStream empty() {
        return StreamSupport.intStream(Spliterators.emptyIntSpliterator(), false);
    }

    /**
     * Returns a sequential {@code IntStream} containing a single element.
     *
     * 返回包含单个元素的序列{@code IntStream}。
     *
     * @param t the single element
     * @return a singleton sequential stream
     */
    public static IntStream of(int t) {
        return StreamSupport.intStream(new Streams.IntStreamBuilderImpl(t), false);
    }

    /**
     * Returns a sequential ordered stream whose elements are the specified values.
     *
     * 返回一个按顺序排序的流，其元素是指定的值。
     *
     * @param values the elements of the new stream
     * @return the new stream
     */
    public static IntStream of(int... values) {
        return Arrays.stream(values);
    }

    /**
     * Returns an infinite sequential ordered {@code IntStream} produced by iterative
     * application of a function {@code f} to an initial element {@code seed},
     * producing a {@code Stream} consisting of {@code seed}, {@code f(seed)},
     * {@code f(f(seed))}, etc.
     *
     * 将函数{@code f}迭代应用到初始元素{@code seed}生成的无限顺序{@code IntStream}返回，
     * 生成由{@code seed}、{@code f(seed)}、{@code f(f(seed))}等组成的{@code Stream}。
     *
     * <p>The first element (position {@code 0}) in the {@code IntStream} will be
     * the provided {@code seed}.  For {@code n > 0}, the element at position
     * {@code n}, will be the result of applying the function {@code f} to the
     * element at position {@code n - 1}.
     *
     * {@code IntStream}中的第一个元素(位置{@code 0})将是提供的{@code seed}。对于{@code n > 0}，
     * 位置为{@code n}的元素，是将函数{@code f}应用于位置为{@code n - 1}的元素的结果。
     *
     * @param seed the initial element
     * @param f a function to be applied to to the previous element to produce
     *          a new element
     * @return A new sequential {@code IntStream}
     */
    public static IntStream iterate(final int seed, final IntUnaryOperator f) {
        Objects.requireNonNull(f);
        final PrimitiveIterator.OfInt iterator = new PrimitiveIterator.OfInt() {
            int t = seed;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public int nextInt() {
                int v = t;
                t = f.applyAsInt(t);
                return v;
            }
        };
        return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(
                iterator,
                Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }

    /**
     * Returns an infinite sequential unordered stream where each element is
     * generated by the provided {@code IntSupplier}.  This is suitable for
     * generating constant streams, streams of random elements, etc.
     *
     * 返回无限顺序无序流，其中每个元素由提供的{@code IntSupplier}生成。这适用于生成恒定流，随机元素流等。
     *
     * @param s the {@code IntSupplier} for generated elements
     * @return a new infinite sequential unordered {@code IntStream}
     */
    public static IntStream generate(IntSupplier s) {
        Objects.requireNonNull(s);
        return StreamSupport.intStream(
                new StreamSpliterators.InfiniteSupplyingSpliterator.OfInt(Long.MAX_VALUE, s), false);
    }

    /**
     * Returns a sequential ordered {@code IntStream} from {@code startInclusive}
     * (inclusive) to {@code endExclusive} (exclusive) by an incremental step of
     * {@code 1}.
     *
     * 通过{@code 1}的增量步骤，将{@code startInclusive}(包括)中的顺序有序{@code IntStream}返回到{@code endExclusive}(不包括)。
     *
     * @apiNote
     * <p>An equivalent sequence of increasing values can be produced
     * sequentially using a {@code for} loop as follows:
     * <pre>{@code
     *     for (int i = startInclusive; i < endExclusive ; i++) { ... }
     * }</pre>
     *
     * @param startInclusive the (inclusive) initial value
     * @param endExclusive the exclusive upper bound
     * @return a sequential {@code IntStream} for the range of {@code int}
     *         elements
     */
    public static IntStream range(int startInclusive, int endExclusive) {
        if (startInclusive >= endExclusive) {
            return empty();
        } else {
            return StreamSupport.intStream(
                    new Streams.RangeIntSpliterator(startInclusive, endExclusive, false), false);
        }
    }

    /**
     * Returns a sequential ordered {@code IntStream} from {@code startInclusive}
     * (inclusive) to {@code endInclusive} (inclusive) by an incremental step of
     * {@code 1}.
     *
     * 通过{@code 1}的增量步骤，从{@code startInclusive}(包括)到{@code endInclusive}(包括)返回顺序排序的{@code IntStream}。
     *
     * @apiNote
     * <p>An equivalent sequence of increasing values can be produced
     * sequentially using a {@code for} loop as follows:
     * <pre>{@code
     *     for (int i = startInclusive; i <= endInclusive ; i++) { ... }
     * }</pre>
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive the inclusive upper bound
     * @return a sequential {@code IntStream} for the range of {@code int}
     *         elements
     */
    public static IntStream rangeClosed(int startInclusive, int endInclusive) {
        if (startInclusive > endInclusive) {
            return empty();
        } else {
            return StreamSupport.intStream(
                    new Streams.RangeIntSpliterator(startInclusive, endInclusive, true), false);
        }
    }

    /**
     * Creates a lazily concatenated stream whose elements are all the
     * elements of the first stream followed by all the elements of the
     * second stream.  The resulting stream is ordered if both
     * of the input streams are ordered, and parallel if either of the input
     * streams is parallel.  When the resulting stream is closed, the close
     * handlers for both input streams are invoked.
     *
     * 创建一个延迟连接的流，其元素是第一个流的所有元素，后跟第二个流的所有元素。
     * 如果两个输入流都是有序的，则对所得到的流进行排序，如果任一输入流是并行的，
     * 则对其进行并行。关闭结果流时，将调用两个输入流的关闭处理程序。
     *
     * @implNote
     * Use caution when constructing streams from repeated concatenation.
     * Accessing an element of a deeply concatenated stream can result in deep
     * call chains, or even {@code StackOverflowException}.
     *
     * @implNote
     * 从重复串联构造流时要小心。访问深度连接的流的元素可能导致深度调用链，甚至{@code StackOverflowException}。
     *
     * @param a the first stream
     * @param b the second stream
     * @return the concatenation of the two input streams
     */
    public static IntStream concat(IntStream a, IntStream b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        Spliterator.OfInt split = new Streams.ConcatSpliterator.OfInt(
                a.spliterator(), b.spliterator());
        IntStream stream = StreamSupport.intStream(split, a.isParallel() || b.isParallel());
        return stream.onClose(Streams.composedClose(a, b));
    }

    /**
     * A mutable builder for an {@code IntStream}.
     *
     * {@code IntStream}的可变构建器。
     *
     * <p>A stream builder has a lifecycle, which starts in a building
     * phase, during which elements can be added, and then transitions to a built
     * phase, after which elements may not be added.  The built phase
     * begins when the {@link #build()} method is called, which creates an
     * ordered stream whose elements are the elements that were added to the
     * stream builder, in the order they were added.
     *
     * 流构建器具有生命周期，该生命周期从构建阶段开始，在此期间可以添加元素，然后转换为构建阶段，
     * 之后可能不会添加元素。构建阶段在调用{@link #build()}方法时开始，该方法创建一个有序流，
     * 其元素是添加到流构建器的元素，按添加顺序排列。
     *
     * @see IntStream#builder()
     * @since 1.8
     */
    public interface Builder extends IntConsumer {

        /**
         * Adds an element to the stream being built.
         *
         * @throws IllegalStateException if the builder has already transitioned
         * to the built state
         */
        @Override
        void accept(int t);

        /**
         * Adds an element to the stream being built.
         *
         * 向正在构建的流添加元素。
         *
         * @implSpec
         * The default implementation behaves as if:
         * <pre>{@code
         *     accept(t)
         *     return this;
         * }</pre>
         *
         * @param t the element to add
         * @return {@code this} builder
         * @throws IllegalStateException if the builder has already transitioned
         * to the built state
         */
        default Builder add(int t) {
            accept(t);
            return this;
        }

        /**
         * Builds the stream, transitioning this builder to the built state.
         * An {@code IllegalStateException} is thrown if there are further
         * attempts to operate on the builder after it has entered the built
         * state.
         *
         * 构建流，将此构建器转换为构建状态。如果在构建器进入构建状态后进一步尝试对构建器进行操作，
         * 则抛出{@code IllegalStateException}。
         *
         * @return the built stream
         * @throws IllegalStateException if the builder has already transitioned to
         * the built state
         */
        IntStream build();
    }
}
