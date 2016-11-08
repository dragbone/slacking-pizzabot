package com.dragbone.slackbot

import com.ullink.slack.simpleslackapi.SlackChannel

interface ICronTask {
    fun run(channel: SlackChannel): Iterable<String>
}