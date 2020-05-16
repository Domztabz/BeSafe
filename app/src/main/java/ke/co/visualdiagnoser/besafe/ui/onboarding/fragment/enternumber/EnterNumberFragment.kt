package ke.co.visualdiagnoser.besafe.ui.onboarding.fragment.enternumber

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.NavigationRes
import androidx.core.os.bundleOf
import com.google.firebase.auth.PhoneAuthProvider
import ke.co.visualdiagnoser.besafe.R
import ke.co.visualdiagnoser.besafe.TracerApp
import ke.co.visualdiagnoser.besafe.ui.PagerChildFragment
import ke.co.visualdiagnoser.besafe.ui.UploadButtonLayout
import ke.co.visualdiagnoser.besafe.ui.onboarding.fragment.enterpin.EnterPinFragment.Companion.ENTER_CLEANSED_PIN_PHONE_NUMBER
import ke.co.visualdiagnoser.besafe.ui.onboarding.fragment.enterpin.EnterPinFragment.Companion.ENTER_PIN_CHALLENGE_NAME
import ke.co.visualdiagnoser.besafe.ui.onboarding.fragment.enterpin.EnterPinFragment.Companion.ENTER_PIN_DESTINATION_ID
import ke.co.visualdiagnoser.besafe.ui.onboarding.fragment.enterpin.EnterPinFragment.Companion.ENTER_PIN_PHONE_NUMBER
import ke.co.visualdiagnoser.besafe.ui.onboarding.fragment.enterpin.EnterPinFragment.Companion.ENTER_PIN_PROGRESS
import ke.co.visualdiagnoser.besafe.ui.onboarding.fragment.enterpin.EnterPinFragment.Companion.ENTER_PIN_SESSION
import kotlinx.android.synthetic.main.fragment_enter_number.*

class EnterNumberFragment : PagerChildFragment() {

    companion object {
        const val ENTER_NUMBER_DESTINATION_ID = "destination_id"
        const val ENTER_NUMBER_PROGRESS = "progress"
        const val ENTER_TOKEN = "token"
        const val TAG = "EnterNumberFragment"
        const val ENTER_VERIFICATION_ID = "verification_id"

    }

    override val navigationIcon: Int? = R.drawable.ic_up
    override var stepProgress: Int? = 2

    private val enterNumberPresenter = EnterNumberPresenter(this)
    private var alertDialog: AlertDialog? = null
    @NavigationRes
    private var destinationId: Int? = null

    private val phoneNumberTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // change LengthFilter if user making a mistake of entering phone number starting with 0
            val phoneNumberLength = TracerApp.AppContext.resources.getInteger(R.integer.kenyan_phone_number_length)
//            val filters = enter_number_phone_number.filters
//            val newFilterLength = if (s?.toString()?.startsWith("0") == true) {
//                phoneNumberLength + 1
//            } else {
//                phoneNumberLength
//            }
//            enter_number_phone_number.filters = filters.filterNot { it is InputFilter.LengthFilter }.toTypedArray() +
//                    InputFilter.LengthFilter(newFilterLength)

            updateButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_enter_number, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        view.use_oz_phone_number.movementMethod = LinkMovementMethod.getInstance()
        arguments?.let {
            destinationId = it.getInt(ENTER_NUMBER_DESTINATION_ID)
            stepProgress = if (it.containsKey(ENTER_NUMBER_PROGRESS)) it.getInt(ENTER_PIN_PROGRESS) else null
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onResume() {
        super.onResume()
        enter_number_phone_number.selectAll()
        enter_number_phone_number.addTextChangedListener(phoneNumberTextWatcher)
        updateButtonState()
    }

    override fun onPause() {
        super.onPause()
        enter_number_phone_number.removeTextChangedListener(phoneNumberTextWatcher)
    }

    fun showInvalidPhoneNumber() {
        Log.d(TAG, "showInvalidPhoneNumber called")

        invalid_phone_number.visibility = VISIBLE
        enter_number_phone_number.background = context?.getDrawable(R.drawable.phone_number_invalid_background)
    }

    override fun updateButtonState() {
        if (enterNumberPresenter.validateNumber(enter_number_phone_number?.text?.toString())) {
            enableContinueButton()
        } else {
            disableContinueButton()
        }
    }

    fun showGenericError() {
        Log.d(TAG, "showGenericError called")
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(activity)
                .setMessage(R.string.generic_error)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, null).show()
    }

    fun navigateToOTPPage(
            code: String,
            token: PhoneAuthProvider.ForceResendingToken,
            session: String?,
            challengeName: String?,
            phoneNumber: String,
            censoredPN: String) {
        val bundle = bundleOf(
                ENTER_TOKEN to token,
                ENTER_VERIFICATION_ID to code,
                ENTER_PIN_SESSION to session,
                ENTER_PIN_CHALLENGE_NAME to challengeName,
                ENTER_PIN_PHONE_NUMBER to phoneNumber,
                ENTER_CLEANSED_PIN_PHONE_NUMBER to censoredPN,
                ENTER_PIN_DESTINATION_ID to destinationId).also { bundle ->
            stepProgress?.let {
                bundle.putInt(ENTER_PIN_PROGRESS, it + 1)
            }
        }
        navigateTo(R.id.action_enterNumberFragment_to_otpFragment, bundle)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        alertDialog?.dismiss()
        root.removeAllViews()
    }

    override fun getUploadButtonLayout() = UploadButtonLayout.ContinueLayout(R.string.enter_number_button) {
//        enterNumberPresenter.requestOTP(enter_number_phone_number.text.toString().trim())

        val bundle = bundleOf(
                ENTER_CLEANSED_PIN_PHONE_NUMBER to "796026997")
        navigateTo(R.id.action_enterNumberFragment_to_otpFragment,  bundle)

    }

    fun showCheckInternetError() {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(activity)
                .setMessage(R.string.generic_internet_error)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, null).show()
    }

}