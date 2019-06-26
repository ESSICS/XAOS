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


import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.europeanspallationsource.xaos.core.util.spi.LogHandlerProvider;
import se.europeanspallationsource.xaos.tools.annotation.ServiceLoaderUtilities;


/**
 * Utilities for logging messages.
 *
 * @author claudio.rosati@esss.se
 */
public class LogUtils {

	private static final String FORMAT = "{0}:{1,number,###############0}";
	private static final Map<Level, Set<LogHandler>> HANDLERS = new ConcurrentHashMap<>(1);
	private static final Logger LOGGER = Logger.getLogger(LogUtils.class.getName());

	/**
	 * Static initializer used to discover and register handler.
	 */
	static {
		ServiceLoaderUtilities.of(ServiceLoader.load(LogHandlerProvider.class)).forEach(
			provider -> registerHandle(provider.getLevel(), provider.getHandler())
		);
	}

	/**
	 * Log a message.
	 *
	 * @param logger  The {@link Logger} to log the message.
	 * @param level   One of the message level identifiers, e.g.
	 *                {@link Level#SEVERE}.
	 * @param message The string message.
	 */
	public static void log( Logger logger, Level level, String message ) {

		StackTraceElement trace = new Exception().getStackTrace()[1];

		logp(
			logger,
			level,
			trace.getClassName(),
			MessageFormat.format(FORMAT, trace.getMethodName(), trace.getLineNumber()),
			message
		);

	}

	/**
	 * Log a message.
	 *
	 * @param logger     The {@link Logger} to log the message.
	 * @param className  The name of the class to be logged.
	 * @param methodName The name of the method to be logged.
	 * @param level      One of the message level identifiers, e.g.
	 *                   {@link Level#SEVERE}.
	 * @param message    The string message. It can contains parameters.
	 * @see MessageFormat
	 */
	public static void log( Logger logger, String className, String methodName, Level level, String message ) {
		logp(logger, level, className, methodName, message);
	}

	/**
	 * Log a message.
	 *
	 * @param logger     The {@link Logger} to log the message.
	 * @param level      One of the message level identifiers, e.g.
	 *                   {@link Level#SEVERE}.
	 * @param message    The string message. It can contains parameters.
	 * @param parameters Array of parameters to the message.
	 * @see MessageFormat
	 */
	public static void log( Logger logger, Level level, String message, Object... parameters ) {

		StackTraceElement trace = new Exception().getStackTrace()[1];

		logp(
			logger,
			level,
			trace.getClassName(),
			MessageFormat.format(FORMAT, trace.getMethodName(), trace.getLineNumber()),
			MessageFormat.format(message, parameters)
		);

	}

	/**
	 * Log a message.
	 *
	 * @param logger     The {@link Logger} to log the message.
	 * @param className  The name of the class to be logged.
	 * @param methodName The name of the method to be logged.
	 * @param level      One of the message level identifiers, e.g.
	 *                   {@link Level#SEVERE}.
	 * @param message    The string message. It can contains parameters.
	 * @param parameters Array of parameters to the message.
	 * @see MessageFormat
	 */
	public static void log( Logger logger, String className, String methodName, Level level, String message, Object... parameters ) {
		logp(logger, level, className, methodName, MessageFormat.format(message, parameters));
	}

	/**
	 * Log a message, with associated {@link Throwable} information.
	 *
	 * @param logger The {@link Logger} to log the message.
	 * @param level  One of the message level identifiers, e.g.
	 *               {@link Level#SEVERE}.
	 * @param thrown {@link Throwable} associated with log message.
	 */
	public static void log( Logger logger, Level level, Throwable thrown ) {

		StackTraceElement trace = new Exception().getStackTrace()[1];

		logp(
			logger,
			level,
			trace.getClassName(),
			MessageFormat.format(FORMAT, trace.getMethodName(), trace.getLineNumber()),
			null,
			thrown
		);

	}

	/**
	 * Log a message, with associated {@link Throwable} information.
	 *
	 * @param logger     The {@link Logger} to log the message.
	 * @param className  The name of the class to be logged.
	 * @param methodName The name of the method to be logged.
	 * @param level      One of the message level identifiers, e.g.
	 *                   {@link Level#SEVERE}.
	 * @param thrown     {@link Throwable} associated with log message.
	 */
	public static void log( Logger logger, String className, String methodName, Level level, Throwable thrown ) {
		logp(logger, level, className, methodName, null, thrown);
	}

