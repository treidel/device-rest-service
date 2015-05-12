import ch.qos.logback.ext.loggly.LogglyAppender;

// report logging status to the console
statusListener(OnConsoleStatusListener)

// create the console appender
appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  }
}

// create the LOGGLY appender
appender("LOGGLY", LogglyAppender) {
  endpointUrl = "http://logs-01.loggly.com/inputs/2b2e404d-6a7f-46e2-93c9-b3cbc26950d3/tag/logback"
  pattern = "%d{ISO8601} %p %t %c{0}.%M - %m%n"
}

// send logs to both appenders
root(INFO, ["CONSOLE", "LOGGLY"])