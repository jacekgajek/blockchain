package com.jaga.blockchain.block

data class BlockChain(
    val blocks: List<Block> = listOf(),
) : List<Block> by blocks
