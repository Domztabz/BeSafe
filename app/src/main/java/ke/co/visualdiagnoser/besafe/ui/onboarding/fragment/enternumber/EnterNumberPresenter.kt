package ke.co.visualdiagnoser.besafe.ui.onboarding.fragment.enternumber


import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import ke.co.visualdiagnoser.besafe.Preference
import ke.co.visualdiagnoser.besafe.R
import ke.co.visualdiagnoser.besafe.TracerApp
import ke.co.visualdiagnoser.besafe.extensions.isInternetAvailable
import ke.co.visualdiagnoser.besafe.factory.NetworkFactory
import ke.co.visualdiagnoser.besafe.interactor.usecase.GetOnboardingOtp
import java.util.concurrent.TimeUnit


class EnterNumberPresenter(private val enterNumberFragment: EnterNumberFragment) : LifecycleObserver {

    private val TAG = this.javaClass.simpleName
    private lateinit var phoneNumber: String
    private lateinit var getOnboardingOtp: GetOnboardingOtp
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    init {
        enterNumberFragment.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        getOnboardingOtp = GetOnboardingOtp(NetworkFactory.awsClient, enterNumberFragment.lifecycle)
    }

    internal fun requestOTP(phoneNumber: String) {
        when {
            enterNumberFragment.activity?.isInternetAvailable() == false -> {
                enterNumberFragment.showCheckInternetError()
            }
            validateNumber(phoneNumber) -> {
                val cleansedNumber = if (phoneNumber.startsWith("0")) {
                    phoneNumber.takeLast(TracerApp.AppContext.resources.getInteger(R.integer.kenyan_phone_number_length))
                } else phoneNumber
                val fullNumber = "${enterNumberFragment.resources.getString(R.string.enter_number_prefix)}$cleansedNumber"
                Preference.putPhoneNumber(TracerApp.AppContext, fullNumber)
                Log.d(TAG, "Full Number $fullNumber")
                this.phoneNumber = cleansedNumber
                makeOTPCall(fullNumber, cleansedNumber)
            }
            else -> {
                enterNumberFragment.showInvalidPhoneNumber()
            }
        }
    }

    /**
     * @param phoneNumber cleansed phone number, 9 digits, doesn't start with 0
     */
    private fun makeOTPCall(phoneNumber: String, cleansedNumber: String) {
        enterNumberFragment.activity?.let {
            enterNumberFragment.disableContinueButton()
            enterNumberFragment.showLoading()
//            getOnboardingOtp.invoke(GetOtpParams(phoneNumber,
//                    Preference.getDeviceID(enterNumberFragment.requireContext()),
//                    Preference.getPostCode(enterNumberFragment.requireContext()),
//                    Preference.getAge(enterNumberFragment.requireContext()),
//                    Preference.getName(enterNumberFragment.requireContext())),
//                    onSuccess = {
//                        enterNumberFragment.navigateToOTPPage(
//                                it.session,
//                                it.challengeName,
//                                phoneNumber)
//                    },
//                    onFailure = {
//                        if (it is GetOnboardingOtpException.GetOtpInvalidNumberException) {
//                            enterNumberFragment.showInvalidPhoneNumber()
//                        } else {
//                            enterNumberFragment.showGenericError()
//                        }
//                        enterNumberFragment.hideLoading()
//                        enterNumberFragment.enableContinueButton()
//                    })

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

//                signInWithPhoneAuthCredential(credential)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            // This callback is invoked in an invalid request for verification is made,
                            // for instance if the the phone number format is not valid.
                            Log.w(TAG, "onVerificationFailed", e)
                            enterNumberFragment.showGenericError()

                            if (e is FirebaseAuthInvalidCredentialsException) {
                                // Invalid request
                                enterNumberFragment.showInvalidPhoneNumber()
                                // ...
                            } else if (e is FirebaseTooManyRequestsException) {
                                // The SMS quota for the project has been exceeded
                                // ...
                                enterNumberFragment.showGenericError()
                            } else if (e is FirebaseAuthException) {
                                enterNumberFragment.showGenericError()
                            }
                            enterNumberFragment.hideLoading()
                            enterNumberFragment.enableContinueButton()

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
                            enterNumberFragment.navigateToOTPPage(
                                    verificationId,
                                    token,

                                    null,
                                    null,
                                    phoneNumber,cleansedNumber)

                            // ...
                        }
                    }) // OnVerificationStateChangedCallbacks

//            enterNumberFragment.navigateToOTPPage(
//                                null,
//                                null,
//                                phoneNumber)
        }
    }

    internal fun validateNumber(phoneNumber: String?): Boolean {
        var australianPhoneNumberLength = enterNumberFragment.resources.getInteger(R.integer.kenyan_phone_number_length)
        if (phoneNumber?.startsWith("0") == true) {
            australianPhoneNumberLength++
        }
        return (phoneNumber?.length ?: 0) == australianPhoneNumberLength
    }

}