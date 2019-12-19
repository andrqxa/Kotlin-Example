package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import ru.skillbranch.kotlinexample.extensions.normalizePhone
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

class User private  constructor(
    private val firstName: String,
    private val lastName: String?,
    email: String? = null,
    rawPhone: String? = null,
    meta: Map<String, Any>? = null
){
    var userInfo: String

    private val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .capitalize()

    private val initials: String?
        get() = listOfNotNull(firstName, lastName)
            .map { it.first().toUpperCase() }
            .joinToString(" ")


    var phone: String? = null
        set( value ) {
            field = value?.normalizePhone()
        }


    private var _login: String? = null

    var login: String
        set( value ) {
            _login = value.toLowerCase()
        }
        get() = _login!!

    private var salt: String = ByteArray(16).also { SecureRandom().nextBytes(it) }.toString()

    private lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var accessCode: String? = null

//    for email
    constructor(
        firstName: String,
        lastName: String?,
        email: String,
        password: String
) : this(firstName, lastName, email = email, meta = mapOf("auth" to Method.password)) {
        println("Secondary mail constructor")
        passwordHash = encrypt(password)
    }


    //    for phone
    constructor(
        firstName: String,
        lastName: String?,
        rawPhone: String
    ) : this(firstName, lastName, rawPhone = rawPhone, meta = mapOf("auth" to Method.sms)) {
        println("Secondary phone constructor")
        val code = changeAccessCode()
        setAccessCodeToUser(rawPhone, code)
    }

    //    for csv_phone
    constructor(
        firstName: String,
        lastName: String?,
        rawPhone: String,
        method: Method
    ) : this(firstName, lastName, rawPhone = rawPhone, meta = mapOf("src" to method.name)) {
        println("Secondary phone csv constructor")
        val code = changeAccessCode()
        setAccessCodeToUser(rawPhone, code)
    }

    //    for csv_mail
    constructor(
        firstName: String,
        lastName: String?,
        email: String,
        salt: String,
        hash: String,
        method: Method
    ) : this(firstName, lastName, email = email, meta = mapOf("src" to method.name)) {
        this.salt = salt
        passwordHash = hash
    }

    init {
        println("First init block, primary constructor was called")

        check(!firstName.isBlank()){ "FirstName must be not blank" }
        check(email.isNullOrBlank() || rawPhone.isNullOrBlank()){ "Email or phone must be not blank" }

        phone = rawPhone
        login = email ?: phone!!

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

    fun checkPassword(pass: String) = encrypt(pass) == passwordHash

    fun changePassword(oldPass: String, newPass: String) {
        if(checkPassword(oldPass)){
            passwordHash = encrypt(newPass)
        } else {
            throw IllegalArgumentException("The entered password does not match the current password")
        }
    }

    private fun encrypt(password: String) = salt.plus(password).md5()//password with salt

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5") //get MD5 algorithm
        val digest = md.digest(toByteArray()) //16 byte
        val hexString = BigInteger(1, digest).toString(16)
        return hexString.padStart(32, '0') //fill from beginning until length of 32
    }



    private fun generateAccessCode(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

        return StringBuilder().apply {
            repeat(6){
                (possible.indices).random().also { index ->
                    append(possible[index])
                }
            }
        }.toString()
    }

    private fun setAccessCodeToUser(phone: String, code: String) {
        println("... sending access code: $code on $phone")
    }

    fun changeAccessCode(): String {
        val code = generateAccessCode()
        passwordHash = encrypt(code)
        accessCode = code
        return code
    }

    companion object Factory {
        fun makeUser(
            fullName: String,
            email: String? = null,
            password: String? = null,
            phone: String? = null
        ): User {
            val (firstName, lastName) = fullName.fullNameToPair()
            return when {
                !phone.isNullOrBlank() -> User(firstName, lastName, phone)
                !email.isNullOrBlank() && !password.isNullOrBlank() ->  User(firstName, lastName, email, password)
                else -> throw IllegalArgumentException("Email or phone must be not null or blank")
            }
        }

        fun makeCsvUser(
            fullName: String,
            email: String? = null,
            phone: String? = null,
            salt: String? = null,
            hash: String? = null
        ): User {
            val (firstName, lastName) = fullName.fullNameToPair()
            return when {
                !phone.isNullOrBlank() -> User(firstName, lastName, phone, Method.csv)
                !email.isNullOrBlank() && !salt.isNullOrBlank() && !hash.isNullOrBlank() -> User(
                    firstName,
                    lastName,
                    email,
                    salt,
                    hash,
                    Method.csv
                )
                else -> throw IllegalArgumentException("Email or phone must be not null or blank")
            }
        }

        private fun String.fullNameToPair(): Pair<String, String?> {
            return this.split(" ")
                .filter { it.isNotBlank() }
                .run {
                    when(size) {
                        1 -> first() to null
                        2 -> first() to last()
                        else -> throw IllegalArgumentException("Fullname must contain only first nme " +
                                "and last name, current split result $this@fullNameToPair")
                    }
                }
        }
    }
}

enum class Method {
    sms,
    password,
    csv
}




