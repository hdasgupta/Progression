package com.progression.parsertokenizer

class Atomic<T>(var obj: T) {
    fun get(): T = obj
    fun set(obj: T): Unit {
        this.obj = obj
    }
}