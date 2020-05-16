package ke.co.visualdiagnoser.besafe.interactor.usecase

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.visualdiagnoser.besafe.TracerApp
import ke.co.visualdiagnoser.besafe.interactor.Either
import ke.co.visualdiagnoser.besafe.interactor.Failure
import ke.co.visualdiagnoser.besafe.interactor.Success
import ke.co.visualdiagnoser.besafe.interactor.UseCase
import ke.co.visualdiagnoser.besafe.logging.CentralLog
import ke.co.visualdiagnoser.besafe.streetpass.persistence.StreetPassRecordStorage
import ke.co.visualdiagnoser.besafe.ui.upload.model.ExportData

class UploadData(
                 private val context: Context?,
                 lifecycle: Lifecycle)
    : UseCase<UseCase.None, String>(lifecycle) {

    private val TAG = this.javaClass.simpleName

    override suspend fun run(params: String): Either<Exception, None> {
//        val jwtToken = Preference.getEncrypterJWTToken(context)

        val exportedData = ExportData(StreetPassRecordStorage(TracerApp.AppContext).getAllRecords())
        CentralLog.d(TAG, "records: ${exportedData.records}")

        val uid = FirebaseAuth.getInstance().uid
        uid?.let {
            val db = FirebaseFirestore.getInstance().collection("contacts")
            val collection = db.document(uid).collection("uploads")

            exportedData.records.forEach {
                collection
                        .add(it)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
            }

            return Success(None)

        } ?: run {
                        return Failure(Exception())
        }

//        jwtToken?.let { jwtToken ->
//            try {
//                val initialUploadResponse = retryRetrofitCall {
//                    awsClient.initiateUpload("Bearer $jwtToken", params).execute()
//                }
//                if (initialUploadResponse == null) {
//                    Failure(UploadDataException.UploadDataIncorrectPinException)
//                } else if (initialUploadResponse.isSuccessful) {
//                    val uploadLink = initialUploadResponse.body()?.uploadLink
//                    if (uploadLink.isNullOrEmpty()) {
//                        Failure(Exception())
//                    } else {
//                        zipAndUploadData(uploadLink)
//                    }
//                } else if (initialUploadResponse.code() == 400) {
//                    Failure(UploadDataException.UploadDataIncorrectPinException)
//                } else if (initialUploadResponse.code() == 403) {
//                    Failure(UploadDataException.UploadDataJwtExpiredException)
//                } else {
//                    Failure(Exception())
//                }
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        } ?: run {
//            return Failure(Exception())
//        }
    }

    private suspend fun fireStoreUpload() {
        val exportedData = ExportData(StreetPassRecordStorage(TracerApp.AppContext).getAllRecords())
        CentralLog.d(TAG, "records: ${exportedData.records}")

        val uid = FirebaseAuth.getInstance().uid
        uid?.let {
            val db = FirebaseFirestore.getInstance().collection("contacts")
            val collection = db.document(uid).collection("uploads")

            exportedData.records.forEach {
                collection
                        .add(it)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
            }
        }
    }

//    private suspend fun zipAndUploadData(uploadLink: String): Either<Exception, None> {
//        val exportedData = ExportData(StreetPassRecordStorage(TracerApp.AppContext).getAllRecords())
//        CentralLog.d(TAG, "records: ${exportedData.records}")
//
//        val jsonData = Gson().toJson(exportedData)
//
//        val request = Request.Builder()
//                .url(uploadLink)
//                .put(jsonData.toRequestBody(null))
//                .build()
//        return try {
//            val response = retryOkhttpCall { okHttpClient.newCall(request).execute() }
//            return if (response == null) {
//                Failure(Exception())
//            } else {
//                Success(None)
//            }
//        } catch (e: Exception) {
//            Failure(Exception())
//        }
//    }

}

sealed class UploadDataException : Exception() {
    object UploadDataIncorrectPinException : UploadDataException()
    object UploadDataJwtExpiredException : UploadDataException()
}
