package ke.co.visualdiagnoser.besafe.networking.request

import androidx.annotation.Keep

@Keep
data class AuthChallengeRequest(val session: String?, val code: String?)