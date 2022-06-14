package com.m37moud.mynewlang.util

fun String.intOrString(): Boolean{
    return when(toIntOrNull()) {
        null -> false
        else -> true
    }
}