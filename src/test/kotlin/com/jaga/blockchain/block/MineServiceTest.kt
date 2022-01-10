package com.jaga.blockchain.block

import com.jaga.blockchain.mqtt.BlockChainPublisher
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MineServiceTest : FunSpec({
    val miner = MineService(MineServiceConfig(2))
    val chain = BlockChainService(miner, object : BlockChainPublisher { override fun send(data: BlockChain) {} })

    test("mines valid zero block") {
        val block = miner.mine(null, "Test")
        shouldNotThrow<Throwable> { miner.verifyAndThrow(null, block) }
    }

    test("mines valid next block and overwrite whole chain") {
        val zero = miner.mine(null, "Test 1")
        val block = miner.mine(zero, "Test 2")
        shouldNotThrow<Throwable> { miner.verifyAndThrow(null, block) }
        chain.clear()
        shouldNotThrow<Throwable> { chain.overwrite(BlockChain(listOf(zero, block)))  }
        chain.lastBlock shouldNotBe null
        chain.chain[0].content.data shouldBe "Test 1"
        chain.lastBlock!!.content.data shouldBe "Test 2"
    }

    test("mines valid next block and overwrite chain step by step") {
        val zero = miner.mine(null, "Test 1")
        val block = miner.mine(zero, "Test 2")
        chain.clear()
        chain.lastBlock shouldBe null
        shouldNotThrow<Throwable> { chain.overwrite(BlockChain(listOf(zero)))  }
        chain.lastBlock shouldNotBe null
        chain.lastBlock!!.content.data shouldBe "Test 1"
        shouldNotThrow<Throwable> { miner.verifyAndThrow(null, block) }
        shouldNotThrow<Throwable> { chain.overwrite(BlockChain(listOf(zero, block)))  }
        chain.lastBlock shouldNotBe null
        chain.chain[0].content.data shouldBe "Test 1"
        chain.lastBlock!!.content.data shouldBe "Test 2"
    }
})
