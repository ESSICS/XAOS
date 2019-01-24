/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018 by European Spallation Source ERIC.
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Available thread pools to be used by almost any generic applications.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ThreadPools {

	private final static ReentrantReadWriteLock CACHED_THREAD_POOL_LOCK = new ReentrantReadWriteLock();
	private final static AtomicBoolean WAS_SHUTDOWN = new AtomicBoolean();
	private static ExecutorService cachedThreadPoolService = null;
	
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
	 */
	public static ExecutorService cachedThreadPool() {

		CACHED_THREAD_POOL_LOCK.readLock().lock();

		try {

			if ( cachedThreadPoolService == null ) {

				CACHED_THREAD_POOL_LOCK.readLock().unlock();
				CACHED_THREAD_POOL_LOCK.writeLock().lock();

				try {
					cachedThreadPoolService = Executors.newCachedThreadPool(
						r -> new Thread(r, "ThreadPools.cachedThreadPool.thread-" + Long.toHexString(System.nanoTime()))
					);
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

//	public static void shutdown 

	private ThreadPools() {
	}

}
