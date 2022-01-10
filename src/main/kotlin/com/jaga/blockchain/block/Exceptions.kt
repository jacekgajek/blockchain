package com.jaga.blockchain.block

class BlockChainValidationFailedException(message: String = "Block chain validation failed.") : Exception(message)
class BlockChainUninitializedException
    : Exception(
    "Cannot append a new block because the block chain was never received. " +
        "Please wait for another node to publish an updated block chain before mining a new block."
)
