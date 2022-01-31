package com.jaga.blockchain.api

import com.jaga.blockchain.block.Block
import com.jaga.blockchain.block.BlockChainService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post

@Controller("/api")
class HelloController(
    private val chainService: BlockChainService,
){

    data class BlockData(val data: String)
    data class BlockChainWrapper(val chain: List<Block>)

    @Post("/mine-block-zero")
    fun mineBlockZero(message: BlockData): Block {
        return chainService.mineZeroBlock(message.data)
    }

    @Post("/mine")
    fun mine(message: BlockData): Block {
        return chainService.mineBlock(message.data)
    }

    @Post("/clear")
    fun clear() {
        return chainService.clear()
    }

    @Get("/block-chain")
    fun getBlockChain(): BlockChainWrapper {
        return BlockChainWrapper(chainService.chain)
    }

    @Post("/append-no-validation")
    fun append(block: Block) {
        chainService.appendNoValidation(block)
    }
}
