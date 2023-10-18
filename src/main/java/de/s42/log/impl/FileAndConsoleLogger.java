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
package de.s42.log.impl;

import de.s42.base.date.Now;
import de.s42.log.LogLevel;
import de.s42.log.Logger;
import static de.s42.log.impl.ConsoleLogger.timersPerThread;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Benjamin Schiller
 */
public class FileAndConsoleLogger extends ConsoleLogger
{

	public final static String CONFIG_FILE = "file";
	public final static String CONFIG_ERROR_FILE = "errorFile";
	public final static String CONFIG_PERFORMANCE_FILE = "performanceFile";

	public final static String CONFIG_REMOVE_ANSI_IN_FILES = "removeAnsiInFiles";
	public final static String CONFIG_REMOVE_ANSI_IN_FILES_DEFAULT = "false";

	public final static String CONFIG_WRITE_FILE_ASYNCHRONOUS = "writeFileAsynchronous";
	public final static String CONFIG_WRITE_FILE_ASYNCHRONOUS_DEFAULT = "false";

	public final static String CONFIG_FILE_WRITER_DAEMON = "fileWriterDeamon";
	public final static String CONFIG_FILE_WRITER_DAEMON_DEFAULT = "false";

	public final static String CONFIG_REMOVE_INNER_NEWLINES_IN_FILES = "removeInnerNewlinesInFiles";
	public final static String CONFIG_REMOVE_INNER_NEWLINES_IN_FILES_DEFAULT = "false";

	public final static String CONFIG_LOG_TO_FILE = "logToFile";
	public final static String CONFIG_LOG_TO_FILE_DEFAULT = "false";

	public final static String CONFIG_LOG_TO_PERFORMANCE_FILE = "logToPerformanceFile";
	public final static String CONFIG_LOG_TO_PERFORMANCE_FILE_DEFAULT = "false";

	public final static String CONFIG_FILE_LOG_LEVEL = "fileLevel";
	
	public final static String CONFIG_FILE_ERROR_LEVEL = "fileErrorLevel";
	public final static String CONFIG_FILE_ERROR_LEVEL_DEFAULT = "NEVER";

	public final static String CONFIG_FILE_DATE_FORMAT = "fileDateFormat";
	public final static String CONFIG_FILE_DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss:SSS";

	public final static String CONFIG_FILE_INFO_SEPARATOR = "fileInfoSeparator";
	public final static String CONFIG_FILE_INFO_SEPARATOR_DEFAULT = " ";

	public final static class FileWriteJob
	{

		public FileWriteJob(Path file, String data)
		{
			assert file != null;
			assert data != null;

			this.file = file;
			this.data = data;
		}

		public Path file;
		public String data;
	}

	protected static String file;
	protected static String errorFile;
	protected static String performanceFile;

	protected static String fileInfoSeparator = " ";
	protected static boolean removeAnsiInFiles = false;
	protected static boolean removeInnerNewlinesInFiles = false;
	protected static boolean logToFile = false;
	protected static boolean logToPerformanceFile = false;

	protected static int fileWriterQueue = 1000;
	protected static LogLevel fileLogLevel;
	protected static LogLevel fileErrorLevel = LogLevel.NEVER;
	protected static DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	protected static boolean writeFileAsynchronous = false;
	protected static boolean fileWriterDeamon = false;
	protected static boolean shallFinishFileWriting = false;
	protected static Thread fileWriter;
	protected static BlockingQueue<FileWriteJob> fileWriterJobs;

	public FileAndConsoleLogger()
	{
		super();
	}

	public FileAndConsoleLogger(String name)
	{
		super(name);
	}

	@Override
	public Logger createLogger(String name)
	{
		assert name != null;

		return new FileAndConsoleLogger(name);
	}