	/**
	 * Log a message, with associated {@link Throwable} information.
	 *
	 * @param logger  The {@link Logger} to log the message.
	 * @param level   One of the message level identifiers, e.g.
	 *                {@link Level#SEVERE}.
	 * @param thrown  {@link Throwable} associated with log message.
	 * @param message The string message.
	 */
	public static void log( Logger logger, Level level, Throwable thrown, String message ) {

		StackTraceElement trace = new Exception().getStackTrace()[1];

		logp(
			logger,
			level,
			trace.getClassName(),
			MessageFormat.format(FORMAT, trace.getMethodName(), trace.getLineNumber()),
			message,
			thrown
		);

	}

	/**
	 * Log a message, with associated {@link Throwable} information.
	 *
	 * @param logger     The {@link Logger} to log the message.
	 * @param className  The name of the class to be logged.
	 * @param methodName The name of the method to be logged.
	 * @param level      One of the message level identifiers, e.g.
	 *                   {@link Level#SEVERE}.
	 * @param thrown     {@link Throwable} associated with log message.
	 * @param message    The string message. It can contains parameters.
	 * @see MessageFormat
	 */
	public static void log( Logger logger, String className, String methodName, Level level, Throwable thrown, String message ) {
		logp(logger, level, className, methodName, message, thrown);
	}

	/**
	 * Log a message, with associated {@link Throwable} information.
	 *
	 * @param logger     The {@link Logger} to log the message.
	 * @param level      One of the message level identifiers, e.g.
	 *                   {@link Level#SEVERE}.
	 * @param thrown     {@link Throwable} associated with log message.
	 * @param message    The string message. It can contains parameters.
	 * @param parameters Array of parameters to the message.
	 * @see MessageFormat
	 */
	public static void log( Logger logger, Level level, Throwable thrown, String message, Object... parameters ) {

		StackTraceElement trace = new Exception().getStackTrace()[1];

		logp(
			logger,
			level,
			trace.getClassName(),
			MessageFormat.format(FORMAT, trace.getMethodName(), trace.getLineNumber()),
			MessageFormat.format(message, parameters),
			thrown
		);

	}

	/**
	 * Log a message, with associated {@link Throwable} information.
	 *
	 * @param logger     The {@link Logger} to log the message.
	 * @param className  The name of the class to be logged.
	 * @param methodName The name of the method to be logged.
	 * @param level      One of the message level identifiers, e.g.
	 *                   {@link Level#SEVERE}.
	 * @param thrown     {@link Throwable} associated with log message.
	 * @param message    The string message. It can contains parameters.
	 * @param parameters Array of parameters to the message.
	 * @see MessageFormat
	 */
	public static void log( Logger logger, String className, String methodName, Level level, Throwable thrown, String message, Object... parameters ) {
		logp(
			logger,
			level,
			className,
			methodName,
			MessageFormat.format(message, parameters),
			thrown
		);
	}

	/**
	 * Logs the time spent executing the given {@code function}.
	 *
	 * @param logger       The {@link Logger} to log the message.
	 * @param level        One of the message level identifiers, e.g.
	 *                     {@link Level#SEVERE}.
	 * @param function     The function whose execution time must be
	 *                     measured and logged.
	 * @param startMessage The start message to be logged. It can contains
	 *                     parameters, where parameter {@code {0}} will always
	 *                     be substituted by the time this method is called
	 *                     (i.e. before the given {@code function} is started).
	 *                     Parameter {@code {1}} indicates the first extra
	 *                     argument, parameter {@code {2}} the second one, and
	 *                     so on.
	 * @param endMessage   The message to be logged. It can contains parameters,
	 *                     where parameter {@code {0}} will always be
	 *                     substituted by the time spent running the given
	 *                     {@code function} in seconds (double value). If not
	 *                     used, the time spent will be appended to the
	 *                     {@code message}. Parameter {@code {1}} will be the
	 *                     start time, Parameter {@code {2}} will be the end
	 *                     time. Parameter {@code {3}} indicates the first
	 *                     extra argument, parameter {@code {4}} the second one,
	 *                     and so on.
	 * @param args         Arguments to be logged as parameters of messages.
	 * @see MessageFormat
	 * @see Instant#toString()
	 */
	public static void logTime( Logger logger, Level level, Runnable function, String startMessage, String endMessage, Object... args ) {

		Instant startInstant = Instant.now();

		logStartTime(logger, level, startInstant, startMessage, args);

		try {
			function.run();
		} catch ( Exception ex ) {
			log(LOGGER, Level.SEVERE, ex, "Unable to call function.");
		} finally {
			logTimeSpent(logger, level, startInstant, Instant.now(), endMessage, args);
		}

	}

