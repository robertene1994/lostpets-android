package com.robert.android.lostpets.domain.model

import java.io.Serializable

data class AccountCredentials(val email: String, val password: String) : Serializable
