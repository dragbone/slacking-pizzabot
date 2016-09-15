package com.dragbone.pizzabot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackUser
import java.io.File
import java.util.*

class PizzaCommand(val pizzaState: PizzaVoteState) : ICommand {
    override val name: String = "pizza"
    override fun process(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        val aggregatedVotes = pizzaState.summedVotesByDay()
        val formattedVotes = aggregatedVotes.entries.joinToString { "${it.key.toPrettyString()}=${it.value}" }
        return listOf("Summed votes: $formattedVotes")
    }
}

class VoteCommand(val pizzaState: PizzaVoteState) : ICommand {
    override val name: String = "vote"
    override fun process(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        val votes = PizzaVoteParser().parsePizzaVote(args)
        pizzaState.vote(sender.id, votes)

        // save state
        jacksonObjectMapper().writeValue(File("pizzastate.json"), pizzaState)

        return listOf(
                "Thank you for your vote ${sender.userName}.",
                "You voted for ${votes.joinToString(", ") { it.day.toPrettyString() }}."
        )
    }
}