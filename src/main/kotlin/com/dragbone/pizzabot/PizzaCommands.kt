package com.dragbone.pizzabot

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackUser

class PizzaCommand : Command {
    override fun process(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        return listOf("hi ${sender.userName}")
    }
}

class VoteCommand : Command {
    override fun process(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        val votes = PizzaVoteParser().parsePizzaVote(args)
        return listOf("Thank you for your vote ${sender.userName}.", "You voted for ${votes.joinToString(", ") { it.day.toString() }}")
    }
}