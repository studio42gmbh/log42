![GitHub](https://img.shields.io/github/license/studio42gmbh/log42)
![GitHub top language](https://img.shields.io/github/languages/top/studio42gmbh/log42)
![GitHub last commit](https://img.shields.io/github/last-commit/studio42gmbh/log42)
![GitHub issues](https://img.shields.io/github/issues/studio42gmbh/log42)

# Log 42

Log 42 is a simple, secure and fast logging for Java. Everyone who does not need JNDI lookups in his logging might enjoy that ;)

If you like Log 42 or have constructive critique dont hesitate to write us directly on info@s42m.de. We are always happy for qualified feedback!

Have a great day!

Benjamin Schiller

> "Look up to the stars not down on your feet. Be curious!" _Stephen Hawking 1942 - 2018_


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
* Extend demo cases with tutorials like showing extension
* Make log42 available in public maven repos


## Usage

* Download project
* Add as maven dependency to your project locally
* Use LogManager and Logger
* Configure settings by adding a log42.properties into the root folder of your project resources

Find the Javadoc here: https://studio42gmbh.github.io/log42/javadoc/

Important: You also need to get a local copy of the Base 42 library - see https://github.com/studio42gmbh/base42


### Just use in your class

The usage is mostly compatible to the logging provided by log4j et al.

```java
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.log42.Version;

public class UseLogging
{

	// create a class based logger - as the name is arbitrary you can also share loggers across systems if preferred
	private final static Logger log = LogManager.getLogger(UseLogging.class.getName());

	public static void main(String... args)
	{
		// simple info logging - the messages will be joined with a " " in between
		log.info("Welcome", "to", "Logging");

		// simple debug logging - available is: trace, debug, info, warn, error, fatal
		log.debug("Version of Log42", Version.getVersion());

		// starts a timer with id MyTimer in the default implementation the timers are threadbound
		log.start("MyTimer");

		// stops the timer with id MyTimer and prints the duration as DEBUG log
		log.stopDebug("MyTimer");

		// simple info logging
		log.info("Bye");
	}
}
```
For complete code see: https://github.com/studio42gmbh/log42/blob/main/src/test/java/de/s42/log42/examples/UseLogging.java

This leads to the following console output (in Netbeans et al. the below at ... is nicely leading to the spot where the logging was put in code):

```
00:27:54:862 INFO [main] de.s42.log42.examples.UseLogging Welcome to Logging
 at de.s42.log42.examples.UseLogging.main(UseLogging.java:45)
00:27:54:885 DEBUG [main] de.s42.log42.examples.UseLogging Version of Log42 0.1.0 #cd642099a34 2021-12-29T23:26:49Z
 at de.s42.log42.examples.UseLogging.main(UseLogging.java:48)
00:27:54:886 DEBUG [main] de.s42.log42.examples.UseLogging Stopped timer MyTimer 0.04 ms.
 at de.s42.log42.examples.UseLogging.main(UseLogging.java:54)
00:27:54:897 INFO [main] de.s42.log42.examples.UseLogging Bye
 at de.s42.log42.examples.UseLogging.main(UseLogging.java:57)
 ```

and the following file output:

```
2021-12-30 00:27:54:862;INFO;[main];de.s42.log42.examples.UseLogging;Welcome to Logging;\n at de.s42.log42.examples.UseLogging.main(UseLogging.java:45);
2021-12-30 00:27:54:885;DEBUG;[main];de.s42.log42.examples.UseLogging;Version of Log42 0.1.0 #cd642099a34 2021-12-29T23:26:49Z;\n at de.s42.log42.examples.UseLogging.main(UseLogging.java:48);
2021-12-30 00:27:54:886;DEBUG;[main];de.s42.log42.examples.UseLogging;Stopped timer MyTimer 0.04 ms.;\n at de.s42.log42.examples.UseLogging.main(UseLogging.java:54);
2021-12-30 00:27:54:897;INFO;[main];de.s42.log42.examples.UseLogging;Bye;\n at de.s42.log42.examples.UseLogging.main(UseLogging.java:57);
```

We just added a flavor of start and stopping timers and we removed all the fancy conversions of contents when logging them.

But you can easily extend that in your own logger by overloading the method:

```java
protected String getMessagesInfo(Throwable ex, Object... messages)
```
when overloading from de.s42.log42.impl.ConsoleLogger or de.s42.log42.impl.FileAndConsoleLogger


### log42.properties

Here is an example of the log42.properties you can put into your resources root.
```properties
loggerFactory = de.s42.log42.impl.FileAndConsoleLogger
enableAnsi = true
level = DEBUG
errorLevel = ERROR
printTraceLine = true
dateFormat = HH:mm:ss:SSS
logToFile = true
writeFileAsynchronous = false
fileWriterDaemon = false
removeAnsiInFiles = true
removeInnerNewlinesInFiles = true
fileInfoSeparator = ;
fileDateFormat = yyyy-MM-dd HH:mm:ss:SSS
file = ./target/log/%y/%m/log42-test-%y-%m-%d.log
fileErrorLevel = ERROR
errorFile = ./target/log/%y/%m/log42-test-%y-%m-%d.error.log
logToPerformanceFile = true
performanceFile = ./target/log/%y/%m/log42-test-%y-%m-%d.performance.json
```

For code see: https://github.com/studio42gmbh/log42/blob/main/src/test/resources/log42.properties


### Explanation of properties of de.s42.log42.impl.FileAndConsoleLogger


#### enableAnsi
Elements will use ANSI for coloring 

(Default is: false)


#### level
Lowest level which will be logged to console

(Default is: DEBUG)


#### errorLevel
Lowest level which will be logged into err output

(Default is: ERROR)


#### printTraceLine
Adds a line under each log which allows to get to the code where the log was done in IDEs like netbeans

(Default is: false)


#### dateFormat
Format of the date info 

For format details see https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/text/SimpleDateFormat.html

(Default is: HH:mm:ss:SSS)


#### logToFile
Is file logging active 

(Default is: false)


#### writeFileAsynchronous
Write file in own thread 

If activated make sure you consider the implications when exiting while writing or keeping the process open by writer

(Default is: false)


#### fileWriterDaemon
File writer will be spawned as daemon thread or not 

If not you have to call FileAndConsoleLogger.finishFileWriting() or exit hard otherwise the process will no terminate

(Default is: false) 


#### removeAnsiInFiles
Elements will remove user ANSI for coloring from files 

(Default is: false)


#### removeInnerNewlinesInFiles
Elements will replace newlines within the logs with "\n" 

(Default is: false)


#### fileInfoSeparator
String used for separating the info parts in a file 

(Default is: ' ')


#### fileDateFormat
Format of the date info 

For format details see https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/text/SimpleDateFormat.html

(Default is: yyyy-MM-dd HH:mm:ss:SSS)


#### file
File to log into 

Supported replacements: 

%y = year like 2021; 

%m = month like 06; 

%d = day like 27; 

(Required if logToFile == true)

```
./target/log/%y/%m/log42-test-%y-%m-%d.log
```


#### fileErrorLevel
Lowest level which will be logged into errorFile output 

(Default is: NEVER)


#### errorFile
File to log errors into 

Supported replacements: 

%y = year like 2021; 

%m = month like 06; 

%d = day like 27; 

(Required if fileErrorLevel > NEVER)

```
./target/log/%y/%m/log42-test-%y-%m-%d.error.log
```


#### logToPerformanceFile
File to log performance data into 

(Default is: false) 


#### performanceFile
File to log performance infos (start, stop) into

Supported replacements: 

%y = year like 2021; 

%m = month like 06; 

%d = day like 27; 

For the resulting JSON file format see https://www.chromium.org/developers/how-tos/trace-event-profiling-tool

(Required if logToPerformanceFile == true)

```
./target/log/%y/%m/log42-test-%y-%m-%d.performance.json
```
