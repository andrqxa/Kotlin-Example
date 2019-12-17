package ru.skillbranch.kotlinexample.extensions

fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    for (item in this) {
        if (predicate(item)) {
            break
        }
        result.add(item)
    }

    return result
}