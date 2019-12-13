package ru.skillbranch.kotlinexample

import java.math.BigInteger
import java.security.MessageDigest

class User (
    private val firstName: String,
    private val lastName: String?,
    email: String? = null,
    rawPhone: String? = null,
    meta: Map<String, Any>? = null
){
    val userInfo: String

    private val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString { " " }
            .capitalize()

    private val initials: String?
        get() = listOfNotNull(firstName, lastName)
            .map { it.first().toUpperCase() }
            .joinToString { " " }
            .capitalize()


    private var phone: String? = null
        set( value ) {
            field = value?.replace("[^+\\d]".toRegex(), "")
        }


    private var _login: String? = null

    private var login: String
        set( value ) {
            _login = value?.toLowerCase()
        }
        get() = _login!!

    private lateinit var passwordHash: String

//    for email
    constructor(
        firstName: String,
        lastName: String?,
        email: String,
        password: String
    ): this ( firstName, lastName, email = email,  meta = mapOf("auth" to "phone" )){
        println("Secondary mail constructor")
        passwordHash = encrypt(password)
    }

    //    for phone
    constructor(
        firstName: String,
        lastName: String?,
        rawPhone: String
    ): this ( firstName, lastName, rawPhone = rawPhone,  meta = mapOf("auth" to "phone" )){
        println("Secondary phone constructor")
    }

    init {
        println("First init block, primary constructor was called")
        userInfo = """
            firstName: $firstName
            lastName: $lastName
            login: $login
            fullName: $fullName
            initials: $initials
            email: $email
            phone: $phone
            meta: $meta
        """.trimIndent()
    }

    private fun encrypt(password: String) = password.md5()//don't o that

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5") //get MD5 algorithm
        val digest = md.digest(toByteArray()) //16 byte
        val hexString = BigInteger(1, digest).toString(16)
        return hexString.padStart(32, '0') //fill from beginning until length of 32
    }
}


