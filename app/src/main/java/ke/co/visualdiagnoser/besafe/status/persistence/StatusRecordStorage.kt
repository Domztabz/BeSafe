package ke.co.visualdiagnoser.besafe.status.persistence

import android.content.Context
import ke.co.visualdiagnoser.besafe.streetpass.persistence.StreetPassRecordDatabase

class StatusRecordStorage(val context: Context) {

    private val statusDao = StreetPassRecordDatabase.getDatabase(context).statusDao()

    suspend fun saveRecord(record: StatusRecord) {
        statusDao.insert(record)
    }

    fun getAllRecords(): List<StatusRecord> {
        return statusDao.getCurrentRecords()
    }

    fun deleteDataOlderThan(timeInMs: Long): Int {
        return statusDao.deleteDataOlder(timeInMs)
    }

}