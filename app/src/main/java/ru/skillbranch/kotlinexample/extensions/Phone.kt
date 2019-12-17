package ru.skillbranch.kotlinexample.extensions

fun String.normalizePhone(): String? {
    val shortString = this.trim()
    val templatePhone = "\\+\\d[^\\p{L}]*".toRegex()
    if(shortString.isBlank() || !templatePhone.matches(shortString)){
        throw IllegalArgumentException("Enter a valid phone " +
                "number starting with a and containing 11 digits")
    }
    val phone = shortString.substring(1).replace("\\D".toRegex(), "")
//    val newPhone = phone.replace("\\D".toRegex(), "")
    if(phone.length != 11){
        throw IllegalArgumentException("Enter a valid phone " +
                "number starting with a and containing 11 digits")
    }
    return "+$phone"
}