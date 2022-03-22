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
package de.s42.log;

import de.s42.log.impl.ConsoleLogger;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 *
 * @author Benjamin Schiller
 */
public final class LogManager
{

	public final static String CONFIG_LOGGER_FACTORY = "loggerFactory";
	public final static String CONFIG_LOGGER_FACTORY_DEFAULT = ConsoleLogger.class.getName();

	private static LoggerFactory loggerFactory = new ConsoleLogger();

	static {
		init();
	}

	private LogManager()
	{
		// never instantiated
	}

	private static void init()
	{
		InputStream configStream = LogManager.class.getClassLoader().getResourceAsStream("log42.properties");

		if (configStream != null) {
			try {
				Properties config = new Properties();
				config.load(configStream);

				String loggerFactoryClassName = config.getProperty(
					CONFIG_LOGGER_FACTORY, CONFIG_LOGGER_FACTORY_DEFAULT);

				if (loggerFactoryClassName != null) {

					loggerFactory = (LoggerFactory) Class.forName(loggerFactoryClassName).getConstructor().newInstance();
					loggerFactory.init(config);
				}
			} catch (IOException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
				throw new RuntimeException("Error reading logging config - " + ex.getMessage(), ex);
			}
		}
	}

	public static Logger getLogger(String name)
	{
		return loggerFactory.createLogger(name);
	}
}
