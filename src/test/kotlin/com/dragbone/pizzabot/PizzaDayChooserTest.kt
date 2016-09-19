package com.dragbone.pizzabot

import org.junit.Test

import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class PizzaDayChooserTest(val cl: Class<PizzaDayChooser>) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(arrayOf<Any>(PizzaDayChooser::class.java))
        }
    }

    private fun getPizzaDayChooser(): PizzaDayChooser {
        return cl.constructors.first().newInstance(PizzaVoteState()) as PizzaDayChooser
    }

    @Test fun choosePizzaDay() {
        val chooser = getPizzaDayChooser()
        val k = mapOf(
                DayOfWeek.MONDAY to 1.0,
                DayOfWeek.TUESDAY to 2.0,
                DayOfWeek.WEDNESDAY to 1.0
        )

        assertEquals(DayOfWeek.TUESDAY, chooser.choosePizzaDay(k))
    }


}