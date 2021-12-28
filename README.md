![GitHub](https://img.shields.io/github/license/studio42gmbh/log42)
![GitHub top language](https://img.shields.io/github/languages/top/studio42gmbh/log42)
![GitHub last commit](https://img.shields.io/github/last-commit/studio42gmbh/log42)
![GitHub issues](https://img.shields.io/github/issues/studio42gmbh/log42)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/studio42gmbh/log42/Java%20CI%20with%20Maven)

# Log 42

Log 42 is a simple, secure and fast logging for Java. Everyone who does not need JNDI lookups in his logging might enjoy that ;)

"Look up to the stars not down on your feet. Be curious!" _Stephen Hawking 1942 - 2018_

Have a great day!

Benjamin Schiller

## Features

* Log to console (out and err)
* Log to file (log and error log)
* Simple logging of times with start and stop extension
* Log times into google chrome performance json format (https://www.chromium.org/developers/how-tos/trace-event-profiling-tool)
* Asynchronous file logging optional
* Support ANSI coloring
* Easily extend and change the logging

## Future Plans

* Logging to rsyslog (https://de.wikipedia.org/wiki/Rsyslog)
* Optimize performance

## Usage

* Download project
* Add as maven dependency to your project locally
* Use LogManager and Logger
* Configure settings by adding a log42.properties into the root folder of your project resources

Find the Javadoc here: https://studio42gmbh.github.io/log42/javadoc/

### Just use in your class

The usage is mostly compatible to the logging provided by log4j et al.

```java
import de.s42.log42.LogManager;
import de.s42.log42.Logger;

public class UseLogging
{

	private final static Logger log = LogManager.getLogger(UseLogging.class.getName());

	public static void main(String[] args)
	{
		log.info("Welcome");
		log.start("Timer");
		log.stopDebug("Timer");
		log.info("Bye");
	}
}
```
For code see: https://github.com/studio42gmbh/log42/blob/main/src/test/java/de/s42/log42/examples/UseLogging.java

Leads to the following output (in Netbeans et al. the below at ... is nicely leading to the spot where the logging was put in code):

```
21:32:23:609 INFO [main] de.s42.log42.examples.UseLogging Welcome
 at de.s42.log42.examples.UseLogging.main(UseLogging.java:41)
21:32:23:628 DEBUG [main] de.s42.log42.examples.UseLogging Stopped timer Timer 0.0421 ms.
 at de.s42.log42.examples.UseLogging.main(UseLogging.java:43)
21:32:23:643 INFO [main] de.s42.log42.examples.UseLogging Bye
 at de.s42.log42.examples.UseLogging.main(UseLogging.java:44)
```

We just added a flavor of start and stopping timers and we removed all the fancy conversions of contents when logging them.
But you can easily extend that in your own logger by overloading the method 

```java
protected String getMessagesInfo(Throwable ex, Object... messages)
```
when overloading from de.s42.log42.impl.ConsoleLogger or de.s42.log42.impl.FileAndConsoleLogger

### log42.properties

Here is an example of the log42.properties you can put into your resources root.
```properties
# Select the factory implementation class (Default is: de.s42.log42.impl.ConsoleLogger)
loggerFactory = de.s42.log42.impl.FileAndConsoleLogger

# Elements will use ANSI for coloring (Default is: false)
enableAnsi = true

# Lowest level which will be logged (Default is: DEBUG)
level = DEBUG

# Lowest level which will be logged into err output (Default is: ERROR)
errorLevel = ERROR

# Adds a line under each log which allows to get to the code where the log was done in netbeans (Default is: false)
printTraceLine = true

# Format of the date info (Default is: HH:mm:ss:SSS)
dateFormat = HH:mm:ss:SSS

# File to log into (Default is: false)
logToFile = true

# Write file in own thread (Default is: false) -> If activated make sure you consider the implications when exiting while writing or keeping the process open by writer
writeFileAsynchronous = false

# File writer will be spawned as daemon thread or not (Default is: false) -> If not you have to call FileAndConsoleLogger.finishFileWriting() or exit hard otherwise the process will no terminate
fileWriterDaemon = false

# Elements will remove user ANSI for coloring from files (Default is: false)
removeAnsiInFiles = true

# Elements will replace newlines within the logs with "\n" (Default is: false)
removeInnerNewlinesInFiles = true

# String used for separating the info parts in a file (Default is: ' ')
fileInfoSeparator = ;

# Format of the date info (Default is: yyyy-MM-dd HH:mm:ss:SSS)
fileDateFormat = yyyy-MM-dd HH:mm:ss:SSS

# File to log into %y = year like 2021; %m = month like 06; %d = day like 27; (Required if logToFile == true)
file = ./target/log/%y/%m/log42-test-%y-%m-%d.log

# Lowest level which will be logged into errorFile output (Default is: NEVER)
fileErrorLevel = ERROR

# File to log into %y = year like 2021; %m = month like 06; %d = day like 27; (Required if fileErrorLevel < NEVER)
errorFile = ./target/log/%y/%m/log42-test-%y-%m-%d.error.log

# File to log performance data into (Default is: false) 
logToPerformanceFile = true

# File to log into %y = year like 2021; %m = month like 06; %d = day like 27; (Required if logToPerformanceFile == true) 
# File format see https://www.chromium.org/developers/how-tos/trace-event-profiling-tool
performanceFile = ./target/log/%y/%m/log42-test-%y-%m-%d.performance.json
```

For code see: https://github.com/studio42gmbh/log42/blob/main/src/test/resources/log42.properties
