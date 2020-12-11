package org.coroutines.benchmark

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun AsynchronousFileChannel.ReadBuffer(buf: ByteBuffer): Int =
    suspendCoroutine { cont ->
        read(buf, 0L, Unit, object : CompletionHandler<Int, Unit> {
            override fun completed(bytesRead: Int, attachment: Unit) {
                cont.resume(bytesRead)
            }

            override fun failed(exception: Throwable, attachment: Unit) {
                cont.resumeWithException(exception)
            }
        })
    }

suspend fun AsynchronousFileChannel.WriteBuffer(buf: ByteBuffer): Int =
    suspendCoroutine { cont ->
        write(buf, 0L, Unit, object : CompletionHandler<Int, Unit> {
            override fun completed(bytesWrote: Int, attachment: Unit) {
                cont.resume(bytesWrote)
            }

            override fun failed(exception: Throwable, attachment: Unit) {
                cont.resumeWithException(exception)
            }
        })
    }

suspend fun AsynchronousFileChannel.WriteString(string: String): Int =
    WriteBuffer(ByteBuffer.wrap(string.toByteArray()))

fun main(args: Array<String>) {
    //val numCoroutines = args[0].toLong()

    val filePath = "D:\\async_file_write.txt"
    val file = Paths.get(filePath)

    val ch = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
    GlobalScope.launch {
        ch.WriteBuffer(ByteBuffer.wrap("Kotlin".toByteArray()))
    }

//    val startTime = System.currentTimeMillis()
//    val endTime = System.currentTimeMillis()
//
//    println("Execution time: " + (endTime - startTime) + "ms.")
}