	/**
	 * Logs the time spent executing the given {@code function}.
	 *
	 * @param <T>          The type of the return value.
	 * @param logger       The {@link Logger} to log the message.
	 * @param level        One of the message level identifiers, e.g.
	 *                     {@link Level#SEVERE}.
	 * @param function     The function whose execution time must be
	 *                     measured and logged.
	 * @param startMessage The start message to be logged. It can contains
	 *                     parameters, where parameter {@code {0}} will always
	 *                     be substituted by the time this method is called
	 *                     (i.e. before the given {@code function} is started).
	 *                     Parameter {@code {1}} indicates the first extra
	 *                     argument, parameter {@code {2}} the second one, and
	 *                     so on.
	 * @param endMessage   The message to be logged. It can contains parameters,
	 *                     where parameter {@code {0}} will always be
	 *                     substituted by the time spent running the given
	 *                     {@code function} in seconds (double value). If not
	 *                     used, the time spent will be appended to the
	 *                     {@code message}. Parameter {@code {1}} will be the
	 *                     start time, Parameter {@code {2}} will be the end
	 *                     time. Parameter {@code {3}} indicates the first
	 *                     extra argument, parameter {@code {4}} the second one,
	 *                     and so on.
	 * @param args         Arguments to be logged as parameters of messages.
	 * @return The value returned by the give {@code function}.
	 * @see MessageFormat
	 */
	public static <T> T logTime( Logger logger, Level level, Callable<T> function, String startMessage, String endMessage, Object... args ) {

		Instant startInstant = Instant.now();

		logStartTime(logger, level, startInstant, startMessage, args);

		try {
			return function.call();
		} catch ( Exception ex ) {
			log(LOGGER, Level.SEVERE, ex, "Unable to call function.");
			return null;
		} finally {
			logTimeSpent(logger, level, startInstant, Instant.now(), endMessage, args);
		}

	}

	/**
	 * Logs the time spent executing the given {@code function}.
	 *
	 * @param logger   The {@link Logger} to log the message.
	 * @param level    One of the message level identifiers, e.g.
	 *                 {@link Level#SEVERE}.
	 * @param function The function whose execution time must be
	 *                 measured and logged.
	 * @param message  The message to be logged. It can contains parameters,
	 *                 where parameter {@code {0}} will always be substituted by
	 *                 the time spent running the given {@code function} in
	 *                 seconds (double value). If not used, the time spent will
	 *                 be appended to the {@code message}. Parameter {@code {1}}
	 *                 will be the start time, Parameter {@code {2}} will be the
	 *                 end time. Parameter {@code {3}} indicates the first extra
	 *                 argument, parameter {@code {4}} the second one, and so on.
	 * @param args     Arguments to be logged as parameters of {@code message}.
	 * @see MessageFormat
	 */
	public static void logTimeSpent( Logger logger, Level level, Runnable function, String message, Object... args ) {

		Instant startInstant = Instant.now();

		try {
			function.run();
		} catch ( Exception ex ) {
			log(LOGGER, Level.SEVERE, ex, "Unable to call function.");
		} finally {
			logTimeSpent(logger, level, startInstant, Instant.now(), message, args);
		}

	}

	/**
	 * Logs the time spent executing the given {@code function}.
	 *
	 * @param logger    The {@link Logger} to log the message.
	 * @param level     One of the message level identifiers, e.g.
	 *                  {@link Level#SEVERE}.
	 * @param threshold The time spent executing the given {@code function} is
	 *                  logged only if it is larger than the threshold in
	 *                  milliseconds.
	 * @param function  The function whose execution time must be
	 *                  measured and logged.
	 * @param message   The message to be logged. It can contains parameters,
	 *                  where parameter {@code {0}} will always be substituted
	 *                  by the time spent running the given {@code function} in
	 *                  seconds (double value). If not used, the time spent will
	 *                  be appended to the {@code message}. Parameter {@code {1}}
	 *                  will be the start time, Parameter {@code {2}} will be
	 *                  the end time. Parameter {@code {3}} indicates the first
	 *                  extra argument, parameter {@code {4}} the second one,
	 *                  and so on.
	 * @param args      Arguments to be logged as parameters of {@code message}.
	 * @see MessageFormat
	 */
	public static void logTimeSpent( Logger logger, Level level, long threshold, Runnable function, String message, Object... args ) {

		Instant startInstant = Instant.now();

		try {
			function.run();
		} catch ( Exception ex ) {
			log(LOGGER, Level.SEVERE, ex, "Unable to call function.");
		} finally {

			Instant endInstant = Instant.now();

			if ( startInstant.plusMillis(threshold).isBefore(endInstant) ) {
				logTimeSpent(logger, level, startInstant, endInstant, message, args);
			}

		}

	}

