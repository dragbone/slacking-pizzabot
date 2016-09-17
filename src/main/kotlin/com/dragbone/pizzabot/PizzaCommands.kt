package com.dragbone.pizzabot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackUser
import java.io.File
import java.util.*

class PizzaCommand(val pizzaState: PizzaVoteState) : ICommand {
    val pizzaDayChooser = PizzaDayChooser(pizzaState)
    override val name = "pizza"
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        val aggregatedVotes = pizzaState.summedVotesByDay()
        val formattedVotes = aggregatedVotes.entries.joinToString { "${it.key.toPrettyString()}=${it.value}" }
        return listOf(
                "Summed votes: $formattedVotes",
                "You should eat pizza on ${pizzaDayChooser.choosePizzaDay(aggregatedVotes).toPrettyString()}."
        )
    }
}

class VoteCommand(val pizzaState: PizzaVoteState) : ICommand {
    override val name = "vote"
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        val votes = PizzaVoteParser().parsePizzaVote(args)
        pizzaState.vote(sender.id, votes)

        // Save state
        jacksonObjectMapper().writeValue(File("pizzastate.json"), pizzaState)

        val messages = listOf(
                "Thank you for your vote ${sender.userName}.",
                "You voted for ${votes.joinToString(", ") { it.day.toPrettyString() }}."
        )

        if (pizzaState.getPizzaVotes().count() >= 3) {
            return messages.union(PizzaCommand(pizzaState).execute("", channel, sender))
        }
        return messages
    }
}

class ResetCommand(val pizzaState: PizzaVoteState) : ICommand {
    override val name = "clear"
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        pizzaState.resetVotes()
        return emptyList()
    }
}