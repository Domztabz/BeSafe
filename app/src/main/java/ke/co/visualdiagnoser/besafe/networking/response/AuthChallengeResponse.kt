package ke.co.visualdiagnoser.besafe.networking.response

import androidx.annotation.Keep

@Keep
data class AuthChallengeResponse(val token: String, val uuid: String, val token_expiry: String, val pin: String)