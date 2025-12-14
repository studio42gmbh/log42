// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2021 Studio 42 GmbH (https://www.s42m.de).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//</editor-fold>
package de.s42.log;

/**
 *
 * @author Benjamin Schiller
 */
public interface Logger
{

	void start(String id);

	double stop(LogLevel level, String id, int count);

	void log(LogLevel level, Object... messages);

	void logThrowable(LogLevel level, Throwable ex, Object... messages);

	void ifLevel(LogLevel level, Runnable doOnLevel);

	default double stop(LogLevel level, String id)
	{
		return stop(level, id, 1);
	}

	default double stopTrace(String id)
	{
		return stop(LogLevel.TRACE, id);
	}

	default double stopDebug(String id)
	{
		return stop(LogLevel.DEBUG, id);
	}

	default double stopInfo(String id)
	{
		return stop(LogLevel.INFO, id);
	}

	default double stopWarn(String id)
	{
		return stop(LogLevel.WARN, id);
	}

	default double stopError(String id)
	{
		return stop(LogLevel.ERROR, id);
	}

	default double stopFatal(String id)
	{
		return stop(LogLevel.FATAL, id);
	}

	default void trace(Object... messages)
	{
		log(LogLevel.TRACE, messages);
	}

	default void debug(Object... messages)
	{
		log(LogLevel.DEBUG, messages);
	}

	default void info(Object... messages)
	{
		log(LogLevel.INFO, messages);
	}

	default void warn(Object... messages)
	{
		log(LogLevel.WARN, messages);
	}

	default void error(Object... messages)
	{
		log(LogLevel.ERROR, messages);
	}

	default void fatal(Object... messages)
	{
		log(LogLevel.FATAL, messages);
	}

	default void trace(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.TRACE, ex, messages);
	}

	default void debug(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.DEBUG, ex, messages);
	}

	default void info(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.INFO, ex, messages);
	}

	default void warn(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.WARN, ex, messages);
	}

	default void error(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.ERROR, ex, messages);
	}

	default void fatal(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.FATAL, ex, messages);
	}

	default void ifTrace(Runnable doOnTrace)
	{
		ifLevel(LogLevel.TRACE, doOnTrace);
	}

	default void ifDebug(Runnable doOnDebug)
	{
		ifLevel(LogLevel.DEBUG, doOnDebug);
	}

	default void ifInfo(Runnable doOnInfo)
	{
		ifLevel(LogLevel.INFO, doOnInfo);
	}

	default void ifWarn(Runnable doOnWarn)
	{
		ifLevel(LogLevel.WARN, doOnWarn);
	}

	default void ifError(Runnable doOnError)
	{
		ifLevel(LogLevel.ERROR, doOnError);
	}

	default void ifFatal(Runnable doOnFatal)
	{
		ifLevel(LogLevel.FATAL, doOnFatal);
	}
}
