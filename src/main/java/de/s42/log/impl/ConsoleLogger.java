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
package de.s42.log.impl;

import de.s42.base.console.AnsiHelper;
import de.s42.base.console.AnsiHelper.TerminalColor;
import de.s42.log.LogLevel;
import de.s42.log.Logger;
import de.s42.log.LoggerFactory;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 *
 * @author Benjamin Schiller
 */
public class ConsoleLogger implements Logger, LoggerFactory
{

	public final static String CONFIG_LEVEL = "level";
	public final static String CONFIG_LEVEL_DEFAULT = "DEBUG";

	public final static String CONFIG_ERROR_LEVEL = "errorLevel";
	public final static String CONFIG_ERROR_LEVEL_DEFAULT = "ERROR";

	public final static String CONFIG_PRINT_TRACE_LINE = "printTraceLine";
	public final static String CONFIG_PRINT_TRACE_LINE_DEFAULT = "false";

	public final static String CONFIG_DATE_FORMAT = "dateFormat";
	public final static String CONFIG_DATE_FORMAT_DEFAULT = "HH:mm:ss:SSS";

	public final static String CONFIG_ENABLE_ANSI = "enableAnsi";
	public final static String CONFIG_ENABLE_ANSI_DEFAULT = "false";

	protected String name;

	protected static UUID runId = UUID.randomUUID();
	protected static LogLevel logLevel = LogLevel.DEBUG;
	protected static LogLevel logErrorLevel = LogLevel.NEVER;
	protected static boolean printTraceLine = false;
	protected static boolean enableAnsi = false;
	protected static DateFormat dateFormat = new SimpleDateFormat(CONFIG_DATE_FORMAT_DEFAULT);

	protected static ThreadLocal<Map<String, Long>> timersPerThread = new ThreadLocal<>();

	public ConsoleLogger()
	{

	}

	public ConsoleLogger(String name)
	{
		assert name != null;

		this.name = name;
	}

	@Override
	public void init(Properties config)
	{
		assert config != null;

		logLevel = LogLevel.valueOf(
			config.getProperty(CONFIG_LEVEL, CONFIG_LEVEL_DEFAULT));

		logErrorLevel = LogLevel.valueOf(
			config.getProperty(CONFIG_ERROR_LEVEL, CONFIG_ERROR_LEVEL_DEFAULT));

		enableAnsi = Boolean.parseBoolean(
			config.getProperty(CONFIG_ENABLE_ANSI, CONFIG_ENABLE_ANSI_DEFAULT));

		printTraceLine = Boolean.parseBoolean(
			config.getProperty(CONFIG_PRINT_TRACE_LINE, CONFIG_PRINT_TRACE_LINE_DEFAULT));

		dateFormat = new SimpleDateFormat(
			config.getProperty(CONFIG_DATE_FORMAT, CONFIG_DATE_FORMAT_DEFAULT));
	}

	@Override
	public Logger createLogger(String name)
	{
		assert name != null;

		return new ConsoleLogger(name);
	}

	@Override
	public void ifLevel(LogLevel level, Runnable doOnLevel)
	{
		assert level != null;
		assert doOnLevel != null;

		if (logLevel.isLower(level)) {
			return;
		}

		doOnLevel.run();
	}

	@Override
	public void start(String id)
	{
		assert id != null;

		Map<String, Long> timerMap = timersPerThread.get();

		if (timerMap == null) {
			timerMap = new HashMap<>();
			timersPerThread.set(timerMap);
		}

		timerMap.putIfAbsent(id, System.nanoTime());
	}

	@Override
	public double stop(LogLevel level, String id, int count)
	{
		assert level != null;
		assert id != null;

		Map<String, Long> timerMap = timersPerThread.get();

		// no timermap found with given id
		if (timerMap == null) {
			warn("No timermap found for this thread");
			return 0.0;
		}

		Long start = timerMap.remove(id);

		// no start with id found with given id
		if (start == null) {
			warn("No timer with id '" + id + "' found for this thread");
			return 0.0;
		}

		long delta = System.nanoTime() - start;

		double deltaMs = ((double) delta) / 1000000.0; // convert nano seconds to milliseconds

		if (count > 1) {

			double deltaPerMs = deltaMs / ((double) count);

			// @todo optimize multi count timer message
			log(level, "Stopped timer", id, deltaMs, "ms.", deltaPerMs, "ms. for", count, "iterations");
		} else {
			log(level, "Stopped timer", id, deltaMs, "ms.");
		}

		return deltaMs;
	}

	@Override
	public void log(LogLevel level, Object... messages)
	{
		assert level != null;

		logThrowable(level, null, messages);
	}

