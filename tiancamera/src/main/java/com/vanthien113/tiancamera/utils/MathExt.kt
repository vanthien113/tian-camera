package com.vanthien113.tiancamera.utils

fun <T> Boolean.triadOperator(firstElement: T, secondElement: T): T {
    if (this) {
        return firstElement
    }
    return secondElement
}