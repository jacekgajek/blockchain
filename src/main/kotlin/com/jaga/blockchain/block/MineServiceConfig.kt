package com.jaga.blockchain.block

import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton

@Singleton
class MineServiceConfig(@Property(name = "jaga.blockchain.miner.prefixLength") val prefixLen: Int)
