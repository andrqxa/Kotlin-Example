package ru.skillbranch.kotlinexample

import org.junit.After
import org.junit.Assert
import org.junit.Test
import ru.skillbranch.kotlinexample.extensions.dropLastUntil

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @After
    fun beforeTest() = UserHolder.clearMap()


    @Test
    fun register_user_success() {
        val holder = UserHolder
        val user = holder.registerUser("John Doe", "John_Doe@unknown.com","testPass")
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: john_doe@unknown.com
            fullName: John Doe
            initials: J D
            email: John_Doe@unknown.com
            phone: null
            meta: {auth=password}
        """.trimIndent()

        Assert.assertEquals(expectedInfo, user.userInfo)
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_fail_blank() {
        val holder = UserHolder
        holder.registerUser("", "John_Doe@unknown.com","testPass")
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_fail_illegal_name() {
        val holder = UserHolder
        holder.registerUser("John Jr Doe", "John_Doe@unknown.com","testPass")
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_fail_illegal_exist() {
        val holder = UserHolder
        holder.registerUser("John Doe", "John_Doe@unknown.com","testPass")
        holder.registerUser("John Doe", "John_Doe@unknown.com","testPass")
    }

    @Test
    fun register_user_by_phone_success() {
        val holder = UserHolder
        val user = holder.registerUserByPhone("John Doe", "    +   7 ( \\9  17 -) *971 11-11")
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: +79179711111
            fullName: John Doe
            initials: J D
            email: null
            phone: +79179711111
            meta: {auth=sms}
        """.trimIndent()

        Assert.assertEquals(expectedInfo, user.userInfo)
        Assert.assertNotNull(user.accessCode)
        Assert.assertEquals(6, user.accessCode?.length)
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_by_phone_fail_blank() {
        val holder = UserHolder
        holder.registerUserByPhone("", "+7 (917) 971 11-11")
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_by_phone_fail_letter() {
        val holder = UserHolder
        holder.registerUserByPhone("", "+7 (91a7) 971 11-11")
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_by_phone_fail_illegal_name() {
        val holder = UserHolder
        holder.registerUserByPhone("John Jr Doe", "+7 (XXX) XX XX-XX")
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_failby_phone_illegal_exist() {
        val holder = UserHolder
        holder.registerUserByPhone("John Doe", "+7 (917) 971-11-11")
        holder.registerUserByPhone("John Doe", "+7 (917) 971-11-11")
    }

    @Test
    fun login_user_success() {
        val holder = UserHolder
        holder.registerUser("John Doe", "John_Doe@unknown.com","testPass")
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: john_doe@unknown.com
            fullName: John Doe
            initials: J D
            email: John_Doe@unknown.com
            phone: null
            meta: {auth=password}
        """.trimIndent()

        val successResult =  holder.loginUser("john_doe@unknown.com", "testPass")

        Assert.assertEquals(expectedInfo, successResult)
    }

    @Test
    fun login_user_by_phone_success() {
        val holder = UserHolder
        val user = holder.registerUserByPhone("John Doe", "+7 (917) 971-11-11")
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: +79179711111
            fullName: John Doe
            initials: J D
            email: null
            phone: +79179711111
            meta: {auth=sms}
        """.trimIndent()

        val successResult =  holder.loginUser("+7 (917) 971-11-11", user.accessCode!!)

        Assert.assertEquals(expectedInfo, successResult)
    }

    @Test
    fun login_user_fail() {
        val holder = UserHolder
        holder.registerUser("John Doe", "John_Doe@unknown.com","testPass")

        val failResult =  holder.loginUser("john_doe@unknown.com", "test")

        Assert.assertNull(failResult)
    }

    @Test
    fun login_user_not_found() {
        val holder = UserHolder
        holder.registerUser("John Doe", "John_Doe@unknown.com","testPass")

        val failResult =  holder.loginUser("john_cena@unknown.com", "test")

        Assert.assertNull(failResult)
    }

    @Test
    fun request_access_code() {
        val holder = UserHolder
        val user = holder.registerUserByPhone("John Doe", "+7 (917) 971-11-11")
        val oldAccess = user.accessCode
        holder.requestAccessCode("+7 (917) 971-11-11")

        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: +79179711111
            fullName: John Doe
            initials: J D
            email: null
            phone: +79179711111
            meta: {auth=sms}
        """.trimIndent()

        val successResult =  holder.loginUser("+7 (917) 971-11-11", user.accessCode!!)

        Assert.assertNotEquals(oldAccess, user.accessCode!!)
        Assert.assertEquals(expectedInfo, successResult)
    }

    @Test
    fun drop_last_until_list_of_int() {
        val source = listOf(1, 2, 3)
        val result = source.dropLastUntil { it == 2 }.toString()
        val expectedInfo = "[1]"
        Assert.assertEquals(expectedInfo, result)
    }

    @Test
    fun drop_last_until_list_of_empty() {
        val source = listOf(1, 2, 3)
        val result = source.dropLastUntil { it == 1 }.toString()
        val expectedInfo = "[]"
        Assert.assertEquals(expectedInfo, result)
    }

    @Test
    fun drop_last_until_full() {
        val source = listOf(1, 2, 3)
        val result = source.dropLastUntil { it == 4 }.toString()
        val expectedInfo = "[1, 2, 3]"
        Assert.assertEquals(expectedInfo, result)
    }

    @Test
    fun drop_last_until_list_of_string() {
        val source = "House Nymeros Martell of Sunspear".split(" ")
        val result = source.dropLastUntil { it == "of" }.toString()
        val expectedInfo = "[House, Nymeros, Martell]"
        Assert.assertEquals(expectedInfo, result)
    }

    @Test
    fun drop_last_until_list_of_string_2() {
        val source = "House Nymeros Martell of Sunspear".split(" ")
        val result = source.dropLastUntil { it == "Nymeros" }.toString()
        val expectedInfo = "[House]"
        Assert.assertEquals(expectedInfo, result)
    }

    @Test
    fun import_user_csv_phone() {
        val source = listOf(" John Doe ;;;+8(050)147-74-12;")
        val result = UserHolder.importUsers(source)[0].userInfo
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: +80501477412
            fullName: John Doe
            initials: J D
            email: null
            phone: +80501477412
            meta: {src=csv}
        """.trimIndent()
        Assert.assertEquals(expectedInfo, result)
    }

    @Test
    fun import_user_csv_email() {
        val source =
            listOf(" John Doe ;JohnDoe@unknow.com;[B@7591083d:c6adb4becdc64e92857e1e2a0fd6af84;")
        val result = UserHolder.importUsers(source)[0].userInfo
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: johndoe@unknow.com
            fullName: John Doe
            initials: J D
            email: JohnDoe@unknow.com
            phone: null
            meta: {src=csv}
        """.trimIndent()
        Assert.assertEquals(expectedInfo, result)
    }
}
