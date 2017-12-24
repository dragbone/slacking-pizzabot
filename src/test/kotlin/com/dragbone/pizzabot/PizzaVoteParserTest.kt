package com.dragbone.pizzabot

import org.junit.Assert.*
import org.junit.Test
import java.text.ParseException

class PizzaVoteParserTest {
    @Test fun mapDay() {
        val parser = PizzaVoteParser()
        assertEquals(DayOfWeek.MONDAY, parser.mapToDay("Montag"))
        assertEquals(DayOfWeek.MONDAY, parser.mapToDay("monday"))
        assertEquals(DayOfWeek.MONDAY, parser.mapToDay("mo"))
        assertEquals(DayOfWeek.MONDAY, parser.mapToDay("mon"))

        assertEquals(DayOfWeek.TUESDAY, parser.mapToDay("Tue"))
        assertEquals(DayOfWeek.TUESDAY, parser.mapToDay("Di"))
        assertEquals(DayOfWeek.TUESDAY, parser.mapToDay("dienstag"))

        assertEquals(DayOfWeek.WEDNESDAY, parser.mapToDay("mi"))
        assertEquals(DayOfWeek.WEDNESDAY, parser.mapToDay("wed"))
        assertEquals(DayOfWeek.WEDNESDAY, parser.mapToDay("mittwoch"))
        assertEquals(DayOfWeek.WEDNESDAY, parser.mapToDay("wednesday"))

        assertEquals(DayOfWeek.THURSDAY, parser.mapToDay("thursday"))
        assertEquals(DayOfWeek.THURSDAY, parser.mapToDay("Donnerstag"))
        assertEquals(DayOfWeek.THURSDAY, parser.mapToDay("Do"))
        assertEquals(DayOfWeek.THURSDAY, parser.mapToDay("Thu"))

        assertEquals(DayOfWeek.FRIDAY, parser.mapToDay("Freitag"))
        assertEquals(DayOfWeek.FRIDAY, parser.mapToDay("friday"))
        assertEquals(DayOfWeek.FRIDAY, parser.mapToDay("Fr"))
        assertEquals(DayOfWeek.FRIDAY, parser.mapToDay("fri"))

        assertEquals(DayOfWeek.SATURDAY, parser.mapToDay("Samstag"))
        assertEquals(DayOfWeek.SATURDAY, parser.mapToDay("Saturday"))
        assertEquals(DayOfWeek.SATURDAY, parser.mapToDay("Sa"))
        assertEquals(DayOfWeek.SATURDAY, parser.mapToDay("sam"))

        assertEquals(DayOfWeek.SUNDAY, parser.mapToDay("Sonntag"))
        assertEquals(DayOfWeek.SUNDAY, parser.mapToDay("Sunday"))
        assertEquals(DayOfWeek.SUNDAY, parser.mapToDay("So"))
        assertEquals(DayOfWeek.SUNDAY, parser.mapToDay("su"))
    }

    @Test fun getNoneList() {
        assertTrue(PizzaVoteParser.noneList.contains("none"))
        assertTrue(PizzaVoteParser.noneList.contains("nada"))
        assertTrue(PizzaVoteParser.noneList.contains("never"))
        assertTrue(PizzaVoteParser.noneList.contains("{}"))
        assertTrue(PizzaVoteParser.noneList.contains("[]"))

        assertFalse(PizzaVoteParser.noneList.contains("mo"))
        assertFalse(PizzaVoteParser.noneList.contains("di"))
        assertFalse(PizzaVoteParser.noneList.contains("mi"))
        assertFalse(PizzaVoteParser.noneList.contains("do"))
        assertFalse(PizzaVoteParser.noneList.contains("fr"))
    }

    @Test fun parsePizzaVote() {
        val parser = PizzaVoteParser()

        assertEquals(setOf(Vote(DayOfWeek.MONDAY, 1f)), parser.parsePizzaVote("mo"))

        assertEquals(setOf(
                Vote(DayOfWeek.MONDAY, 1f),
                Vote(DayOfWeek.TUESDAY, 1f),
                Vote(DayOfWeek.WEDNESDAY, 1f)),
                parser.parsePizzaVote("mo,tue,wed"))

        assertEquals(setOf(
                Vote(DayOfWeek.MONDAY, 1f),
                Vote(DayOfWeek.TUESDAY, 0.5f),
                Vote(DayOfWeek.WEDNESDAY, 1f)),
                parser.parsePizzaVote("mo,(tue),wed"))

        assertEquals(setOf(
                Vote(DayOfWeek.MONDAY, 1f),
                Vote(DayOfWeek.TUESDAY, 1f),
                Vote(DayOfWeek.WEDNESDAY, 1f),
                Vote(DayOfWeek.FRIDAY, 1f)),
                parser.parsePizzaVote("mo-wed,fr"))

        assertEquals(setOf(
                Vote(DayOfWeek.MONDAY, 0.5f),
                Vote(DayOfWeek.TUESDAY, 0.5f),
                Vote(DayOfWeek.WEDNESDAY, 0.5f),
                Vote(DayOfWeek.FRIDAY, 1f)),
                parser.parsePizzaVote("(mo-wed),fr"))

        assertEquals(setOf(
                Vote(DayOfWeek.MONDAY, 1f),
                Vote(DayOfWeek.TUESDAY, 1f),
                Vote(DayOfWeek.WEDNESDAY, 1f),
                Vote(DayOfWeek.FRIDAY, 1f)),
                parser.parsePizzaVote("mo - wed, fr"))

        assertEquals(setOf(
                Vote(DayOfWeek.MONDAY, 0.5f),
                Vote(DayOfWeek.TUESDAY, 0.5f),
                Vote(DayOfWeek.WEDNESDAY, 0.5f),
                Vote(DayOfWeek.FRIDAY, 1f)),
                parser.parsePizzaVote("(mo - wed), fr"))

        assertEquals(setOf(
                Vote(DayOfWeek.MONDAY, 1f),
                Vote(DayOfWeek.TUESDAY, 1f),
                Vote(DayOfWeek.WEDNESDAY, 1f),
                Vote(DayOfWeek.FRIDAY, 1f)),
                parser.parsePizzaVote(" mo - wed , fr"))

        assertEquals(setOf(
                Vote(DayOfWeek.MONDAY, 0.5f),
                Vote(DayOfWeek.TUESDAY, 0.5f),
                Vote(DayOfWeek.WEDNESDAY, 0.5f),
                Vote(DayOfWeek.FRIDAY, 0.5f)),
                parser.parsePizzaVote("( mo - wed ), ( fr)"))

        // Chris did nothing wrong
        assertEquals(setOf(
                Vote(DayOfWeek.WEDNESDAY, 1f),
                Vote(DayOfWeek.THURSDAY, 1f),
                Vote(DayOfWeek.FRIDAY, 1f)),
                parser.parsePizzaVote("Mi, do, fr"))
    }

    @Test(expected = ParseException::class) fun parsePizzaVote_invalidRange() {
        val parser = PizzaVoteParser()
        parser.parsePizzaVote("di-mo")
    }

    @Test(expected = IllegalArgumentException::class) fun parsePizzaVote_doubleVote() {
        val parser = PizzaVoteParser()
        parser.parsePizzaVote("mo-wed,di")
    }
}