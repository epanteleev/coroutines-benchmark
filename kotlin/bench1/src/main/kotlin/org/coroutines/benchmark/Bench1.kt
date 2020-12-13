package org.coroutines.benchmark

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

val MESSAGE = "hello "
val NUM_HELLO = 1_000_000

fun task(channel: AsynchronousFileChannel, numCoroutines: Long) {
    var startPos = 0L
    var inc = 0
    for (i in 1L..numCoroutines) {
        runBlocking {
            launch {
                while (inc < NUM_HELLO) {
                    channel.WriteString(MESSAGE, startPos)
                    startPos += MESSAGE.length
                    inc += 1
                }
            }
        }
    }
}

suspend fun AsynchronousFileChannel.WriteBuffer(buf: ByteBuffer, pos: Long = 0): Int =
        suspendCoroutine { cont ->
            write(buf, pos, Unit, object : CompletionHandler<Int, Unit> {
                override fun completed(bytesWrote: Int, attachment: Unit) {
                    cont.resume(bytesWrote)
                }

                override fun failed(exception: Throwable, attachment: Unit) {
                    cont.resumeWithException(exception)
                }
            })
        }

suspend fun AsynchronousFileChannel.WriteString(string: String, pos: Long = 0): Int =
        WriteBuffer(ByteBuffer.wrap(string.toByteArray()), pos)

fun main(args: Array<String>) {
    val numCoroutines = args[0].toLong()
    val file = Paths.get(args[1])

    val ch = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE)

    val startTime = System.currentTimeMillis()
    task(ch, numCoroutines)
    val endTime = System.currentTimeMillis()

    println("Execution time: " + (endTime - startTime) + "ms.")
    ch.close()
}