	@Override
	public void init(Properties config)
	{
		super.init(config);

		removeAnsiInFiles = Boolean.parseBoolean(
			config.getProperty(CONFIG_REMOVE_ANSI_IN_FILES, CONFIG_REMOVE_ANSI_IN_FILES_DEFAULT));

		removeInnerNewlinesInFiles = Boolean.parseBoolean(
			config.getProperty(CONFIG_REMOVE_INNER_NEWLINES_IN_FILES, CONFIG_REMOVE_INNER_NEWLINES_IN_FILES_DEFAULT));

		writeFileAsynchronous = Boolean.parseBoolean(
			config.getProperty(CONFIG_WRITE_FILE_ASYNCHRONOUS, CONFIG_WRITE_FILE_ASYNCHRONOUS_DEFAULT));

		logToPerformanceFile = Boolean.parseBoolean(
			config.getProperty(CONFIG_LOG_TO_PERFORMANCE_FILE, CONFIG_LOG_TO_PERFORMANCE_FILE_DEFAULT));

		logToFile = Boolean.parseBoolean(
			config.getProperty(CONFIG_LOG_TO_FILE, CONFIG_LOG_TO_FILE_DEFAULT));

		file = config.getProperty(CONFIG_FILE);

		fileInfoSeparator = config.getProperty(CONFIG_FILE_INFO_SEPARATOR, CONFIG_FILE_INFO_SEPARATOR_DEFAULT);

		errorFile = config.getProperty(CONFIG_ERROR_FILE);

		performanceFile = config.getProperty(CONFIG_PERFORMANCE_FILE);

		fileErrorLevel = LogLevel.valueOf(
			config.getProperty(CONFIG_FILE_ERROR_LEVEL, CONFIG_FILE_ERROR_LEVEL_DEFAULT));
		
		// Default file log level to log level
		fileLogLevel = LogLevel.valueOf(
			config.getProperty(CONFIG_FILE_LOG_LEVEL, logLevel.toString()));

		fileDateFormat = new SimpleDateFormat(
			config.getProperty(CONFIG_FILE_DATE_FORMAT, CONFIG_FILE_DATE_FORMAT_DEFAULT));

		if (writeFileAsynchronous) {

			fileWriterJobs = new ArrayBlockingQueue(fileWriterQueue);

			fileWriter = new Thread(() -> {
				while (true) {
					try {
						FileWriteJob job = fileWriterJobs.take();

						writeLogLineToFileImmediately(job.file, job.data);

						if (fileWriterJobs.isEmpty() && shallFinishFileWriting) {

							break;
						}

					} catch (InterruptedException ex) {
						throw new RuntimeException(ex);
					}
				}
			});
			fileWriter.setDaemon(fileWriterDeamon);
			fileWriter.start();
		}
	}

	public static void finishFileWriting() throws InterruptedException
	{
		if (!writeFileAsynchronous) {
			return;
		}

		shallFinishFileWriting = true;
		fileWriter.join();
	}

	protected String expandPathInfo(String path, Date date)
	{
		Now now = new Now(date);

		path = path.replaceAll("%y", now.getYearAsString());
		path = path.replaceAll("%m", now.getMonthTwoDigit());
		path = path.replaceAll("%d", now.getDayTwoDigit());

		return path;
	}

	protected Path getPathInfo(Date date)
	{
		if (file == null) {
			return null;
		}

		return Path.of(expandPathInfo(file, date));
	}

	protected Path getErrorPathInfo(Date date)
	{
		if (errorFile == null) {
			return null;
		}

		return Path.of(expandPathInfo(errorFile, date));
	}

	protected Path getPerformancePathInfo(Date date)
	{
		if (performanceFile == null) {
			return null;
		}

		return Path.of(expandPathInfo(performanceFile, date));
	}

	protected String getFileDateInfo(Date date)
	{
		return fileDateFormat.format(date);
	}

	@Override
	protected void printLogLine(LogLevel level, Date date, Throwable ex, Object... messages)
	{
		super.printLogLine(level, date, ex, messages);

		printLogLineToFile(level, date, ex, messages);
	}

