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
}

