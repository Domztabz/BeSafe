package ke.co.visualdiagnoser.besafe.interactor.usecase

import androidx.lifecycle.Lifecycle
import ke.co.visualdiagnoser.besafe.interactor.Either
import ke.co.visualdiagnoser.besafe.interactor.Failure
import ke.co.visualdiagnoser.besafe.interactor.Success
import ke.co.visualdiagnoser.besafe.interactor.UseCase
import ke.co.visualdiagnoser.besafe.logging.CentralLog
import ke.co.visualdiagnoser.besafe.networking.response.UploadOTPResponse
import ke.co.visualdiagnoser.besafe.networking.service.AwsClient

class GetUploadOtp(private val awsClient: AwsClient, lifecycle: Lifecycle)
    : UseCase<UploadOTPResponse?, String>(lifecycle) {

    private val TAG = this.javaClass.simpleName

    override suspend fun run(params: String): Either<Exception, UploadOTPResponse?> {
        return try {
            val response = awsClient.requestUploadOtp("Bearer $params").execute()
            return if (response.code() == 200) {
                CentralLog.d(TAG, "onCodeUpload")
                Success(response.body())
            } else {
                Failure(GetUploadOtpException.GetUploadOtpServiceException(response.code()))
            }
        } catch (e: Exception) {
            Failure(e)
        }
    }
}

sealed class GetUploadOtpException : Exception() {
    class GetUploadOtpServiceException(val code: Int?) : GetUploadOtpException()
}