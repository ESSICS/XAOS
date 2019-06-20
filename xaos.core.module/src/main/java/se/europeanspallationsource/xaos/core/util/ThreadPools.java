/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.core.util;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;


/**
 * Available thread pools to be used by almost any generic applications.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ThreadPools {


	private final static ReentrantReadWriteLock CACHED_THREAD_POOL_LOCK = new ReentrantReadWriteLock();
	private final static ReentrantReadWriteLock FIXED_THREAD_POOL_LOCK = new ReentrantReadWriteLock();
	private static final Logger LOGGER = Logger.getLogger(ThreadPools.class.getName());
	private final static ReentrantReadWriteLock SINGLE_THREAD_EXECUTOR_LOCK = new ReentrantReadWriteLock();
	private final static AtomicBoolean WAS_REGISTERED = new AtomicBoolean(false);
	private final static AtomicBoolean WAS_SHUTDOWN = new AtomicBoolean(false);
	private final static ReentrantReadWriteLock WORK_STEALING_THREAD_POOL_LOCK = new ReentrantReadWriteLock();

	private static ExecutorService cachedThreadPoolService = null;
	private static ScheduledExecutorService fixedThreadPoolService = null;
	private static ScheduledExecutorService singleThreadExecutorService = null;
	private static ExecutorService workStealingThreadPoolService = null;

	/**
	 * Returns a thread pool that creates new threads as needed, but will reuse
	 * previously constructed threads when they are available.
	 * <p>
	 * The returned pool can grow indefinitely and must be used with care. It is
	 * best suited for I/O tasks, such as reading and writing databases, web
	 * requests, and disk storage. Those tasks often have idle time waiting for
	 * the data to be sent or come back.</p>
	 *
	 * @return The cached thread pool.
	 * @throws IllegalStateException If the thread pull should be lazily created
	 *                               and the thread pools were already shutdown
	 *                               by calling {@link #shutdown()}.
	 * @see Executors#newCachedThreadPool()
	 */
	public static ExecutorService cachedThreadPool() throws IllegalStateException {

		CACHED_THREAD_POOL_LOCK.readLock().lock();

		try {

			if ( cachedThreadPoolService == null ) {

				CACHED_THREAD_POOL_LOCK.readLock().unlock();
				CACHED_THREAD_POOL_LOCK.writeLock().lock();

				try {
					if ( cachedThreadPoolService == null && !WAS_SHUTDOWN.get() ) {

						cachedThreadPoolService = Executors.newCachedThreadPool(
							r -> new Thread(r, "ThreadPools.cachedThreadPool.thread-" + Long.toHexString(System.nanoTime()))
						);

						registerShutdownHook();

					}
				} finally {
					CACHED_THREAD_POOL_LOCK.readLock().lock();
					CACHED_THREAD_POOL_LOCK.writeLock().unlock();
				}

			}

			return cachedThreadPoolService;

		} finally {
			CACHED_THREAD_POOL_LOCK.readLock().unlock();
		}

	}

	/**
	 * Returns a thread pool that reuses a fixed number of threads (equals to
	 * the number of available processors) operating off a shared unbounded
	 * queue, and that can schedule commands to run after a given delay, or to
	 * execute periodically. At any point, at most <i>n-threads</i> will be
	 * active processing tasks. If additional tasks are submitted when all
	 * threads are active, they will wait in the queue until a thread is
	 * available. If any thread terminates due to a failure during execution
	 * prior to shutdown, a new one will take its place if needed to execute
	 * subsequent tasks.
	 * <p>
	 * It is best suited for computation-intensive operations, because it will
	 * maintain a conservative number of threads to keep the CPU from being
	 * taxed.</p>
	 *
	 * @return The fixed thread pool.
	 * @throws IllegalStateException If the thread pull should be lazily created
	 *                               and the thread pools were already shutdown
	 *                               by calling {@link #shutdown()}.
	 * @see Runtime#availableProcessors()
	 * @see Executors#newScheduledThreadPool(int)
	 */
	public static ScheduledExecutorService fixedThreadPool() throws IllegalStateException {

		FIXED_THREAD_POOL_LOCK.readLock().lock();

		try {

			if ( fixedThreadPoolService == null ) {

				FIXED_THREAD_POOL_LOCK.readLock().unlock();
				FIXED_THREAD_POOL_LOCK.writeLock().lock();

				try {
					if ( fixedThreadPoolService == null && !WAS_SHUTDOWN.get() ) {

						fixedThreadPoolService = Executors.newScheduledThreadPool(
							Runtime.getRuntime().availableProcessors(),
							r -> new Thread(r, "ThreadPools.fixedThreadPool.thread-" + Long.toHexString(System.nanoTime()))
						);

						registerShutdownHook();

					}
				} finally {
					FIXED_THREAD_POOL_LOCK.readLock().lock();
					FIXED_THREAD_POOL_LOCK.writeLock().unlock();
				}

			}

			return fixedThreadPoolService;

		} finally {
			FIXED_THREAD_POOL_LOCK.readLock().unlock();
		}

	}

	/**
	 * @return The JDK {@link ForkJoinPool#commonPool()}.
	 */
	public static ForkJoinPool jdkCommonPool() {
		return ForkJoinPool.commonPool();
	}

	/**
	 * Returns a single-threaded executor (a degenerated pool) that can schedule
	 * commands to run after a given delay, or to execute periodically,
	 * operating off an unbounded queue (note however that if this single thread
	 * terminates due to a failure during execution prior to shutdown, a new one
	 * will take its place if needed to execute subsequent tasks). Tasks are
	 * guaranteed to execute sequentially, and no more than one task will be
	 * active at any given time.
	 *
	 * @return The single thread pool.
	 * @throws IllegalStateException If the thread pull should be lazily created
	 *                               and the thread pools were already shutdown
	 *                               by calling {@link #shutdown()}.
	 * @see Executors#newSingleThreadScheduledExecutor()
	 */
	public static ScheduledExecutorService singleThreadExecutor() throws IllegalStateException {

		SINGLE_THREAD_EXECUTOR_LOCK.readLock().lock();

		try {

			if ( singleThreadExecutorService == null ) {

				SINGLE_THREAD_EXECUTOR_LOCK.readLock().unlock();
				SINGLE_THREAD_EXECUTOR_LOCK.writeLock().lock();

				try {
					if ( singleThreadExecutorService == null && !WAS_SHUTDOWN.get() ) {

						singleThreadExecutorService = Executors.newSingleThreadScheduledExecutor(
							r -> new Thread(r, "ThreadPools.singleThreadExecutor.thread-" + Long.toHexString(System.nanoTime()))
						);

						registerShutdownHook();

					}
				} finally {
					SINGLE_THREAD_EXECUTOR_LOCK.readLock().lock();
					SINGLE_THREAD_EXECUTOR_LOCK.writeLock().unlock();
				}

			}

			return singleThreadExecutorService;

		} finally {
			SINGLE_THREAD_EXECUTOR_LOCK.readLock().unlock();
		}

	}

	/**
	 * Returns a thread pool that maintains enough threads to support a given
	 * parallelism level (equals to the number of available processors), and may
	 * use multiple queues to reduce contention. The parallelism level
	 * corresponds to the maximum number of threads actively engaged in, or
	 * available to engage in, task processing. The actual number of threads may
	 * grow and shrink dynamically. A work-stealing pool makes no guarantees
	 * about the order in which submitted tasks are executed.
	 * <p>
	 * It is best suited for heavy-load situations, o when a running thread is
	 * recursively creating other threads.</p>
	 *
	 * @return The fixed thread pool.
	 * @throws IllegalStateException If the thread pull should be lazily created
	 *                               and the thread pools were already shutdown
	 *                               by calling {@link #shutdown()}.
	 * @see Runtime#availableProcessors()
	 * @see Executors#newWorkStealingPool()
	 */
	public static ExecutorService workStealingThreadPool() throws IllegalStateException {

		WORK_STEALING_THREAD_POOL_LOCK.readLock().lock();

		try {

			if ( workStealingThreadPoolService == null ) {

				WORK_STEALING_THREAD_POOL_LOCK.readLock().unlock();
				WORK_STEALING_THREAD_POOL_LOCK.writeLock().lock();

				try {
					if ( workStealingThreadPoolService == null && !WAS_SHUTDOWN.get() ) {

						workStealingThreadPoolService = Executors.newWorkStealingPool(
							Runtime.getRuntime().availableProcessors()
						);

						registerShutdownHook();

					}
				} finally {
					WORK_STEALING_THREAD_POOL_LOCK.readLock().lock();
					WORK_STEALING_THREAD_POOL_LOCK.writeLock().unlock();
				}

			}

			return workStealingThreadPoolService;

		} finally {
			WORK_STEALING_THREAD_POOL_LOCK.readLock().unlock();
		}

	}

	private static void registerShutdownHook() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	/**
	 * Initiates an orderly shutdown of all the thread pools, in which
	 * previously submitted tasks are executed, but no new tasks will be
	 * accepted.
	 * <p>
	 * This method does not wait for previously submitted tasks to complete
	 * execution. Use {@code awaitTermination} on the thread pool you're
	 * interested in to do that.</p>
	 * <p>
	 * This method doesn't need to be explicitly called, because automatically
	 * done by a shutdown hook.</p>
	 *
	 * @see ExecutorService#shutdown()
	 */
	private static void shutdown() {

		if ( WAS_SHUTDOWN.compareAndSet(false, true) ) {

			//	---- cached thread pool ----------------------------------------
			CACHED_THREAD_POOL_LOCK.writeLock().lock();

			try {
				if ( cachedThreadPoolService != null ) {
					cachedThreadPoolService.shutdown();
				}
			} finally {
				CACHED_THREAD_POOL_LOCK.writeLock().unlock();
			}

			//	---- fixed thread pool -----------------------------------------
			FIXED_THREAD_POOL_LOCK.writeLock().lock();

			try {
				if ( fixedThreadPoolService != null ) {
					fixedThreadPoolService.shutdown();
				}
			} finally {
				FIXED_THREAD_POOL_LOCK.writeLock().unlock();
			}

			//	---- single thread executor ------------------------------------
			SINGLE_THREAD_EXECUTOR_LOCK.writeLock().lock();

			try {
				if ( singleThreadExecutorService != null ) {
					singleThreadExecutorService.shutdown();
				}
			} finally {
				SINGLE_THREAD_EXECUTOR_LOCK.writeLock().unlock();
			}

			//	---- work stealing thread pool ---------------------------------
			WORK_STEALING_THREAD_POOL_LOCK.writeLock().lock();

			try {
				if ( workStealingThreadPoolService != null ) {
					workStealingThreadPoolService.shutdown();
				}
			} finally {
				WORK_STEALING_THREAD_POOL_LOCK.writeLock().unlock();
			}

		}

	}

	private ThreadPools() {
		if ( WAS_REGISTERED.compareAndSet(false, true) ) {
			Runtime.getRuntime().addShutdownHook(new Thread(ThreadPools::shutdown, "ThreadPools Shutdown Hook"));
		}
	}

}
