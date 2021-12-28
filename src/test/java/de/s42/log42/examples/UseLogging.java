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
package de.s42.log42.examples;

import de.s42.log42.LogLevel;
import de.s42.log42.LogManager;
import de.s42.log42.Logger;

/**
 * This simple example shows the usage with using the log42.properties in the root folder of test
 *
 * @author Benjamin.Schiller
 */
public class UseLogging
{

	private final static Logger log = LogManager.getLogger(UseLogging.class.getName());

	public static void main(String[] args)
	{
		log.info("Welcome");
		log.start("Timer");
		log.stop(LogLevel.INFO, "Timer");
		log.info("Bye");
	}
}