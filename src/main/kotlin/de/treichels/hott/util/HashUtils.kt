package de.treichels.hott.util

import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

// compute hash for a byte array
fun ByteArray.hash() = inputStream().hash()

private const val BUFFER_SIZE = 1024 * 1024
const val HASH_ALGORITHM = "MD5"
const val HASH_SIZE = 32 // 128 bit => 16 byte => 32 hex characters
// convert message digest into zero padded hex string
fun MessageDigest.getHash() = BigInteger(1, digest()).toString(16).padStart(HASH_SIZE, '0')

// compute hash for a zip entry
fun ZipFile.hash(entry: ZipEntry) = getInputStream(entry).hash()

// compute hash for a regular file
fun File.hash() = inputStream().hash()

// compute hash for an input stream. The stream will be read to the end and closed.
fun InputStream.hash(): String {
    val md = MessageDigest.getInstance(HASH_ALGORITHM)
    val buffer = ByteArray(BUFFER_SIZE)

    use { stream ->
        while (true) {
            val len = stream.read(buffer)
            if (len >= 0) md.update(buffer, 0, len) else break
        }
    }

    return md.getHash()
}

// extract a zip entry to a file and return its hash
fun ZipFile.extract(zipEntry: ZipEntry, file: File): String {
    val md = MessageDigest.getInstance(HASH_ALGORITHM)
    val buffer = ByteArray(BUFFER_SIZE)

    file.parentFile.mkdirs()

    var bytesRead = 0L

    getInputStream(zipEntry).use { inputStream ->
        file.outputStream().use { outputStream ->
            while (true) {
                val len = inputStream.read(buffer)
                if (len >= 0) {
                    bytesRead += len
                    md.update(buffer, 0, len)
                    outputStream.write(buffer, 0, len)
                } else break
            }
        }
    }

    file.setLastModified(zipEntry.time)

    return md.getHash()
}
