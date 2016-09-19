package com.dragbone.di

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.defaultType
import kotlin.reflect.jvm.javaType


class DiContainer {
    class DiEntry<out T : Any>(generator: () -> T) {
        val instance: T by lazy { generator() }
    }

    val register: MutableMap<KType, DiEntry<Any>> = mutableMapOf()

    inline fun <reified T : Any> instantiate() = instantiate(T::class)

    fun <T : Any> instantiateAll(vararg cls: KClass<T>) = cls.map { instantiate(it) }

    fun <T : Any> instantiate(cls: KClass<T>): T {
        val dep = register.keys
        val constructors = cls.constructors.filter { it.parameters.all { dep.contains(it.type) } }

        val cons = constructors.first()
        val params = cons.parameters
        val args = params.map {
            register[it.type]!!.instance
        }.toTypedArray()
        return cons.call(*args)
    }

    inline fun <reified T : Any> setupConcrete() {
        register[T::class.defaultType] = DiEntry { instantiate<T>() }
    }

    inline fun <reified I : Any, reified T : I> setup() {
        register[I::class.defaultType] = DiEntry { instantiate<T>() }
    }
}