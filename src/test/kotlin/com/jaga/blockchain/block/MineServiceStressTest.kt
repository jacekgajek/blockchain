package com.jaga.blockchain.block

import io.kotest.core.spec.style.FunSpec

private const val COUNT = 1

private val miner = MineService(MineServiceConfig(3))
class MineServiceStressTest : FunSpec({

    test("mineMultiThreaded stress test").config() {
        miner.mineMultiThreaded(null, "Test")
        miner.mineMultiThreaded(null, "Test")
        val start = System.currentTimeMillis()

        val nonces = (1 .. COUNT).map { i ->
            miner.mineMultiThreaded(null, "Test").content.nonce
        }
        report(start, nonces)
    }

    test("mine stress test") {
        miner.mine(null, "Test")
        miner.mine(null, "Test")
        val start = System.currentTimeMillis()
        val nonces = (1..COUNT).map { i ->
            miner.mine(null, "Test").content.nonce
        }
        report(start, nonces)
    }
})

fun main() {
    miner.mineMultiThreaded(null, "Test")
    val start = System.currentTimeMillis()

    val nonces = (1 .. COUNT).map { i ->
        miner.mineMultiThreaded(null, "Test").content.nonce
    }
    report(start, nonces)
}

//fun main() {
//    miner.mineMultiThreaded(null, "Test")
//    miner.mineMultiThreaded(null, "Test")
//    val reports = mutableListOf<ReportResult>()
//    for (j in 1..10) {
//        val start = System.currentTimeMillis()
//        miner.jobCount = j
//
//        val nonces = (1 .. COUNT).fold(
//            listOf(miner.mineMultiThreaded(null, "Test 0"))
//        ) { chain: List<Block>, i: Int ->
//            chain + miner.mineMultiThreaded(chain.last(), "Test $i")
//        }.map { it.content.nonce }
//        reports.add(report(start, nonces).copy(threadCount = j))
//    }
//    println("Thread Count;Average Time;Average Non;Average Nonce per sec")
//    reports.forEach { printReportValues(it) }
//}

private data class ReportResult(val avgTime: Double, val avgNonce: Int, val avgNoncePerSec: Int, val threadCount: Int? = null)

private fun report(start: Long, nonces: List<Long>): ReportResult {
    val time = (System.currentTimeMillis() - start) / 1000.0 / COUNT.toDouble()
    val result = ReportResult((System.currentTimeMillis() - start) / 1000.0 / COUNT.toDouble(),
        nonces.average().toInt(),
        (nonces.average() / time).toInt()
    )
    printReport(result)
    return result
}

private fun printReportValues(result: ReportResult) {
    with(result) {
        println("${threadCount ?: ""};$avgTime;$avgNonce;$avgNoncePerSec")
    }
}

private fun printReport(result: ReportResult) {
    if (result.threadCount != null)
        println("Thread Count:          " + result.threadCount)
    println("Average Time:          " + result.avgTime)
    println("Average Nonce:         " + result.avgNonce)
    println("Average Nonce per sec: " + result.avgNoncePerSec)
    println()
}
