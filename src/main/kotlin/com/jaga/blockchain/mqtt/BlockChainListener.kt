package com.jaga.blockchain.mqtt

import com.jaga.blockchain.block.Block
import com.jaga.blockchain.block.BlockChain
import com.jaga.blockchain.block.BlockChainService
import com.jaga.blockchain.block.BlockChainValidationFailedException
import com.jaga.blockchain.shared.logger
import io.micronaut.mqtt.annotation.MqttSubscriber
import io.micronaut.mqtt.annotation.Topic
import mu.KLogger

@MqttSubscriber
class BlockChainListener(
    private val blockChainService: BlockChainService
) {
    private val log: KLogger = logger(BlockChainListener::class.java)

    @Topic(BLOCK_TOPIC)
    fun receive(blockChain: List<Block>) {
        try {
            blockChainService.overwrite(BlockChain(blockChain))
            log.info { "Blockchain updated." }
        } catch (e: BlockChainValidationFailedException) {
            log.warn { "Blockchain rejected: ${e.message}" }
        }
    }
}
