package com.jaga.blockchain.mqtt

import com.jaga.blockchain.block.BlockChain
import io.micronaut.mqtt.annotation.Topic
import io.micronaut.mqtt.v5.annotation.MqttPublisher

@MqttPublisher
interface BlockChainPublisher {
    @Topic(BLOCK_TOPIC, qos = 2)
    fun send(data: BlockChain)
}