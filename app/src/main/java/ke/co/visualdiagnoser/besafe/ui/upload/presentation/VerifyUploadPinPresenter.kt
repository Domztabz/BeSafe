package ke.co.visualdiagnoser.besafe.ui.upload.presentation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ke.co.visualdiagnoser.besafe.BuildConfig
import ke.co.visualdiagnoser.besafe.Preference
import ke.co.visualdiagnoser.besafe.extensions.isInternetAvailable
import ke.co.visualdiagnoser.besafe.factory.NetworkFactory
import ke.co.visualdiagnoser.besafe.interactor.usecase.UploadData
import ke.co.visualdiagnoser.besafe.interactor.usecase.UploadDataException
import ke.co.visualdiagnoser.besafe.streetpass.persistence.StreetPassRecordStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class VerifyUploadPinPresenter(private val fragment: VerifyUploadPinFragment) : LifecycleObserver {

    private val TAG = this.javaClass.simpleName

    private var awsClient = NetworkFactory.awsClient
    private lateinit var uploadData: UploadData

    private lateinit var recordStorage: StreetPassRecordStorage

    init {
        fragment.lifecycle.addObserver(this)
        fragment.context?.let { context ->
            recordStorage = StreetPassRecordStorage(context)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        uploadData = UploadData( fragment.context, fragment.lifecycle)
    }

    internal fun uploadData(otp: String) {
        if (fragment.activity?.isInternetAvailable() == false) {
            fragment.showCheckInternetError()
        } else {
            fragment.disableContinueButton()
            fragment.showDialogLoading()
            uploadData.invoke(otp,
                    onSuccess = {
                        if (!BuildConfig.DEBUG) {
                            GlobalScope.launch { recordStorage.nukeDbAsync() }
                        }
                        fragment.context?.let { context ->
                            Preference.setDataIsUploaded(context, true)
                        }
                        fragment.navigateToNextPage()
                    },
                    onFailure = {
                        when (it) {
                            is UploadDataException.UploadDataIncorrectPinException -> {
                                fragment.showInvalidOtp()
                            }
                            is UploadDataException.UploadDataJwtExpiredException -> {
                                fragment.navigateToRegister()
                            }
                            else -> {
                                fragment.showGenericError()
                            }
                        }
                        fragment.enableContinueButton()
                        fragment.hideKeyboard()
                        fragment.hideLoading()
                    }
            )
        }
    }
}

