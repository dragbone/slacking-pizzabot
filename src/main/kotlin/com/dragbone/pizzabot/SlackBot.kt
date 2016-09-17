package com.dragbone.pizzabot

import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener

class SlackBot : SlackMessagePostedListener {
    val commandIndicator = "!"
    val channelName: String
    val commandMap: Map<String, ICommand>

    constructor(channelName: String, commands: Iterable<ICommand>) {
        this.channelName = channelName
        commandMap = commands.associateBy { it.name }
        if (commandMap.count() != commands.count()) {
            val duplicates = commands.groupBy { it.name }.filter { it.value.count() > 1 }.keys.joinToString()
            throw IllegalArgumentException("Commands with identical names added: $duplicates")
        }
    }

    override fun onEvent(event: SlackMessagePosted, session: SlackSession) {
        val messageContent = event.messageContent
        val channel = event.channel
        val sender = event.sender
        println("Received message in channel ${channel.name}, filtering for $channelName")
        if (messageContent.length == 0 || sender.isBot || !channelName.equals(channel.name))
            return

        val firstWord = messageContent.substringBefore(' ')

        // Check if message is a command
        if (firstWord.startsWith(commandIndicator)) {
            // Find command
            val commandName = firstWord.removePrefix(commandIndicator)
            println("Command: $commandName")
            try {
                val command = commandMap[commandName]!!

                // Execute
                val messages = command.execute(messageContent.substringAfter(' '), channel, sender)

                println("Going to send message $messages")

                // Send messages
                messages.forEach { session.sendMessage(channel, it) }
            } catch (e: Exception) {
                println("Exception while executing command $commandName: $e")
            }
        }
    }
}