	protected void printLogLineToFile(LogLevel level, Date date, Throwable ex, Object... messages)
	{
		assert level != null;
		assert date != null;

		if (!logToFile) {
			return;
		}
		
		// dont log lower levels than logLevel
		if (fileLogLevel.isLower(level)) {
			return;
		}

		String logLine = fileLogLine(date, level, ex, messages);

		if (removeAnsiInFiles) {
			logLine = logLine.replaceAll("\u001B\\[[;\\d]*m", "");
		}

		if (removeInnerNewlinesInFiles) {
			logLine = logLine.replaceAll("\n(?!$)", "\\\\n");
		}

		Path path;

		if (fileErrorLevel.isLower(level)) {
			path = getPathInfo(date);
		} else {
			path = getErrorPathInfo(date);
		}

		writeLogLineToFile(path, logLine);
	}

	protected void writeLogLineToFile(Path file, String logLine)
	{
		// @todo https://www.netjstech.com/2020/07/writing-file-asynchronously-java-program.html
		if (writeFileAsynchronous) {
			fileWriterJobs.add(new FileWriteJob(file, logLine));
		} else {
			writeLogLineToFileImmediately(file, logLine);
		}
	}

	protected void writeLogLineToFileImmediately(Path file, String logLine)
	{
		try {
			if (!Files.exists(file)) {
				Files.createDirectories(file.getParent());
			}

			// @todo https://www.happycoders.eu/de/java/dateien-schnell-einfach-schreiben/
			Files.writeString(file, logLine, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected String fileLogLine(Date date, LogLevel level, Throwable ex, Object... messages)
	{
		StringBuilder logLine = new StringBuilder();

		logLine.append(getFileDateInfo(date)).append(fileInfoSeparator);
		logLine.append(getLevelInfo(level)).append(fileInfoSeparator);
		logLine.append(getThreadInfo()).append(fileInfoSeparator);
		logLine.append(getNameInfo()).append(fileInfoSeparator);
		logLine.append(getMessagesInfo(ex, messages)).append(fileInfoSeparator);

		// print trace line i.e. at de.s42.dl.examples.loadandstore.LoadAndStore.main(LoadAndStore.java:52)
		// will be clickable when printed with maven logging in netbeans
		if (printTraceLine) {
			logLine.append(getTraceInfo()).append(fileInfoSeparator);
		}

		if (ex != null) {
			logLine.append(getExceptionInfo(ex)).append(fileInfoSeparator);
		}

		logLine.append("\n");

		return logLine.toString();
	}

	@Override
	public double stop(LogLevel level, String id, int count)
	{
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

		long end = System.nanoTime();

		long delta = end - start;

		double deltaMs = ((double) delta) / 1000000.0; // convert nano seconds to milliseconds

		if (count > 1) {

			double deltaPerMs = deltaMs / ((double) count);

			// @todo optimize multi count timer message
			log(level, "Stopped timer", id, deltaMs, "ms.", deltaPerMs, "ms. for", count, "iterations");
		} else {
			log(level, "Stopped timer", id, deltaMs, "ms.");
		}

		if (logToPerformanceFile) {
			logPerformanceRecordToFile(id, name, start, delta);
		}

		return deltaMs;
	}

	/**
	 * See https://www.chromium.org/developers/how-tos/trace-event-profiling-tool Open chrome://tracing/ in google
	 * chrome and load the generated file
	 *
	 * @param name Name of the record entry
	 * @param category Category of the record entry
	 * @param timeStampNs Timestamp in nanoseconds
	 * @param durationNs Duration in nanoseconds
	 */
	protected void logPerformanceRecordToFile(String name, String category, long timeStampNs, long durationNs)
	{
		Path path = getPerformancePathInfo(new Date());

		if (!Files.exists(path)) {

			writeLogLineToFileImmediately(path, "[\n");
		}

		//{"name": "myFunction", "cat": "foo", "ph": "X", "ts": 123, "dur": 234, "pid": 2343, "tid": 2347 }
		String line
			= "{\"name\": \"" + name
			+ "\", \"cat\": \"" + category
			+ "\", \"ph\": \"X\", \"ts\": " + (timeStampNs / 1000L)
			+ ", \"dur\": " + (durationNs / 1000L)
			+ ", \"pid\": " + runId.hashCode()
			+ ", \"tid\": " + Thread.currentThread().getId()
			+ " },\n";

		writeLogLineToFile(path, line);
	}
}
