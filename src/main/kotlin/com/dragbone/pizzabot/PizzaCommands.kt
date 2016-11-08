package com.dragbone.pizzabot

import com.dragbone.slackbot.ICommand
import com.dragbone.slackbot.ICronTask
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.SlackUser
import java.io.File
import java.util.*

class PizzaCommand(val pizzaState: PizzaVoteState) : ICommand {
    val pizzaDayChooser = PizzaDayChooser(pizzaState)
    override val name = "pizza"
    override val requiresAdmin = false
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser?): Iterable<String> {
        if (!pizzaState.hasVotes())
            return listOf("No votes for any day yet.")

        val aggregatedVotes = pizzaState.summedVotesByDay()
        val pizzaDay = pizzaDayChooser.choosePizzaDay(aggregatedVotes)
        // Save state
        jacksonObjectMapper().writeValue(File("pizzastate.json"), pizzaState)

        val votesOnDay = pizzaState.votesByDay()[pizzaDay]!!;
        val userMap = channel.members.associateBy { it.id }
        val participants = votesOnDay.map {
            val username = userMap[it.user]!!.userName
            if (it.vote.strength == 1f) username else "($username)"
        }.joinToString()
        return listOf(
                "You should eat :pizza: on ${pizzaDay.toPrettyString()}.",
                "Participants: $participants"
        )
    }
}

class VoteCommand(val pizzaState: PizzaVoteState) : ICommand {
    override val name = "vote"
    override val requiresAdmin = false
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser?): Iterable<String> {
        sender!!
        val votes = PizzaVoteParser().parsePizzaVote(args)
        pizzaState.vote(sender.id, votes)

        val messages = listOf(
                "Thank you for your vote ${sender.userName}.",
                "You voted for {${votes.joinToString() { it.day.toPrettyString() }}}."
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
    override val requiresAdmin = true
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser?): Iterable<String> {
        pizzaState.resetVotes()
        return listOf("Votes have been reset.")
    }
}

class InfoCommand() : ICommand {
    override val name = "info"
    override val requiresAdmin = false
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser?): Iterable<String> {
        return listOf(
                "I'm a slack bot and I love pizza!",
                "You can find my code on https://github.com/dragbone/slacking-pizzabot"
        )
    }
}

class RemindCronTask(val pizzaState: PizzaVoteState) : ICronTask {
    override fun run(channel: SlackChannel): Iterable<String> {
        if (pizzaState.reminderTriggered)
            return emptyList()
        val currentDay = DayOfWeek.current()
        if (pizzaState.currentRecommendedDay == currentDay && Calendar.getInstance()[Calendar.HOUR_OF_DAY] >= 17) {
            pizzaState.reminderTriggered = true
            return listOf("REMINDER!").union(PizzaCommand(pizzaState).execute("", channel, null))
        }
        return emptyList()
    }
}