package com.dragbone.pizzabot

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackUser

/**
 * Created by dragbone on 13.09.2016.
 */
interface Command {
    fun process(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String>
}