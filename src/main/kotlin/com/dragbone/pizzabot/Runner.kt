package com.dragbone.pizzabot

import com.dragbone.IDisposeable
import com.dragbone.slackbot.ICommand
import com.dragbone.slackbot.Props
import com.dragbone.slackbot.SlackBot
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import java.io.File

fun main(args: Array<String>) {
    // Deploy test
    if (args.count() < 2) {
        println("Usage: program <bot-api-token> <channel-name>")
    }

    val botSession = BotSession(args[0], args[1])

    ShutdownService().listen()

    botSession.dispose()
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
    val pizzaState: PizzaVoteState

    constructor(botToken: String, channel: String) {
        val file = File(stateFile)
        pizzaState = if (file.exists()) jacksonObjectMapper().readValue(file, PizzaVoteState::class.java) else PizzaVoteState()

        session = SlackSessionFactory.createWebSocketSlackSession(botToken)
        session.connect()

        bot = SlackBot(session, channel, commands(), listOf(RemindCronTask(pizzaState)))
        session.addMessagePostedListener(bot)

        val ch = session.channels.single { it.name == channel }
        session.sendMessage(ch, "I HAVE BEEN REBORN! (revision:${Props()[Props.Values.Revision]})")
    }

    private fun commands(): List<ICommand> {
        return listOf(
                VoteCommand(pizzaState),
                PizzaCommand(pizzaState),
                ResetCommand(pizzaState),
                InfoCommand()
        )
    }

    override fun dispose() {
        bot.dispose()
        session.removeMessagePostedListener(bot)
        session.disconnect()
    }
}
