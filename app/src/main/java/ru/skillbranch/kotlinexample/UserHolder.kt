package ru.skillbranch.kotlinexample

import ru.skillbranch.kotlinexample.extensions.normalizePhone

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser (
        fullName: String,
        email: String,
        password: String
    ): User {
        val user = User.makeUser(fullName, email = email, password = password)
        when{
            !map.containsKey(user.login) -> {
                map[user.login] = user
                return user
            }
            else -> throw IllegalArgumentException("A user with this email already exists")
        }
    }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String): User {
        val realPhone = rawPhone.normalizePhone()
        val user = User.makeUser(fullName, phone = realPhone)
        when{
            map.containsKey(user.login) -> throw IllegalArgumentException("A user with this phone " +
                    "already exists")
            else -> {
                map[user.login] = user
                return user
            }
        }
    }

    fun loginUser(login: String, password: String): String? {
        val realLogin = if(login.first() == '+'){
            login.normalizePhone().toString()
        } else {
            login.trim()
        }
        return map[realLogin]?.run {
            if(checkPassword(password)){
                this.userInfo
            } else {
                null
            }
        }
    }

    fun clearMap() {
        map.clear()
    }

    fun requestAccessCode(login: String) {
        val realLogin = login.normalizePhone()
        if( map.containsKey(realLogin) ){
            map[realLogin]?.changeAccessCode()
        } else {
            throw IllegalArgumentException("A user with this phone is not exist")
        }
    }

    fun importUsers(source: List<String>): List<User> {
        val result = mutableListOf<User>()
        for (item in source) {
            val (rawFullName, rawEmail, rawSaltHash, rawPhone) = item.split(";")
            val fullName = rawFullName.trim()
            val email = normalizeField(rawEmail)
            val saltHash = normalizeField(rawSaltHash)
            val phone = normalizeField(rawPhone)?.normalizePhone()
            val password: String?
            password = when (saltHash) {
                null -> null
                else -> {
                    val (salt, hash) = saltHash.trim().split(":")
                    "${salt}${hash}"
                }
            }
            val currentUser = User.makeCsvUser(
                fullName = fullName,
                email = email,
                phone = phone,
                password = password
            )
            result.add(currentUser)
        }
        return result
    }

    private fun normalizeField(source: String) = if (source.isEmpty()) null else source.trim()
}

