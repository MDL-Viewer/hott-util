package de.treichels.hott.util

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream

class StreamSupportKtTest {
    val data = ByteArray(256) {
        it.toByte()
    }

    @Test
    fun testReadByte() {
        for (i in 0..255) {
            assertEquals(i.toByte(), data.readByte(i))
        }

        data.inputStream().use {
            for (i in 0..255) {
                assertEquals(i.toByte(), it.readByte())
            }
        }

        val a = byteArrayOf(-2, -1, 0, 1, 2)
        assertEquals((-2).toByte(), a.readByte(0))
        assertEquals((-1).toByte(), a.readByte(1))
        assertEquals(0.toByte(), a.readByte(2))
        assertEquals(1.toByte(), a.readByte(3))
        assertEquals(2.toByte(), a.readByte(4))

        a.inputStream().use {
            assertEquals((-2).toByte(), it.readByte())
            assertEquals((-1).toByte(), it.readByte())
            assertEquals(0.toByte(), it.readByte())
            assertEquals(1.toByte(), it.readByte())
            assertEquals(2.toByte(), it.readByte())
        }
    }

    @Test
    fun testReadUnsignedByte() {
        for (i in 0..255) {
            assertEquals(i.toUByte(), data.readUByte(i))
        }

        data.inputStream().use {
            for (i in 0..255) {
                assertEquals(i.toUByte(), it.readUByte())
            }
        }

        val a = byteArrayOf(-2, -1, 0, 1, 2)
        assertEquals(254.toUByte(), a.readUByte(0))
        assertEquals(255.toUByte(), a.readUByte(1))
        assertEquals(0.toUByte(), a.readUByte(2))
        assertEquals(1.toUByte(), a.readUByte(3))
        assertEquals(2.toUByte(), a.readUByte(4))

        a.inputStream().use {
            assertEquals(254.toUByte(), it.readUByte())
            assertEquals(255.toUByte(), it.readUByte())
            assertEquals(0.toUByte(), it.readUByte())
            assertEquals(1.toUByte(), it.readUByte())
            assertEquals(2.toUByte(), it.readUByte())
        }
    }

    @Test
    fun testReadShort() {
        val a = byteArrayOf(-2, -1, -1, -1, 0, 0, 1, 0, 2, 0, 0, 1, 0, 4)
        assertEquals((-2).toShort(), a.readShort(0))
        assertEquals((-1).toShort(), a.readShort(2))
        assertEquals(0.toShort(), a.readShort(4))
        assertEquals(1.toShort(), a.readShort(6))
        assertEquals(2.toShort(), a.readShort(8))
        assertEquals(256.toShort(), a.readShort(10))
        assertEquals(1024.toShort(), a.readShort(12))

        a.inputStream().use {
            assertEquals((-2).toShort(), it.readShort())
            assertEquals((-1).toShort(), it.readShort())
            assertEquals(0.toShort(), it.readShort())
            assertEquals(1.toShort(), it.readShort())
            assertEquals(2.toShort(), it.readShort())
            assertEquals(256.toShort(), it.readShort())
            assertEquals(1024.toShort(), it.readShort())
        }
    }

    @Test
    fun testReadUnsignedShort() {
        val a = byteArrayOf(-2, -1, -1, -1, 0, 0, 1, 0, 2, 0, 0, 1, 0, 4)
        assertEquals(65534.toUShort(), a.readUShort(0))
        assertEquals(65535.toUShort(), a.readUShort(2))
        assertEquals(0.toUShort(), a.readUShort(4))
        assertEquals(1.toUShort(), a.readUShort(6))
        assertEquals(2.toUShort(), a.readUShort(8))
        assertEquals(256.toUShort(), a.readUShort(10))
        assertEquals(1024.toUShort(), a.readUShort(12))

        a.inputStream().use {
            assertEquals(65534.toUShort(), it.readUShort())
            assertEquals(65535.toUShort(), it.readUShort())
            assertEquals(0.toUShort(), it.readUShort())
            assertEquals(1.toUShort(), it.readUShort())
            assertEquals(2.toUShort(), it.readUShort())
            assertEquals(256.toUShort(), it.readUShort())
            assertEquals(1024.toUShort(), it.readUShort())
        }
    }

    @Test
    fun testReadUnsignedInt() {
        val a = byteArrayOf(-2, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1, 0)
        assertEquals(0xfffffffeu, a.readUInt(0))
        assertEquals(0xffffffffu, a.readUInt(4))
        assertEquals(0u, a.readUInt(8))
        assertEquals(1u, a.readUInt(12))
        assertEquals(2u, a.readUInt(16))
        assertEquals(65536u, a.readUInt(20))

        a.inputStream().use {
            assertEquals(0xfffffffeu, it.readUInt())
            assertEquals(0xffffffffu, it.readUInt())
            assertEquals(0u, it.readUInt())
            assertEquals(1u, it.readUInt())
            assertEquals(2u, it.readUInt())
            assertEquals(65536u, it.readUInt())
        }
    }

