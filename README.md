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
* 

## Usage

* Download project
* Add as maven dependency to your project locally
* Use LogManager and Logger
* Configure settings by adding a log42.properties into the root folder of your project resources
