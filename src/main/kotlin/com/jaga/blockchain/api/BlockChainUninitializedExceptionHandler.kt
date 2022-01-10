package com.jaga.blockchain.api

import com.jaga.blockchain.block.BlockChainUninitializedException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Singleton
class BlockChainUninitializedExceptionHandler : ExceptionHandler<BlockChainUninitializedException, HttpResponse<Any>> {
    override fun handle(request: HttpRequest<*>, exception: BlockChainUninitializedException): HttpResponse<Any> {
        return HttpResponse.badRequest(
            mapOf(
                "error" to exception.message
            )
        )
    }
}