package com.dragbone.pizzabot

import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener

class PizzaBot(val channelName: String) : SlackMessagePostedListener {
    val commandIndicator = "!"

    val commandMap: Map<String, Command> = mapOf(
            "pizza" to PizzaCommand(),
            "vote" to VoteCommand()
    )

    override fun onEvent(event: SlackMessagePosted, session: SlackSession) {
        val messageContent = event.messageContent
        val channel = event.channel
        val sender = event.sender
        if (messageContent.length == 0 || sender.isBot || !channelName.equals(channel.name))
            return

        val firstWord = messageContent.substringBefore(' ')

        // Check if message is a command
        if (firstWord.startsWith(commandIndicator)) {
            // Find command
            val commandName = firstWord.removePrefix(commandIndicator)
            val command = commandMap[commandName]!!

            // Execute
            val messages = command.process(messageContent.substringAfter(' '), channel, sender)

            // Send messages
            messages.forEach { session.sendMessage(channel, it) }
        }
    }
}