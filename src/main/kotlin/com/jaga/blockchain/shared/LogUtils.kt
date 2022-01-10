package com.jaga.blockchain.shared

import mu.toKLogger
import org.slf4j.LoggerFactory

fun <T>logger(clazz: Class<T>) = LoggerFactory.getLogger(clazz).toKLogger()
