package de.treichels.hott.util

import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.CancellationException

interface Callback {
    val workDone: Long
    val totalWork: Long
    val progress: Double
    val subWorkDone: Long
    val subTotalWork: Long
    val subProgress: Double

    fun updateMessage(message: String)

    fun updateProgress(workDone: Long, totalWork: Long)
    fun updateProgress(workDone: Int, totalWork: Int) = updateProgress(workDone.toLong(), totalWork.toLong())

    fun updateSubProgress(subWorkDone: Long, subTotalWork: Long)
    fun updateSubProgress(subWorkDone: Int, subTotalWork: Int) = updateProgress(subWorkDone.toLong(), subTotalWork.toLong())

    fun isCancelled(): Boolean
    fun cancel(): Boolean

    fun warning(title: String, message: String, buttonTexts: Array<String>? = null, defaultButton: Int = 0): Boolean
    fun confirm(title: String, message: String, buttonTexts: Array<String>? = null, defaultButton: Int = 0): Boolean
}

abstract class AbstractCallback : Callback {
    private var cancelled = false
    override var workDone = 0L
    override var totalWork = 1L
    override var progress = 0.0
    override var subWorkDone = 0L
    override var subTotalWork = 1L
    override var subProgress = 0.0

    override fun updateProgress(workDone: Long, totalWork: Long) {
        this.workDone = workDone
        this.totalWork = totalWork
        this.progress = workDone.toDouble() / totalWork.toDouble()
    }

    override fun updateSubProgress(subWorkDone: Long, subTotalWork: Long) {
        this.subWorkDone = subWorkDone
        this.subTotalWork = subTotalWork
        this.subProgress = subWorkDone.toDouble() / subTotalWork.toDouble()
    }

    override fun isCancelled(): Boolean = cancelled

    override fun cancel(): Boolean {
        cancelled = true
        return true
    }
}

class SimpleCallback : AbstractCallback() {
    override fun updateMessage(message: String) {
        println(message)
    }

    override fun warning(title: String, message: String, buttonTexts: Array<String>?, defaultButton: Int): Boolean {
        println("Warning: $title\n$message")
        return true
    }

    override fun confirm(title: String, message: String, buttonTexts: Array<String>?, defaultButton: Int): Boolean {
        println("Confirm: $title\n$message")
        return true
    }

    override fun updateProgress(workDone: Long, totalWork: Long) {
        super.updateProgress(workDone, totalWork)
        val percent = (progress * 100).toInt()

        println("progress: $workDone of $totalWork ($percent %)")
    }

    override fun updateSubProgress(subWorkDone: Long, subTotalWork: Long) {
        super.updateSubProgress(subWorkDone, subTotalWork)
        val percent = subWorkDone * 100 / subTotalWork

        println("sub progress: $subWorkDone of $subTotalWork ($percent %)")
    }
}

private fun update(callback: Callback?, counter: Long, total: Long?): Long {
    callback?.apply {
        if (isCancelled()) {
            throw CancellationException()
        }

        if (total != null) {
            // report progress every 1KiB
            if (counter % 1024 == 0L) {
                updateProgress(counter, total)
            }
        }
    }

    return counter + 1
}

class CallbackInputStream(private val callback: Callback?, private val parent: InputStream, private val total: Long?) : InputStream() {
    private var counter = 0L

    override fun read(): Int {
        counter = update(callback, counter, total)
        return parent.read()
    }
}

class CallbackOutputStream(private val callback: Callback?, private val parent: OutputStream, private val total: Long?) : OutputStream() {
    private var counter = 0L

    override fun write(b: Int) {
        counter = update(callback, counter, total)
        parent.write(b)
    }
}
