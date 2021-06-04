package com.naed3r.powerschool.data

import android.widget.Toast
import com.naed3r.powerschool.data.model.LoggedInUser
import java.io.IOException
import khttp.get
import khttp.post
import org.jsoup.Jsoup
import android.util.Log
import java.lang.Exception
import kotlinx.coroutines.launch

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            var user = LoggedInUser(username, password, "N/A")
            val veriToken = kotlinx.coroutines.runBlocking {
                launch(kotlinx.coroutines.Dispatchers.IO) {
                    //val soup = get("https://homeaccess.beth.k12.pa.us/HomeAccess/Account/LogOn/index.html")
                    //Log.d("Tag", "$soup")
                    //val jsoup = Jsoup.parse(soup.text)
                    //val veriToken = jsoup.getElementsByTag("input").attr("name", "__RequestVerificationToken").attr("value")
                    //Log.d("Verification Token", veriToken)
                    //user = LoggedInUser(username, password, veriToken)
                    return@launch
                }
            }

            //Log.d("Post Corouting", user.toString())

            return Result.Success(user)

        } catch (e: Throwable) {
            Log.d("Tag", "$e")
            //return Result.Error(IOException("Error logging in", e))
            return Result.Error(Exception("failed to login: $e"))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}