	/**
	 * Logs the time spent executing the given {@code function}.
	 *
	 * @param <T>      The type of the return value.
	 * @param logger   The {@link Logger} to log the message.
	 * @param level    One of the message level identifiers, e.g.
	 *                 {@link Level#SEVERE}.
	 * @param function The function whose execution time must be
	 *                 measured and logged.
	 * @param message  The message to be logged. It can contains parameters,
	 *                 where parameter {@code {0}} will always be substituted by
	 *                 the time spent running the given {@code function} in
	 *                 seconds (double value). If not used, the time spent will
	 *                 be appended to the {@code message}. Parameter {@code {1}}
	 *                 will be the start time, Parameter {@code {2}} will be the
	 *                 end time. Parameter {@code {3}} indicates the first extra
	 *                 argument, parameter {@code {4}} the second one, and so on.
	 * @param args     Arguments to be logged as parameters of {@code message}.
	 * @return The value returned by the give {@code function}.
	 * @see MessageFormat
	 */
	public static <T> T logTimeSpent( Logger logger, Level level, Callable<T> function, String message, Object... args ) {

		Instant startInstant = Instant.now();

		try {
			return function.call();
		} catch ( Exception ex ) {
			log(LOGGER, Level.SEVERE, ex, "Unable to call function.");
			return null;
		} finally {
			logTimeSpent(logger, level, startInstant, Instant.now(), message, args);
		}

	}

	/**
	 * Logs the time spent executing the given {@code function}.
	 *
	 * @param <T>       The type of the return value.
	 * @param logger    The {@link Logger} to log the message.
	 * @param level     One of the message level identifiers, e.g.
	 *                  {@link Level#SEVERE}.
	 * @param threshold The time spent executing the given {@code function} is
	 *                  logged only if it is larger than the threshold in
	 *                  milliseconds.
	 * @param function  The function whose execution time must be
	 *                  measured and logged.
	 * @param message   The message to be logged. It can contains parameters,
	 *                  where parameter {@code {0}} will always be substituted
	 *                  by the time spent running the given {@code function} in
	 *                  seconds (double value). If not used, the time spent will
	 *                  be appended to the {@code message}. Parameter {@code {1}}
	 *                  will be the start time, Parameter {@code {2}} will be
	 *                  the end time. Parameter {@code {3}} indicates the first
	 *                  extra argument, parameter {@code {4}} the second one,
	 *                  and so on.
	 * @param args      Arguments to be logged as parameters of {@code message}.
	 * @return The value returned by the give {@code function}.
	 * @see MessageFormat
	 */
	public static <T> T logTimeSpent( Logger logger, Level level, long threshold, Callable<T> function, String message, Object... args ) {

		Instant startInstant = Instant.now();

		try {
			return function.call();
		} catch ( Exception ex ) {
			log(LOGGER, Level.SEVERE, ex, "Unable to call function.");
			return null;
		} finally {

			Instant endInstant = Instant.now();

			if ( startInstant.plusMillis(threshold).isBefore(endInstant) ) {
				logTimeSpent(logger, level, startInstant, endInstant, message, args);
			}

		}

	}

	/**
	 * Associate a {@link LogHandler} with the given {@link Level}.
	 * <p>
	 * Normally this method is called by this class' static initializer to
	 * register handlers provided by {@link LogHandlerProvider}s discovered at
	 * run-time.</p>
	 *
	 * @param level   The {@link Level} the given handler has to be associated with.
	 * @param handler The handler managing a message for the given level.
	 */
	public static void registerHandle( Level level, LogHandler handler ) {

		Set<LogHandler> logHandlers = HANDLERS.get(level);

		if ( logHandlers == null ) {

			logHandlers = new CopyOnWriteArraySet<>(Set.of(handler));

			HANDLERS.put(level, logHandlers);

		} else {
			logHandlers.add(handler);
		}

	}

	/**
	 * Returns a string representation of the given stack {@code trace}.
	 *
	 * @param trace The stack trace array.
	 * @return The string representation of the give stack trace.
	 * @see Exception#getStackTrace()
	 */
	public static String stackTrace( StackTraceElement[] trace ) {

		StringBuilder builder = new StringBuilder(512);

		for ( StackTraceElement traceElement : trace ) {
			builder.append("\n\tat ").append(traceElement);
		}

		builder.deleteCharAt(0);

		return builder.toString();

	}

