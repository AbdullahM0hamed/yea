package com.naed3r.powerschool.data.model

import android.os.Parcelable
import kotlinx.serialization.Serializable

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */

data class LoggedInUser(
        val username: String,
        val password: String,
        val LoginToken: String
)