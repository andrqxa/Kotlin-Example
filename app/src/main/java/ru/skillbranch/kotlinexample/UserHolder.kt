package ru.skillbranch.kotlinexample

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
        val realPhone = rawPhone
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace("\\s".toRegex(), "")

        val templatePhone = "\\+\\d{11}".toRegex()
        when {
            !templatePhone.matches(realPhone) -> throw IllegalArgumentException("Enter a valid phone " +
                    "number starting with a and containing 11 digits")
        }
        val user = User.makeUser(fullName, phone = realPhone)
        when{
            map.containsKey(user.login) -> throw IllegalArgumentException("A user with this email " +
                    "already exists")
        }
        when(map.filterValues { it.phone.equals(realPhone) }.size){
            0 -> {
                map[user.login] = user
                return user
            }
            else -> throw IllegalArgumentException("A user with this phone already exists")
        }



    }

    fun loginUser(login: String, password: String): String? {
        return map[login.trim()]?.run {
            if(checkPassword(password)){
                this.userInfo
            } else {
                null
            }
        }
    }

}

