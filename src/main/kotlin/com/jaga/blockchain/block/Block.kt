package com.jaga.blockchain.block

import java.nio.ByteBuffer

data class BlockContent(
    val previousHash: ByteArray,
    val data: String,
    val timeStamp: Long,
    val nonce: Long,
) {
    fun toByteBuffer(): ByteBuffer {
        val dataBytes = data.toByteArray()
        val capacity = Long.SIZE_BYTES + Long.SIZE_BYTES + previousHash.size + dataBytes.size
        val buffer: ByteBuffer = with(ByteBuffer.allocate(capacity)) {
            put(previousHash)
            put(dataBytes)
            putLong(timeStamp)
            mark()
            putLong(nonce)
        }
        return buffer
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockContent

        if (!previousHash.contentEquals(other.previousHash)) return false
        if (data != other.data) return false
        if (timeStamp != other.timeStamp) return false
        if (nonce != other.nonce) return false

        return true
    }

    override fun hashCode(): Int {
        var result = previousHash.contentHashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + timeStamp.hashCode()
        result = 31 * result + nonce.hashCode()
        return result
    }
}

data class Block(
    val content: BlockContent,
    val hash: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (content != other.content) return false
        if (!hash.contentEquals(other.hash)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + hash.contentHashCode()
        return result
    }
}