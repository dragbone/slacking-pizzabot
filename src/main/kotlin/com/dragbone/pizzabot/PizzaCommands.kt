package com.dragbone.pizzabot

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackUser
import java.util.*

class PizzaCommand(val pizzaState: PizzaVoteState) : ICommand {
    override val name: String = "pizza"
    override fun process(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        val aggregatedVotes = pizzaState.getSummedVotesByDay()
        val formattedVotes = aggregatedVotes.entries.joinToString { "${it.key.toPrettyString()}=${it.value}" }
        return listOf("Summed votes: $formattedVotes")
    }
}

class VoteCommand(val pizzaState: PizzaVoteState) : ICommand {
    override val name: String = "vote"
    override fun process(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        val votes = PizzaVoteParser().parsePizzaVote(args)
        pizzaState.vote(sender.id, votes)
        return listOf(
                "Thank you for your vote ${sender.userName}.",
                "You voted for ${votes.joinToString(", ") { it.day.toPrettyString() }}."
        )
    }
}