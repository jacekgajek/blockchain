package com.jaga.blockchain.block

import com.jaga.blockchain.shared.logger
import jakarta.inject.Singleton
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.security.MessageDigest
import java.util.Arrays
import java.util.UUID
import java.util.concurrent.Executors

@Singleton
class MineService(config: MineServiceConfig): Closeable {
    var jobCount: Int = 1
        set(value) {
            if (value <= 0) throw IllegalArgumentException()
            digests = (0 until value).map { MessageDigest.getInstance("SHA-256") }
            field = value
        }

    private val log = logger(MineService::class.java)
    private val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    private val prefixLen = config.prefixLen
    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()

    private lateinit var digests: List<MessageDigest>

    init {
        jobCount = Runtime.getRuntime().availableProcessors()
    }

    fun mineMultiThreaded(previousBlock: Block?, message: String): Block {
        val content = buildBlockContent(previousBlock, message)

        var jobs: List<Job>
        var result: Pair<ByteArray, Long>? = null
        runBlocking(dispatcher) {
            log.info { "Starting mining a new block on $jobCount threads..." }
            jobs = (0 until jobCount).map { i ->
                launch {
                    result = dig(content, digests[i], i, jobCount)
                }
            }
            while (result == null) {
                delay(1)
            }
            jobs.forEach { it.cancel() }
        }
        val (hash, nonce) = result ?: throw IllegalStateException()
        return Block(content.copy(nonce = nonce), hash)
    }

    private fun buildBlockContent(previousBlock: Block?, message: String) = BlockContent(
        previousHash = previousBlock?.hash ?: UUID.randomUUID().toString().toByteArray(),
        data = message,
        timeStamp = System.currentTimeMillis(),
        nonce = 0,
    )

    fun mine(previousBlock: Block?, message: String): Block {
        val content = buildBlockContent(previousBlock, message)
        val result = runBlocking { dig(content, digest) }
        val threadId = Thread.currentThread().id
        log.info { "Starting mining a new block on thread $threadId" }
        val (hash, nonce) = result
        return Block(content.copy(nonce = nonce), hash)
    }

    private suspend fun dig(content: BlockContent, digest: MessageDigest, startNonce: Int = 0, step: Int = 1): Pair<ByteArray, Long> {
        val threadId = Thread.currentThread().id
        var hash: ByteArray
        var nonce = startNonce.toLong()
        val start = System.currentTimeMillis()
        val buffer = content.toByteBuffer()
        var counter = 0
        val limit = 1_000

        do {
            if (counter == limit) {
                currentCoroutineContext().ensureActive()
                counter = 0
            }
            counter++

            buffer.reset()
            buffer.putLong(nonce)
            hash = digest.digest(buffer.array())
            nonce += step
        } while (!startsWithZeroes(hash))
        log.info { "Thread $threadId has mined a block in ${(System.currentTimeMillis() - start)/1000.0}s with nonce=$nonce." }
        return hash to nonce
    }

    private val zeroes = ByteArray(prefixLen) { 0 }

    private fun startsWithZeroes(array: ByteArray): Boolean =
        Arrays.mismatch(zeroes, 0, prefixLen, array, 0, prefixLen) < 0

    fun verifyAndThrow(previousBlock: Block?, block: Block) {
        if (!startsWithZeroes(block.hash)) {
            throw BlockChainValidationFailedException("Proof Of Work unsatisfied – the hash does not start with required prefix.")
        }
        if (previousBlock != null && !block.content.previousHash.contentEquals(previousBlock.hash)) {
            throw BlockChainValidationFailedException("Proof Of Work unsatisfied – a block does not contain the hash of the previous block.")
        }
    }

    override fun close() {
        dispatcher.close()
    }
}
