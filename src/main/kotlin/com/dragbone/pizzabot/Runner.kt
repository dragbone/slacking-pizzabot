package com.dragbone.pizzabot

import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import kotlin.reflect.KFunction

/**
 * Created by dragbone on 13.09.2016.
 */


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
            (something as java.lang.Object).wait()
        }
    }
}

class BotSession(val botToken: String, val channel: String) : IDisposeable {
    val session: SlackSession
    val bot: PizzaBot

    init {
        session = SlackSessionFactory.createWebSocketSlackSession(botToken)
        session.connect()

        bot = PizzaBot(channel)
        session.addMessagePostedListener(bot)
    }

    override fun dispose() {
        session.removeMessagePostedListener(bot)
        session.disconnect()
    }
}