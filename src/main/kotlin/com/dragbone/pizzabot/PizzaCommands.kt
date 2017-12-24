package com.dragbone.pizzabot

import com.dragbone.slackbot.ICommand
import com.dragbone.slackbot.ICronTask
import com.dragbone.slackbot.IUserException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackUser
import java.io.File
import java.util.*

fun saveState(pizzaState: PizzaVoteState) {
    jacksonObjectMapper().writeValue(File("pizzastate.json"), pizzaState)
}

class PizzaCommand(val pizzaState: PizzaVoteState) : ICommand {
    val pizzaDayChooser = PizzaDayChooser(pizzaState)
    override val name = "pizza"
    override val requiresAdmin = false
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser?): Iterable<String> {
        val aggregatedVotes = pizzaState.summedVotesByDay()
        val pizzaDay = pizzaDayChooser.choosePizzaDay(aggregatedVotes)
        saveState(pizzaState)

        if (pizzaDay == null)
            return listOf("Not enough votes yet.")

        val votesOnPizzaDay = pizzaState.votesByDay()[pizzaDay]!!
        val userMap = channel.members.associateBy { it.id }
        val participants = votesOnPizzaDay.map {
            val username = userMap[it.user]?.userName ?: it.user
            if (it.vote.strength >= 1f) username else "($username)"
        }.joinToString()
        return listOf(
                "You should eat :pizza: on ${pizzaDay.toPrettyString()}.",
                "Participants: $participants"
        )
    }
}

class VoteCommand(val pizzaState: PizzaVoteState) : ICommand {
    class VoteException(private val sender: SlackUser) : IUserException() {
        override val userMessage: String
            get() = "Sorry @${sender.userName}, I didn't understand you."
    }

    override val name = "vote"
    override val requiresAdmin = false
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser?): Iterable<String> {
        sender!!
        val votes = try {
            PizzaVoteParser().parsePizzaVote(args)
        } catch (e: Exception) {
            throw VoteException(sender)
        }
        pizzaState.vote(sender.id, votes)

        val messages = listOf(
                "Thank you for your vote @${sender.userName}.",
                "You voted for {${votes.joinToString { it.day.toPrettyString() }}}."
        )

        saveState(pizzaState)

        return messages.union(PizzaCommand(pizzaState).execute("", channel, sender))
    }
}

class ResetCommand(val pizzaState: PizzaVoteState) : ICommand {
    override val name = "clear"
    override val requiresAdmin = true
    override fun execute(args: String, channel: SlackChannel, sender: SlackUser?): Iterable<String> {
        pizzaState.resetVotes()
        pizzaState.currentRecommendedDay = null
        pizzaState.reminderTriggered = false
        saveState(pizzaState)
        return listOf("Votes have been reset.")
    }
}

class InfoCommand : ICommand {
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
            saveState(pizzaState)
            return listOf("REMINDER!").union(PizzaCommand(pizzaState).execute("", channel, null))
        }
        return emptyList()
    }
}