package ke.co.visualdiagnoser.besafe.ui.home.newHome

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    val bluettoothStatus = MutableLiveData<Int>()

    init {

    }

    fun setBluettoothStatus(status: Int) {
        bluettoothStatus.value = status
    }

    val shareAppDynamicLink = MutableLiveData<String>()


    fun setShareAppDynamicLink(link: String) {
        shareAppDynamicLink.value = link
    }


}