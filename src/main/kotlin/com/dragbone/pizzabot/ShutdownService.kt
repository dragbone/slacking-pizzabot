package com.dragbone.pizzabot

import java.net.ServerSocket

class ShutdownService {
    val killCommand = "GET /DEATHPILL"
    fun listen() {
        var running = true
        val serverSocket = ServerSocket(1024)
        println("Started")
        while (running) {
            try {
                serverSocket.accept().use {
                    val stream = it.inputStream
                    val sb = StringBuffer()
                    do {
                        val data = stream.read()
                        sb.append(data.toChar())
                    } while (data != -1 && sb.commonPrefixWith(killCommand).length == sb.length)

                    if (sb.toString().startsWith(killCommand)) {
                        println("DEATHPILL")
                        running = false
                    }
                }
            } catch (e: Exception) {
                println(e)
                Thread.sleep(1000)
            }
        }
    }
}