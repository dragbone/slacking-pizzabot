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
        val pizzaDay = pizzaDayChooser.choosePizzaDay(aggregatedVotes)

        // Save state
        jacksonObjectMapper().writeValue(File("pizzastate.json"), pizzaState)

        return listOf(
                "Summed votes: $formattedVotes",
                "You should eat pizza on ${pizzaDay.toPrettyString()}."
        )
    }
}

class VoteCommand(val pizzaState: PizzaVoteState) : ICommand {
    override val name = "vote"
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        val votes = PizzaVoteParser().parsePizzaVote(args)
        pizzaState.vote(sender.id, votes)

        val messages = listOf(
                "Thank you for your vote ${sender.userName}.",
                "You voted for ${votes.joinToString(", ") { it.day.toPrettyString() }}."
        )

        if (pizzaState.getPizzaVotes().count() >= 3) {
            return messages.union(PizzaCommand(pizzaState).execute("", channel, sender))
        }

        // Save state
        jacksonObjectMapper().writeValue(File("pizzastate.json"), pizzaState)

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

class InfoCommand() : ICommand {
    override val name = "info"
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser): Iterable<String> {
        return listOf(
                "I'm a slack bot and I love pizza!",
                "You can find my code on https://github.com/dragbone/slacking-pizzabot"
        )
    }
}