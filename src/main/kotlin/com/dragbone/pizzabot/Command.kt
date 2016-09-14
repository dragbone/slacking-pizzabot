package com.dragbone.pizzabot

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackUser

interface Command {
    fun process(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String>
}