	@SuppressWarnings( "AssignmentToMethodParameter" )
	private static void logStartTime( Logger logger, Level level, Instant startInstant, String message, Object[] args ) {

		Object[] params = new Object[1 + args.length];

		params[0] = ZoneIDHolder.getInstance().toString(startInstant);

		if ( args.length > 0 ) {
			System.arraycopy(args, 0, params, 1, args.length);
		}

		if ( message != null && !message.isEmpty() ) {
			if ( !message.contains("{0") ) {
				message += " [started at {0}]";
			}
		} else {

			StackTraceElement stElement = new Throwable().getStackTrace()[1];
			String simpleClassName = stElement.getClassName().substring(1 + stElement.getClassName().lastIndexOf('.'));
			String methodName = stElement.getMethodName();

			message = MessageFormat.format("Function called by {0}.{1} started at '{0}'.", simpleClassName, methodName);

		}

		StackTraceElement trace = new Exception().getStackTrace()[2];

		logp(logger, level, trace.getClassName(), MessageFormat.format(FORMAT, trace.getMethodName(), trace.getLineNumber()), MessageFormat.format(message, params));

	}

	@SuppressWarnings( "AssignmentToMethodParameter" )
	private static void logTimeSpent( Logger logger, Level level, Instant startInstant, Instant endInstant, String message, Object... args ) {

		Object[] params = new Object[3 + args.length];

		params[0] = Duration.between(startInstant, endInstant).toMillis() / 1000.0;
		params[1] = ZoneIDHolder.getInstance().toString(startInstant);
		params[2] = ZoneIDHolder.getInstance().toString(endInstant);

		if ( args.length > 0 ) {
			System.arraycopy(args, 0, params, 3, args.length);
		}

		if ( message != null && !message.isEmpty() ) {
			if ( !message.contains("{0") ) {
				message += " [{0,number,############0.###}s]";
			}
		} else {

			StackTraceElement stElement = new Throwable().getStackTrace()[1];
			String simpleClassName = stElement.getClassName().substring(1 + stElement.getClassName().lastIndexOf('.'));
			String methodName = stElement.getMethodName();

			message = MessageFormat.format("Function called by {0}.{1} finished at '{2}' taking '{0,number,############0.000}'s.", simpleClassName, methodName);

		}

		StackTraceElement trace = new Exception().getStackTrace()[2];

		logp(logger, level, trace.getClassName(), MessageFormat.format(FORMAT, trace.getMethodName(), trace.getLineNumber()), MessageFormat.format(message, params));

	}

	private static void logp( Logger logger, Level level, String className, String methodName, String msg ) {

		logger.logp(level, className, methodName, msg);

		Set<LogHandler> logHandlers = HANDLERS.get(level);

		if ( logHandlers != null ) {
			logHandlers.forEach(handler -> {
				if ( handler != null ) {
					try {
						handler.handle(level, className, methodName, msg, null);
					} catch ( Exception ex ) {
						LOGGER.log(
							Level.SEVERE,
							MessageFormat.format("Handle for {0} level failed.", level.getName()),
							ex
						);
					}
				}
			});
		}

	}

	private static void logp( Logger logger, Level level, String className, String methodName, String msg, Throwable thrown ) {

		logger.logp(level, className, methodName, msg, thrown);

		Set<LogHandler> logHandlers = HANDLERS.get(level);

		if ( logHandlers != null ) {
			logHandlers.forEach(handler -> {
				if ( handler != null ) {
					try {
						handler.handle(level, className, methodName, msg, thrown);
					} catch ( Exception ex ) {
						LOGGER.log(
							Level.SEVERE,
							MessageFormat.format("Handle for {0} level failed.", level.getName()),
							ex
						);
					}
				}
			});
		}

	}

	/**
	 * Removes a previously registered {@link LogHandler}.
	 *
	 * @param level   The {@link Level} the given handler was associated with.
	 * @param handler The previously registered handler.
	 * @see LogUtils#registerHandle(java.util.logging.Level, se.europeanspallationsource.xaos.core.util.LogHandler)
	 */
	public static void unregisterHandle( Level level, LogHandler handler ) {

		Set<LogHandler> logHandlers = HANDLERS.get(level);

		if ( logHandlers != null ) {
			logHandlers.remove(handler);
		}

	}

	private LogUtils() {
		//	Nothing to do.
	}

}
