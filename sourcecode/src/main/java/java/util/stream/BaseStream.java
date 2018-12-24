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

import java.util.Iterator;
import java.util.Spliterator;

/**
 * Base interface for streams, which are sequences of elements supporting
 * sequential and parallel aggregate operations.  The following example
 * illustrates an aggregate operation using the stream types {@link Stream}
 * and {@link IntStream}, computing the sum of the weights of the red widgets:
 *
 * 流的基本接口，它是支持顺序和并行聚合操作的元素序列。以下示例说明了使用流类型{@link Stream}和{@link IntStream}的聚合操作，
 * 计算红色小部件的权重总和：
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
 * parallelism, which governs the behavior of all stream types.
 *
 * 有关流，流操作，流管道和并行的其他规范，请参阅{@link Stream}的类文档和java.util.stream的包文档，它们管理所有流类型的行为。
 *
 * @param <T> the type of the stream elements
 * @param <S> the type of of the stream implementing {@code BaseStream}
 * @since 1.8
 * @see Stream
 * @see IntStream
 * @see LongStream
 * @see DoubleStream
 * @see <a href="package-summary.html">java.util.stream</a>
 */
public interface BaseStream<T, S extends BaseStream<T, S>>
        extends AutoCloseable {
    /**
     * Returns an iterator for the elements of this stream.
     *
     * 返回此流的元素的迭代器。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @return the element iterator for this stream
     */
    Iterator<T> iterator();

    /**
     * Returns a spliterator for the elements of this stream.
     *
     * 返回此流元素的spliterator。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @return the element spliterator for this stream
     */
    Spliterator<T> spliterator();

    /**
     * Returns whether this stream, if a terminal operation were to be executed,
     * would execute in parallel.  Calling this method after invoking an
     * terminal stream operation method may yield unpredictable results.
     *
     * 返回此流(如果要执行终端操作)是否将并行执行。在调用终端流操作方法之后调用此方法可能会产生不可预测的结果。
     *
     * @return {@code true} if this stream would execute in parallel if executed
     */
    boolean isParallel();

    /**
     * Returns an equivalent stream that is sequential.  May return
     * itself, either because the stream was already sequential, or because
     * the underlying stream state was modified to be sequential.
     *
     * 返回顺序的等效流。可能会返回自己，因为流已经是顺序的，或者因为基础流状态被修改为顺序。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @return a sequential stream
     */
    S sequential();

    /**
     * Returns an equivalent stream that is parallel.  May return
     * itself, either because the stream was already parallel, or because
     * the underlying stream state was modified to be parallel.
     *
     * 返回并行的等效流。可能会返回自己，因为流已经并行，或者因为基础流状态被修改为并行。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @return a parallel stream
     */
    S parallel();

    /**
     * Returns an equivalent stream that is
     * <a href="package-summary.html#Ordering">unordered</a>.  May return
     * itself, either because the stream was already unordered, or because
     * the underlying stream state was modified to be unordered.
     *
     * 返回无序的等效流。可能会返回自己，因为流已经无序，或者因为基础流状态被修改为无序。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @return an unordered stream
     */
    S unordered();

    /**
     * Returns an equivalent stream with an additional close handler.  Close
     * handlers are run when the {@link #close()} method
     * is called on the stream, and are executed in the order they were
     * added.  All close handlers are run, even if earlier close handlers throw
     * exceptions.  If any close handler throws an exception, the first
     * exception thrown will be relayed to the caller of {@code close()}, with
     * any remaining exceptions added to that exception as suppressed exceptions
     * (unless one of the remaining exceptions is the same exception as the
     * first exception, since an exception cannot suppress itself.)  May
     * return itself.
     *
     * 返回具有附加关闭处理程序的等效流。在流上调用{@link #close()}方法时，将运行关闭处理程序，并按添加顺序执行。
     * 即使先前的关闭处理程序抛出异常，也会运行所有关闭处理程序。如果任何关闭处理程序抛出异常，
     * 则抛出的第一个异常将被中继到{@code close()}的调用者，并将任何剩余的异常作为抑制异常添加到该异常中
     * (除非剩下的异常之一是与第一个例外，因为异常无法抑制自身)可能会返回自己。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @param closeHandler A task to execute when the stream is closed
     * @return a stream with a handler that is run if the stream is closed
     */
    S onClose(Runnable closeHandler);

    /**
     * Closes this stream, causing all close handlers for this stream pipeline
     * to be called.
     *
     * 关闭此流，导致调用此流管道的所有关闭处理程序。
     *
     * @see AutoCloseable#close()
     */
    @Override
    void close();
}
