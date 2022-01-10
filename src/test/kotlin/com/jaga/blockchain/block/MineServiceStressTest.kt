package com.jaga.blockchain.block

import io.kotest.core.spec.style.FunSpec

private const val COUNT = 10

class MineServiceStressTest : FunSpec({
    val miner = MineService(MineServiceConfig(3))

    test("mineMultiThreaded stress test") {
        miner.mineMultiThreaded(null, "Test")
        miner.mineMultiThreaded(null, "Test")
        val start = System.currentTimeMillis()

        val nonces = (1 until COUNT).map { i ->
            miner.mineMultiThreaded(null, "Test").content.nonce
        }
        report(start, nonces)
    }

    test("mine stress test") {
        miner.mine(null, "Test")
        miner.mine(null, "Test")
        val start = System.currentTimeMillis()
        val nonces = (1 until COUNT).map { i ->
            miner.mine(null, "Test").content.nonce
        }
        report(start, nonces)
    }
})

private fun report(start: Long, nonces: List<Long>) {
    val time = (System.currentTimeMillis() - start) / 1000.0 / COUNT.toDouble()
    println("Average Time:          " + (System.currentTimeMillis() - start) / 1000.0 / COUNT.toDouble())
    println("Average Nonce:         " + nonces.average().toInt())
    println("Average Nonce per sec: " + (nonces.average() / time).toInt())
}
