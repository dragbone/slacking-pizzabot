package com.dragbone.slackbot

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackUser

interface ICommand {
    val name: String
    val requiresAdmin: Boolean
    fun execute(args: String, channel: SlackChannel, sender: SlackUser?): Iterable<String>
}