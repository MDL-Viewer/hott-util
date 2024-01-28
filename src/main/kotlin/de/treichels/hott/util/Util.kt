/**
 * HoTT Transmitter Config Copyright (C) 2013 Oliver Treichel
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package de.treichels.hott.util

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import java.util.*
import java.util.jar.JarFile
import java.util.logging.LogManager
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * @author Oliver Treichel &lt;oli@treichels.de&gt;
 */

object Util {
    private const val PARAM_OFFLINE = "offline"
    private const val PARAM_DEBUG = "debug"
    private const val LATEST_VERSIONS_URL =
        "https://drive.google.com/uc?export=download&id=0B_uPguA0xiT4SUl1V1VKYXFjWHc"
    private val latestVersions = Properties()
    val OFFLINE = java.lang.Boolean.getBoolean(PARAM_OFFLINE)
    val DEBUG = java.lang.Boolean.getBoolean(PARAM_DEBUG)

    @JvmOverloads
    fun dumpData(data: ByteArray?, baseAddress: Int = 0): String {
        val sb = StringBuilder()

        if (data != null) {
            val len = data.size
            var addr = 0

            while (addr < len) {
                sb.append(String.format("0x%04x: ", baseAddress + addr))

                for (i in 0..15)
                    if (addr + i < len) {
                        when (i) {
                            4, 12 -> sb.append(':')

                            0, 8 -> sb.append('|')

                            else -> sb.append(' ')
                        }

                        sb.append(String.format("%02x", data[addr + i]))
                    } else
                        sb.append("   ")

                sb.append("| ")

                (0..15).asSequence()
                    .filter { addr + it < len }
                    .map { (data[addr + it].toInt() and 0xff).toChar() }
                    .forEach {
                        if (it.code in 0x20..0x7e)
                            sb.append(it)
                        else
                            sb.append('.')
                    }

                sb.append('\n')
                addr += 16
            }
        }

        return sb.toString()
    }

    fun dumpData(data: ShortArray?, baseAddress: Int = 0): String {
        val sb = StringBuilder()

        if (data != null) {
            val len = data.size
            var addr = 0

            while (addr < len) {
                sb.append(String.format("0x%04x: ", baseAddress + addr * 2))

                for (i in 0..7)
                    if (addr + i < len) {
                        when (i) {
                            2, 6 -> sb.append(':')

                            0, 4 -> sb.append('|')

                            else -> sb.append(' ')
                        }

                        sb.append(String.format("%04x", data[addr + i]))
                    } else
                        sb.append("     ")

                sb.append("| ")

                (0..7).asSequence()
                    .filter { addr + it < len }
                    .map { (data[addr + it].toInt() and 0xffff) }
                    .forEach {
                        val lb = (it ushr 8) and 0xff
                        val hb = it and 0xff

                        if (lb in 0x20..0x7e)
                            sb.append(lb.toChar())
                        else
                            sb.append('.')

                        if (hb in 0x20..0x7e)
                            sb.append(hb.toChar())
                        else
                            sb.append('.')
                    }

                sb.append('\n')
                addr += 8
            }
        }

        return sb.toString()
    }

    fun getLatestVersion(key: String): String? {
        if (latestVersions.isEmpty)
            try {
                URI(LATEST_VERSIONS_URL).toURL().openStream().use { `is` ->
                    InputStreamReader(`is`).use { reader ->
                        latestVersions.load(reader)
                        latestVersions.setProperty(PARAM_OFFLINE, java.lang.Boolean.FALSE.toString())
                    }
                }
            } catch (e: IOException) {
                latestVersions.setProperty(PARAM_OFFLINE, java.lang.Boolean.TRUE.toString())
            }

        return latestVersions.getProperty(key, null)
    }

    fun sourceVersion(clazz: KClass<*>) = sourceLocation(clazz).let { source ->
        if (source.name.endsWith(".jar") || source.name.endsWith(".exe"))
            JarFile(source).use { jarFile ->
                val attributes = jarFile.manifest.mainAttributes
                val version = attributes.getValue("Implementation-Version")
                //val build = attributes.getValue("Implementation-Build")

                "v$version"
            }
        else "Unknown"
    }

    fun programDir(clazz: KClass<*>): String {
        // get the parent directory containing the jar file or the classes directory
        val programDir = sourceLocation(clazz).parentFile

        // if we are running inside Eclipse in the target directory, step up to the project level
        val result = if (programDir.name == "target") programDir.parentFile.absolutePath else programDir.absolutePath
        System.setProperty("program.dir", result)

        return result
    }

    private fun sourceLocation(clazz: KClass<*>) = File(clazz.java.protectionDomain.codeSource.location.toURI())

    fun enableLogging() {
        if (DEBUG) LogManager.getLogManager()
            .readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"))
    }
}

/*
 An abstract base class for objects that can be observed.
 The object can signal that its state has changed on some way by calling the invalidate() method.
 The method will then inform all listeners about the state change.
 */
abstract class ObservableBase : Observable {
    // listener list
    private val listeners = mutableListOf<InvalidationListener>()

    override fun addListener(listener: InvalidationListener?) {
        if (listener != null) listeners.add(listener)
    }

    override fun removeListener(listener: InvalidationListener?) {
        if (listener != null) listeners.remove(listener)
    }

    fun invalidate() {
        listeners.forEach { it.invalidated(this) }
    }
}

/*
 A delegator that allows a val to be backed by an ObservableValue (e.g. a Property).
 This avoids the need to call prop.value very time.

 Example:
    val prop = SimpleStringProperty("initial value")
    val value by prop

    println(value) => "initial value"
 */
operator fun <T> ObservableValue<T>.getValue(thisRef: Any?, property: KProperty<*>): T {
    return value
}

/*
 A delegator that allows a var to be backed by a WritableValue (e.g. a Property).
 This avoids the need to call prop.value = "new value" every time.

 Example:
    val prop = SimpleStringProperty("initial value")
    var value by prop

    println(value) => "initial value"
    value = "new value"
    println(value) => "new value"
 */
operator fun <T> WritableValue<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

class singleAssign<T> {
    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw UninitializedPropertyAccessException()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (this.value != null) throw IllegalStateException("Val ${property.name} was already assigned in class ${thisRef?.javaClass?.name}!")
        this.value = value
    }
}

operator fun ResourceBundle.get(name: String): String = getString(name)
