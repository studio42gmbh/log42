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

	public void start(String id);

	public double stop(LogLevel level, String id, int count);

	public void log(LogLevel level, Object... messages);

	public void logThrowable(LogLevel level, Throwable ex, Object... messages);

	public void ifLevel(LogLevel level, Runnable doOnLevel);

	default public double stop(LogLevel level, String id)
	{
		return stop(level, id, 1);
	}

	default public double stopTrace(String id)
	{
		return stop(LogLevel.TRACE, id);
	}

	default public double stopDebug(String id)
	{
		return stop(LogLevel.DEBUG, id);
	}

	default public double stopInfo(String id)
	{
		return stop(LogLevel.INFO, id);
	}

	default public double stopWarn(String id)
	{
		return stop(LogLevel.WARN, id);
	}

	default public double stopError(String id)
	{
		return stop(LogLevel.ERROR, id);
	}

	default public double stopFatal(String id)
	{
		return stop(LogLevel.FATAL, id);
	}

	default public void trace(Object... messages)
	{
		log(LogLevel.TRACE, messages);
	}

	default public void debug(Object... messages)
	{
		log(LogLevel.DEBUG, messages);
	}

	default public void info(Object... messages)
	{
		log(LogLevel.INFO, messages);
	}

	default public void warn(Object... messages)
	{
		log(LogLevel.WARN, messages);
	}

	default public void error(Object... messages)
	{
		log(LogLevel.ERROR, messages);
	}

	default public void fatal(Object... messages)
	{
		log(LogLevel.FATAL, messages);
	}

	default public void trace(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.TRACE, ex, messages);
	}

	default public void debug(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.DEBUG, ex, messages);
	}

	default public void info(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.INFO, ex, messages);
	}

	default public void warn(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.WARN, ex, messages);
	}

	default public void error(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.ERROR, ex, messages);
	}

	default public void fatal(Throwable ex, Object... messages)
	{
		logThrowable(LogLevel.FATAL, ex, messages);
	}

	default public void ifTrace(Runnable doOnTrace)
	{
		ifLevel(LogLevel.TRACE, doOnTrace);
	}

	default public void ifDebug(Runnable doOnDebug)
	{
		ifLevel(LogLevel.DEBUG, doOnDebug);
	}

	default public void ifInfo(Runnable doOnInfo)
	{
		ifLevel(LogLevel.INFO, doOnInfo);
	}

	default public void ifWarn(Runnable doOnWarn)
	{
		ifLevel(LogLevel.WARN, doOnWarn);
	}

	default public void ifError(Runnable doOnError)
	{
		ifLevel(LogLevel.ERROR, doOnError);
	}

	default public void ifFatal(Runnable doOnFatal)
	{
		ifLevel(LogLevel.FATAL, doOnFatal);
	}
}
