package com.dragbone.slackbot

interface ICronTask {
    fun run(): Iterable<String>
}