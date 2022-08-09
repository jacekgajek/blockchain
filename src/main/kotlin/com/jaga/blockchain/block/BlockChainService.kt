package com.jaga.blockchain.block

import com.jaga.blockchain.mqtt.BlockChainPublisher
import jakarta.inject.Singleton

@Singleton
class BlockChainService(
    private val mineService: MineService,
    private val publisher: BlockChainPublisher,
) {
    private var blockChain: BlockChain = BlockChain()
    private var initialized = false

    val lastBlock get() = blockChain.lastOrNull()
    val chain get() = blockChain.blocks

    fun mineZeroBlock(message: String): Block {
        val block = mineService.mineMultiThreaded(lastBlock, message)
        return appendZeroBlock(block)
    }

    fun mineBlock(message: String): Block {
        if (lastBlock == null) {
            throw BlockChainUninitializedException()
        }
        val block = mineService.mineMultiThreaded(lastBlock, message)
        return appendBlock(block)
    }

    @Throws(BlockChainUninitializedException::class)
    private fun appendBlock(block: Block): Block {
        if (!initialized) throw BlockChainUninitializedException()
        synchronized(blockChain) {
            if (blockChain.any { it.content.timeStamp == block.content.timeStamp })
                return block

            mineService.verifyAndThrow(lastBlock, block)
            publisher.send(BlockChain(blockChain + block))
            // test change
            return block
        }
    }

    fun overwrite(newBlockChain: BlockChain) {
        if (newBlockChain.take(blockChain.blocks.size) != blockChain.blocks) {
            throw BlockChainValidationFailedException("Received blockchain doesn't start with the existing one.")
        }
        newBlockChain.windowed(2)
            .forEach { (prevBlock, block) -> mineService.verifyAndThrow(prevBlock, block) }

        initialized = true
        blockChain = newBlockChain
    }

    private fun appendZeroBlock(block: Block): Block {
        initialized = true
        blockChain = BlockChain()
        return appendBlock(block)
    }

    fun appendNoValidation(block: Block) {
        blockChain = BlockChain(blockChain.blocks + block)
        publisher.send(blockChain)
    }

    fun clear() {
        blockChain = BlockChain()
        initialized = false
    }
}
