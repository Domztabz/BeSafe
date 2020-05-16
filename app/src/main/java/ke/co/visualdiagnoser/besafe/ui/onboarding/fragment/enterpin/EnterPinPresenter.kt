package ke.co.visualdiagnoser.besafe.ui.onboarding.fragment.enterpin

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.visualdiagnoser.besafe.Preference
import ke.co.visualdiagnoser.besafe.extensions.isInternetAvailable
import ke.co.visualdiagnoser.besafe.factory.NetworkFactory
import ke.co.visualdiagnoser.besafe.interactor.usecase.GetOnboardingOtp
import java.util.concurrent.TimeUnit

class EnterPinPresenter(private val enterPinFragment: EnterPinFragment,
                        private val token : PhoneAuthProvider.ForceResendingToken?,
                        private var session: String?,
                        private var challengeName: String?,
                        private val phoneNumber: String?) : LifecycleObserver {

    private val TAG = this.javaClass.simpleName

    private var awsClient = NetworkFactory.awsClient
    private lateinit var getOtp: GetOnboardingOtp


    init {
        enterPinFragment.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        getOtp = GetOnboardingOtp(awsClient, enterPinFragment.lifecycle)
    }

    internal fun resendCode() {
        enterPinFragment.activity?.let {
            when {
                !it.isInternetAvailable() -> {
                    enterPinFragment.showCheckInternetError()
                }
                phoneNumber == null -> {
                    enterPinFragment.showGenericError()
                }
                else -> {
//                    getOtp.invoke(GetOtpParams(phoneNumber,
//                            Preference.getDeviceID(enterPinFragment.requireContext()),
//                            Preference.getPostCode(enterPinFragment.requireContext()),
//                            Preference.getAge(enterPinFragment.requireContext()),
//                            Preference.getName(enterPinFragment.requireContext())),
//                            onSuccess = {
//                                session = it.session
//                                challengeName = it.challengeName
//                                enterPinFragment.resetTimer()
//                            },
//                            onFailure = {
//                                enterPinFragment.showGenericError()
//                            })

                    //TODO Firebase
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber, // Phone number to verify
                            60, // Timeout duration
                            TimeUnit.SECONDS, // Unit of timeout
                            it, // Activity (for callback binding)
                            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    // This callback will be invoked in two situations:
                                    // 1 - Instant verification. In some cases the phone number can be instantly
                                    //     verified without needing to send or enter a verification code.
                                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                    //     detect the incoming verification SMS and perform verification without
                                    //     user action.
                                    Log.d(TAG, "onVerificationCompleted:$credential")
//                                    enterPinFragment.resetTimer()


//                signInWithPhoneAuthCredential(credential)
                                }

                                override fun onVerificationFailed(e: FirebaseException) {
                                    // This callback is invoked in an invalid request for verification is made,
                                    // for instance if the the phone number format is not valid.
                                    Log.w(TAG, "onVerificationFailed", e)

                                    if (e is FirebaseAuthInvalidCredentialsException) {
                                        // Invalid request
                                        enterPinFragment.showGenericError()

                                        // ...
                                    } else if (e is FirebaseTooManyRequestsException) {
                                        // The SMS quota for the project has been exceeded
                                        // ...
                                        //TODO Show Error
                                    }

                                    // Show a message and update the UI
                                    // ...
                                }

                                override fun onCodeSent(
                                        verificationId: String,
                                        token: PhoneAuthProvider.ForceResendingToken
                                ) {
                                    // The SMS verification code has been sent to the provided phone number, we
                                    // now need to ask the user to enter the code and then construct a credential
                                    // by combining the code with a verification ID.
                                    Log.d(TAG, "onCodeSent:$verificationId")

                                    // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token
                                    enterPinFragment.resetTimer()


                                    // ...
                                }
                            }) // OnVerificationStateChangedCallbacks, // OnVerificationStateChangedCallbacks
                }
            }
        }
    }


    internal fun validateOTP(verificationId : String, otp: String) {
        if (TextUtils.isEmpty(otp) || otp.length != 6) {
            enterPinFragment.showErrorOtpMustBeSixDigits()
            return
        }
        if (enterPinFragment.activity?.isInternetAvailable() == false) {
            enterPinFragment.showCheckInternetError()
            return
        }
        enterPinFragment.disableContinueButton()
        enterPinFragment.showLoading()
//        val authChallengeCall: Call<AuthChallengeResponse> = awsClient.respondToAuthChallenge(AuthChallengeRequest(session, otp))
//        authChallengeCall.enqueue(object : Callback<AuthChallengeResponse> {
//            override fun onResponse(call: Call<AuthChallengeResponse>, response: Response<AuthChallengeResponse>) {
//                if (response.code() == 200) {
//                    CentralLog.d(TAG, "code received")
//
//                    val authChallengeResponse = response.body()
//
//                    val handShakePin = authChallengeResponse?.pin
//                    handShakePin?.let {
//                        Preference.putHandShakePin(enterPinFragment.context, handShakePin)
//                    }
//                    val jwtToken = authChallengeResponse?.token
//                    jwtToken.let {
//                        Preference.putEncrypterJWTToken(enterPinFragment.requireContext(), jwtToken)
//                    }
//                    enterPinFragment.hideKeyboard()
//                    enterPinFragment.navigateToNextPage()
//                } else {
//                    onError()
//                }
//            }
//
//            override fun onFailure(call: Call<AuthChallengeResponse>, t: Throwable) {
//                onError()
//            }
//        })

        verifyPhoneNumberWithCode(verificationId, otp)

    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        enterPinFragment?.activity?.let {

            // [START verify_with_code]
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            // [END verify_with_code]

            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(it) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    val handShakePin = user?.uid
                    handShakePin?.let {
                        Preference.putHandShakePin(enterPinFragment.context, handShakePin)
                    }
                            createProfile()

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                onError()
                            }
                        }
                    }
        }
    }

    fun createProfile() {
        val db = FirebaseFirestore.getInstance();
        val auth = FirebaseAuth.getInstance()
        val userUID = auth.uid

        val documentPath = "users/$userUID"

//        val email = auth.currentUser?.email
        val name = Preference.getName(enterPinFragment?.requireActivity())
        val displayName = auth.currentUser?.displayName
//        val photoURL = auth.currentUser?.photoUrl
//        val phoneNumber = auth.currentUser?.phoneNumber
        val phoneNumber = auth.currentUser?.phoneNumber
        val county = Preference.getCounty(enterPinFragment.requireContext())
        val age = Preference.getAge(enterPinFragment.requireContext())


//        val name =

        val user = HashMap<String, Any>()

        name?.let {
            user.put("name", name)
        }
        age?.let {
            user.put("age", age)
        }

        county?.let {
            user.put("county", county.toString())
        }

        phoneNumber?.let {
            user.put("phoneNumber", phoneNumber)
        }

        db.document(documentPath).set(user).addOnCompleteListener {

            if (it.isSuccessful) {
                Log.d(TAG, "Task isSuccessful")

                enterPinFragment.hideKeyboard()
                enterPinFragment.navigateToNextPage()

//                it.finish()
//                startActivity(Intent(activity, SelectStoreFragment::class.java))
                }
            else {
                Log.d(TAG, "Task is Unsuccessful")

            }

        }
    }

    

    private fun onError() {
        enterPinFragment.enableContinueButton()
        enterPinFragment.hideLoading()
        enterPinFragment.hideKeyboard()
        enterPinFragment.showInvalidOtp()
    }
}