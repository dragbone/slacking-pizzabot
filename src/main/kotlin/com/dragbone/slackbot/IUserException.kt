package com.dragbone.slackbot

abstract class IUserException : Throwable() {
    abstract val userMessage: String
}