import ch.qos.logback.ext.loggly.LogglyAppender;

// report logging status to the console
statusListener(OnConsoleStatusListener)

// create the console appender
appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  }
}

// see if we have a LOGGLY key
def LOGGLY_KEY = System.getenv("LOGGLY_KEY")
if (LOGGLY_KEY) {
	// create the LOGGLY appender
	appender("LOGGLY", LogglyAppender) {
  		endpointUrl = "http://logs-01.loggly.com/inputs/${LOGGLY_KEY}/tag/logback"
  		pattern = "%d{ISO8601} %p %t %c{0}.%M - %m%n"
	}
	// send logs to both appenders
	root(INFO, ["CONSOLE", "LOGGLY"])
} else {
	root(INFO, ["CONSOLE"])
}