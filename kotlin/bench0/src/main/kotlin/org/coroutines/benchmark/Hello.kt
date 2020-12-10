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

    val startTime = System.currentTimeMillis()
    task(numCoroutines)
    val endTime = System.currentTimeMillis()

    println("Execution time: " + (endTime - startTime) + "ms.")
}

