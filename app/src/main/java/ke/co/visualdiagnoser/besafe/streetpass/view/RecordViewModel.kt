package ke.co.visualdiagnoser.besafe.streetpass.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ke.co.visualdiagnoser.besafe.streetpass.persistence.StreetPassRecord
import ke.co.visualdiagnoser.besafe.streetpass.persistence.StreetPassRecordDatabase
import ke.co.visualdiagnoser.besafe.streetpass.persistence.StreetPassRecordRepository

class RecordViewModel(app: Application) : AndroidViewModel(app) {

    private var repo: StreetPassRecordRepository

    var allRecords: LiveData<List<StreetPassRecord>>

    init {
        val recordDao = StreetPassRecordDatabase.getDatabase(app).recordDao()
        repo = StreetPassRecordRepository(recordDao)
        allRecords = repo.allRecords
    }


}