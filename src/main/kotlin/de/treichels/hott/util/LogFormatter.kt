package de.treichels.hott.util

import java.io.PrintWriter
import java.io.StringWriter
import java.lang.management.ManagementFactory
import java.text.MessageFormat
import java.util.logging.Formatter
import java.util.logging.LogRecord

fun getThreadById(id: Long): Thread = Thread.getAllStackTraces().keys.first { it.id == id }

class LogFormatter : Formatter() {
    companion object {
        private val start: Long = ManagementFactory.getRuntimeMXBean().startTime
    }

    fun getStackTrace(t: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        t.printStackTrace(printWriter)
        return stringWriter.toString()
    }

    override fun format(record: LogRecord?): String {
        if (record == null) return ""

        val now: Long = record.millis - start
        val parameters: Array<out Any>? = record.parameters;
        val message: String = if (parameters == null || parameters.isEmpty()) record.message else MessageFormat.format(record.message, *parameters)
        val threadName: String = getThreadById(record.getLongThreadID()).name
        val className: String = record.sourceClassName
        val methodName: String = record.sourceMethodName
        val result: String = String.format("----------------------------------------------------------%n%d [%s] %s.%s%n%s%n", now, threadName, className , methodName, message)
        val throwable: Throwable? = record.thrown

        return if (throwable != null) {
            result + getStackTrace(throwable)
        } else
            result
    }
}