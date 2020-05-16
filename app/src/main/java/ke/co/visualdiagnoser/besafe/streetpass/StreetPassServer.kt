package ke.co.visualdiagnoser.besafe.streetpass

import android.content.Context
import ke.co.visualdiagnoser.besafe.bluetooth.gatt.GattServer
import ke.co.visualdiagnoser.besafe.bluetooth.gatt.GattService

class StreetPassServer constructor(val context: Context, serviceUUIDString: String) {

    private val TAG = "StreetPassServer"
    private var gattServer: GattServer? = null

    init {
        gattServer = setupGattServer(context, serviceUUIDString)
    }

    private fun setupGattServer(context: Context, serviceUUIDString: String): GattServer? {
        val gattServer = GattServer(context, serviceUUIDString)
        val started = gattServer.startServer()

        if (started) {
            val readService = GattService(context, serviceUUIDString)
            gattServer.addService(readService)
            return gattServer
        }
        return null
    }

    fun tearDown() {
        gattServer?.stop()
    }

}