	@Override
	public void logThrowable(LogLevel level, Throwable ex, Object... messages)
	{
		assert level != null;

		// dont log lower levels than logLevel
		if (logLevel.isLower(level)) {
			return;
		}

		Date date = new Date();

		printLogLine(level, date, ex, messages);
	}

	protected void printLogLine(LogLevel level, Date date, Throwable ex, Object... messages)
	{
		assert level != null;
		assert date != null;

		String logLine = logLine(date, level, ex, messages);

		PrintStream printStream;
		if (logErrorLevel.isLower(level)) {
			printStream = System.out;
		} else {
			printStream = System.err;
		}

		try {
			printStream.write(logLine.getBytes(StandardCharsets.UTF_8));
		} catch (IOException ex1) {
			// ... should not happen
		}
	}

	protected void appendStackTraceElement(StringBuilder builder, StackTraceElement trace)
	{
		assert builder != null;
		assert trace != null;

		builder
			.append("\n at ")
			.append(trace.getClassName())
			.append(".")
			.append(trace.getMethodName())
			.append("(")
			.append(trace.getFileName())
			.append(":")
			.append(trace.getLineNumber())
			.append(")");
	}

	protected String getNameInfo()
	{
		return name;
	}

	protected String getThreadInfo()
	{
		return "[" + Thread.currentThread().getName() + "]";
	}

	protected String getDateInfo(Date date)
	{
		return dateFormat.format(date);
	}

	protected String getLevelInfo(LogLevel level)
	{
		assert level != null;

		if (enableAnsi) {
			TerminalColor levelInfoColor = TerminalColor.Black;

			switch (level) {
				case FATAL ->
					levelInfoColor = TerminalColor.BrightRed;
				case ERROR ->
					levelInfoColor = TerminalColor.Red;
				case WARN ->
					levelInfoColor = TerminalColor.Yellow;
				case DEBUG ->
					levelInfoColor = TerminalColor.Cyan;
				case TRACE ->
					levelInfoColor = TerminalColor.White;
				default -> {
				}
			}

			return AnsiHelper.coloredString(level.toString(), levelInfoColor);
		} else {
			return level.toString();
		}
	}

	protected String getTraceInfo()
	{
		// @todo is this way of getting the call point stable enough?
		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		int i = 0;
		boolean inLogger = false;
		for (; i < traces.length; ++i) {
			if (traces[i].getClassName().startsWith("de.s42.log.Logger")
				|| traces[i].getClassName().startsWith("de.s42.log.impl")
				|| traces[i].getMethodName().equals("log")) {

				inLogger = true;
			} else if (inLogger) {
				break;
			}
		}
		if (i == traces.length) {
			i = traces.length - 1;
		}
		StringBuilder line = new StringBuilder();
		appendStackTraceElement(line, traces[i]);
		return line.toString();
	}

	protected String getMessagesInfo(Throwable ex, Object... messages)
	{
		StringBuilder line = new StringBuilder();
		boolean first = true;
		for (Object message : messages) {
			if (!first) {
				line.append(" ");
			}
			first = false;
			if (message == null) {
				message = "null";
			}
			line.append(message.toString());
		}

		if (ex != null) {
			line.append(" ").append(ex.getMessage());
		}

		return line.toString();
	}

	protected String getExceptionInfo(Throwable ex)
	{
		assert ex != null;

		StringBuilder line = new StringBuilder();
		line.append("\n Exception ").append(ex.getClass().getName()).append(" : ").append(ex.getMessage());

		Throwable cex = ex;

		while (cex != null) {

			for (StackTraceElement trace : cex.getStackTrace()) {
				appendStackTraceElement(line, trace);
			}

			cex = cex.getCause();

			if (cex != null) {
				line.append("\n Caused by ").append(cex.getClass().getName()).append(" : ").append(cex.getMessage());
			}
		}

		return line.toString();
	}

	protected String logLine(Date date, LogLevel level, Throwable ex, Object... messages)
	{
		assert date != null;
		assert level != null;

		StringBuilder logLine = new StringBuilder();

		logLine.append(getDateInfo(date)).append(" ");
		logLine.append(getLevelInfo(level)).append(" ");
		logLine.append(getThreadInfo()).append(" ");
		logLine.append(getNameInfo()).append(" ");
		logLine.append(getMessagesInfo(ex, messages));

		// print trace line i.e. at de.s42.dl.examples.loadandstore.LoadAndStore.main(LoadAndStore.java:52)
		// will be clickable when printed with maven logging in netbeans
		if (printTraceLine) {
			logLine.append(getTraceInfo());
		}

		if (ex != null) {
			logLine.append(getExceptionInfo(ex));
		}

		logLine.append("\n");

		return logLine.toString();
	}
}
