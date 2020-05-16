package ke.co.visualdiagnoser.besafe.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ke.co.visualdiagnoser.besafe.Utils
import ke.co.visualdiagnoser.besafe.logging.CentralLog

class StartOnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            CentralLog.d("StartOnBootReceiver", "boot completed received")

            try {
                CentralLog.d("StartOnBootReceiver", "Attempting to start service")
                Utils.scheduleStartMonitoringService(context, 500)
            } catch (e: Throwable) {
                CentralLog.e("StartOnBootReceiver", e.localizedMessage)
                e.printStackTrace()
            }

        }
    }
}
