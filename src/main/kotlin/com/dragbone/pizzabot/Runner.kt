package com.dragbone.pizzabot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import java.io.File
import kotlin.reflect.KFunction

fun main(args: Array<String>) {
    if (args.count() < 2) {
        println("Usage: program <bot-api-token> <channel-name>")
    }

    val botSession = BotSession(args[0], args[1])

    // TODO: this doesn't work :(
    Runtime.getRuntime().addShutdownHook(Thread {
        botSession.dispose()
    })

    waitForBetterTimes { botSession.session.isConnected }
}

fun waitForBetterTimes(check: () -> Boolean) {
    val something: Any = Any()
    synchronized(something) {
        while (check()) {
            @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
            (something as java.lang.Object).wait()
        }
    }
}

val stateFile = "pizzastate.json"

class BotSession : IDisposeable {
    val session: SlackSession
    val bot: SlackBot

    constructor(botToken: String, channel: String) {
        session = SlackSessionFactory.createWebSocketSlackSession(botToken)
        session.connect()

        bot = SlackBot(channel, commands())
        session.addMessagePostedListener(bot)
    }

    private fun commands(): List<ICommand> {
        val file = File(stateFile)
        val pizzaState = if (file.exists()) jacksonObjectMapper().readValue(file, PizzaVoteState::class.java) else PizzaVoteState()
        return listOf(
                VoteCommand(pizzaState),
                PizzaCommand(pizzaState),
                ResetCommand(pizzaState),
                InfoCommand()
        )
    }

    override fun dispose() {
        session.removeMessagePostedListener(bot)
        session.disconnect()
    }
}