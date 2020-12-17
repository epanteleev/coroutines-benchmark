package org.coroutines.benchmark

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

fun task(numCoroutines: Long) {
    val c = AtomicLong()

    for (i in 1..numCoroutines) {
        GlobalScope.launch {
            c.addAndGet(1)
        }
    }
}

fun main(args: Array<String>) {
    val numCoroutines = args[0].toLong()
	task(1_000_000) //warm up

    val startTime = System.currentTimeMillis()
    task(numCoroutines)
    val endTime = System.currentTimeMillis()

    println("Execution time: " + (endTime - startTime) + "ms.")
}

