package com.dragbone

import java.util.*

fun <T> List<T>.choose(): T = this[Random().nextInt(size)]