    @Test
    fun testReadInt() {
        val a = byteArrayOf(-2, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1, 0)
        assertEquals(-2, a.readInt(0))
        assertEquals(-1, a.readInt(4))
        assertEquals(0, a.readInt(8))
        assertEquals(1, a.readInt(12))
        assertEquals(2, a.readInt(16))
        assertEquals(65536, a.readInt(20))

        a.inputStream().use {
            assertEquals(-2, it.readInt())
            assertEquals(-1, it.readInt())
            assertEquals(0, it.readInt())
            assertEquals(1, it.readInt())
            assertEquals(2, it.readInt())
            assertEquals(65536, it.readInt())
        }
    }

    @Test
    fun testReadLong() {
        val a = byteArrayOf(-2, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1, 0)
        assertEquals(-2, a.readLong(0))
        assertEquals(0x0000000100000000, a.readLong(8))
        assertEquals(0x0001000000000002, a.readLong(16))

        a.inputStream().use {
            assertEquals(-2, it.readLong())
            assertEquals(0x0000000100000000, it.readLong())
            assertEquals(0x0001000000000002, it.readLong())
        }
    }

    @Test
    fun testWriteByte() {
        val expected = byteArrayOf(-2, -1, 0, 1, 2)
        val data = ByteArray(5)

        data.writeByte(-2,0)
        data.writeByte(-1,1)
        data.writeByte(0,2)
        data.writeByte(1,3)
        data.writeByte(2,4)

        assertArrayEquals(expected, data)

        val stream = ByteArrayOutputStream().apply {
            writeByte(-2)
            writeByte(-1)
            writeByte(0)
            writeByte(1)
            writeByte(2)
        }

        assertArrayEquals(expected, stream.toByteArray())
    }

    @Test
    fun testWriteUnsignedByte() {
        val expected = byteArrayOf(-2, -1, 0, 1, 2)

        val data = ByteArray(5)

        data.writeUByte(254u,0)
        data.writeUByte(255u,1)
        data.writeUByte(0u,2)
        data.writeUByte(1u,3)
        data.writeUByte(2u,4)

        assertArrayEquals(expected, data)

        val stream = ByteArrayOutputStream().apply {
            writeUByte(254u)
            writeUByte(255u)
            writeUByte(0u)
            writeUByte(1u)
            writeUByte(2u)
        }

        assertArrayEquals(expected, stream.toByteArray())
    }

    @Test
    fun testWriteShort() {
        val expected = byteArrayOf(-2, -1, -1, -1, 0, 0, 1, 0, 2, 0, 0, 1, 0, 4)
        val data = ByteArrayOutputStream().apply {
            writeShort(-2)
            writeShort(-1)
            writeShort(0)
            writeShort(1)
            writeShort(2)
            writeShort(256)
            writeShort(1024)
        }

        assertArrayEquals(expected, data.toByteArray())
    }

    @Test
    fun testWriteUnsignedShort() {
        val expected = byteArrayOf(-2, -1, -1, -1, 0, 0, 1, 0, 2, 0, 0, 1, 0, 4)
        val data = ByteArrayOutputStream().apply {
            writeUShort(65534u)
            writeUShort(65535u)
            writeUShort(0u)
            writeUShort(1u)
            writeUShort(2u)
            writeUShort(256u)
            writeUShort(1024u)
        }

        assertArrayEquals(expected, data.toByteArray())
    }

    @Test
    fun testWriteInt() {
        val expected = byteArrayOf(-2, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1, 0)
        val data = ByteArrayOutputStream().apply {
            writeInt(-2)
            writeInt(-1)
            writeInt(0)
            writeInt(1)
            writeInt(2)
            writeInt(65536)
        }

        assertArrayEquals(expected, data.toByteArray())
    }

    @Test
    fun testWriteUnsignedInt() {
        val expected = byteArrayOf(-2, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1, 0)
        val data = ByteArrayOutputStream().apply {
            writeUInt(0xfffffffeu)
            writeUInt(0xffffffffu)
            writeUInt(0u)
            writeUInt(1u)
            writeUInt(2u)
            writeUInt(65536u)
        }

        assertArrayEquals(expected, data.toByteArray())
    }

    @Test
    fun testWriteLong() {
        val expected = byteArrayOf(-2, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1, 0)
        val data = ByteArrayOutputStream().apply {
            writeLong(-2L)
            writeLong(0x0000000100000000)
            writeLong(0x0001000000000002)
        }

        assertArrayEquals(expected, data.toByteArray())
    }
}
