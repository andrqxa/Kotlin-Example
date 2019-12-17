package ru.skillbranch.kotlinexample.extensions

fun String.normalizePhone(): String? {
    println("Added __'$this'__ on validation")
    val shortString = this.trim()
    val templatePhone = "\\+\\s*\\d[^\\p{L}]*".toRegex()
    if(shortString.isBlank() || !templatePhone.matches(shortString)){
        throw IllegalArgumentException("Enter a valid phone " +
                "number starting with a and containing 11 digits")
    }
    val phone = shortString.substring(1).replace("\\D".toRegex(), "")
    if(phone.length != 11){
        throw IllegalArgumentException("Enter a valid phone " +
                "number starting with a and containing 11 digits")
    }
    return "+$phone"
}