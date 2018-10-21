package sample

import kotlinx.coroutines.runBlocking

@ExperimentalUnsignedTypes
fun main(): Unit = runBlocking { ShootumsGame().run() }