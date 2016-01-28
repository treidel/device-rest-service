import org.eluder.logback.ext.cloudwatch.appender.CloudWatchAppender;
import org.eluder.logback.ext.jackson.JacksonEncoder; 

// report logging status to the console
statusListener(OnConsoleStatusListener)

// create the console appender
appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  }
}

// see if we are using AWS cloudwatch
def AWS_CLOUDWATCH_REGION = System.getenv("AWS_CLOUDWATCH_REGION")
def CLOUDWATCH_LOGGROUPNAME = System.getenv("CLOUDWATCH_LOGGROUPNAME")
if (AWS_CLOUDWATCH_REGION && CLOUDWATCH_LOGGROUPNAME) {
	// use the hostname as the log stream name
	def CLOUDWATCH_LOGSTREAMNAME = java.net.InetAddress.getLocalHost().getHostName();
	// create the CloudWatch appender
	appender("CLOUDWATCH", CloudWatchAppender) {
  		region = "${AWS_CLOUDWATCH_REGION}"
  		logGroup = "${CLOUDWATCH_LOGGROUPNAME}"
  		logStream = "${CLOUDWATCH_LOGSTREAMNAME}"
  		encoder(JacksonEncoder) {
  			 timeStampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
  		}
	}
	// send logs to both appenders
	root(INFO, ["CONSOLE", "CLOUDWATCH"])
} else {
	root(INFO, ["CONSOLE"])
}