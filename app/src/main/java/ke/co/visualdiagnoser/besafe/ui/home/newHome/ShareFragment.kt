package ke.co.visualdiagnoser.besafe.ui.home.newHome

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ke.co.visualdiagnoser.besafe.Preference

import ke.co.visualdiagnoser.besafe.R
import ke.co.visualdiagnoser.besafe.extensions.*
import kotlinx.android.synthetic.main.fragment_share.*

/**
 * A simple [Fragment] subclass.
 */
class ShareFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    val TAG : String = "ShareFragment"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_share, container, false)
        homeViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.bluettoothStatus.observe(requireActivity(), Observer {
            when (it) {
                BluetoothAdapter.STATE_OFF -> {
                    Log.d("ShareFragment", "BluetoothAdapter.STATE_OFF")

                    bluetooth_card_view.render(formatBlueToothTitle(false), false)
                    refreshSetupCompleteOrIncompleteUi()
                }
                BluetoothAdapter.STATE_TURNING_OFF -> {
                    Log.d("ShareFragment", "BluetoothAdapter.STATE_TURNING_OFF")

                    bluetooth_card_view.render(formatBlueToothTitle(false), false)
                    refreshSetupCompleteOrIncompleteUi()
                }
                BluetoothAdapter.STATE_ON -> {
                    Log.d("ShareFragment", "BluetoothAdapter.STATE_ON")

                    bluetooth_card_view.render(formatBlueToothTitle(true), true)
                    refreshSetupCompleteOrIncompleteUi()
                }
            }
        })

        updateBlueToothStatus()
        updatePushNotificationStatus()
        updateBatteryOptimizationStatus()
        updateLocationStatus()
    }


    private fun allPermissionsEnabled(): Boolean {
        val bluetoothEnabled = isBlueToothEnabled() ?: true
        val pushNotificationEnabled = isPushNotificationEnabled() ?: true
        val nonBatteryOptimizationAllowed = isNonBatteryOptimizationAllowed() ?: true
        val locationStatusAllowed = isFineLocationEnabled() ?: true

        return bluetoothEnabled &&
                pushNotificationEnabled &&
                nonBatteryOptimizationAllowed &&
                locationStatusAllowed
    }

    private fun refreshSetupCompleteOrIncompleteUi() {
        val isUploaded = context?.let {
            Preference.isDataUploaded(it)
        } ?: run {
            false
        }
//        home_been_tested_button.visibility = if (isUploaded) View.GONE else View.VISIBLE
        when {
            !allPermissionsEnabled() -> {
                Log.d("ShareFragment", "!allPermissionsEnabled")
                home_header_setup_complete_header_active.visibility = View.GONE
                home_setup_incomplete_permissions_group.visibility = View.VISIBLE
//                home_header_setup_complete_header_uploaded.visibility = View.GONE
//                home_header_setup_complete_header_divider.visibility = View.GONE
                home_header_setup_complete_header.setText(R.string.home_header_inactive_title)
//                home_header_picture_setup_complete.setImageResource(R.drawable.ic_logo_home_inactive)
//                home_header_help.setImageResource(R.drawable.ic_help_outline_black)
                context?.let { context ->
                    val backGroundColor = ContextCompat.getColor(context, R.color.grey)
//                    header_background.setBackgroundColor(backGroundColor)
//                    header_background_overlap.setBackgroundColor(backGroundColor)

                    val textColor = ContextCompat.getColor(context, R.color.slack_black)
//                    home_header_setup_complete_header_uploaded.setTextColor(textColor)
                    home_header_setup_complete_header.setTextColor(textColor)
                }
//                content_setup_incomplete_group.visibility = View.VISIBLE
                updateBlueToothStatus()
                updatePushNotificationStatus()
                updateBatteryOptimizationStatus()
                updateLocationStatus()
            }
            isUploaded -> {
                Log.d("ShareFragment", "isUploaded")

//                home_header_setup_complete_header_uploaded.visibility = View.VISIBLE
//                home_header_setup_complete_header_divider.visibility = View.VISIBLE
                home_header_setup_complete_header.setText(R.string.home_header_active_title)
//                home_header_picture_setup_complete.setImageResource(R.drawable.ic_logo_home_uploaded)
//                home_header_picture_setup_complete.setAnimation("spinner_home_upload_complete.json")
//                home_header_help.setImageResource(R.drawable.ic_help_outline_white)
//                content_setup_incomplete_group.visibility = View.GONE
                context?.let { context ->
                    val backGroundColor = ContextCompat.getColor(context, R.color.dark_green)
//                    header_background.setBackgroundColor(backGroundColor)
//                    header_background_overlap.setBackgroundColor(backGroundColor)

                    val textColor = ContextCompat.getColor(context, R.color.white)
//                    home_header_setup_complete_header_uploaded.setTextColor(textColor)
                    home_header_setup_complete_header.setTextColor(textColor)
                }
            }

            else -> {
                Log.d("ShareFragment", "active")

                home_header_setup_complete_header_active.visibility = View.VISIBLE
                home_setup_incomplete_permissions_group.visibility = View.GONE
//                home_header_setup_complete_header_uploaded.visibility = View.GONE
//                home_header_setup_complete_header_divider.visibility = View.GONE
                home_header_setup_complete_header.setText(R.string.home_header_active_title)
//                home_header_help.setImageResource(R.drawable.ic_help_outline_black)
//                home_header_picture_setup_complete.setAnimation("spinner_home.json")

//                        val animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.rotate_circle)
//                logo.startAnimation(animation)
//                content_setup_incomplete_group.visibility = View.GONE
                context?.let { context ->
                    val backGroundColor = ContextCompat.getColor(context, R.color.lighter_green)
//                    header_background.setBackgroundColor(backGroundColor)
//                    header_background_overlap.setBackgroundColor(backGroundColor)

                    val textColor = ContextCompat.getColor(context, R.color.slack_black)
//                    home_header_setup_complete_header_uploaded.setTextColor(textColor)
                    home_header_setup_complete_header.setTextColor(textColor)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        bluetooth_card_view.setOnClickListener { requestBlueToothPermissionThenNextPermission() }
        location_card_view.setOnClickListener { askForLocationPermission() }
        battery_card_view.setOnClickListener { excludeFromBatteryOptimization() }

        refreshSetupCompleteOrIncompleteUi()

    }

    override fun onPause() {
        super.onPause()
        bluetooth_card_view.setOnClickListener(null)
        location_card_view.setOnClickListener(null)
        battery_card_view.setOnClickListener(null)
//        home_been_tested_button.setOnClickListener(null)
//        home_setup_complete_share.setOnClickListener(null)
//        home_setup_complete_news.setOnClickListener(null)
//        home_setup_complete_app.setOnClickListener(null)

    }

    private fun updateBlueToothStatus() {
        isBlueToothEnabled()?.let {
            bluetooth_card_view.visibility = View.VISIBLE
            bluetooth_card_view.render(formatBlueToothTitle(it), it)
        } ?: run {
            bluetooth_card_view.visibility = View.GONE
        }
    }

    private fun updatePushNotificationStatus() {
        isPushNotificationEnabled()?.let {
            push_card_view.visibility = View.VISIBLE
            push_card_view.render(formatPushNotificationTitle(it), it)
        } ?: run {
            push_card_view.visibility = View.GONE
        }
    }

    private fun updateBatteryOptimizationStatus() {
        isNonBatteryOptimizationAllowed()?.let {
            battery_card_view.visibility = View.VISIBLE
//            battery_card_view.render(formatNonBatteryOptimizationTitle(!it), it)
            battery_card_view.render(formatNonBatteryOptimizationTitle(it), it)
        } ?: run {
            battery_card_view.visibility = View.GONE
        }
    }

    private fun updateLocationStatus() {
        isFineLocationEnabled()?.let {
            location_card_view.visibility = View.VISIBLE
            location_card_view.render(formatLocationTitle(it), it)
        } ?: run {
            location_card_view.visibility = View.VISIBLE
        }
    }


    private fun formatBlueToothTitle(on: Boolean): String {
        return resources.getString(R.string.home_bluetooth_permission, getPermissionEnabledTitle(on))
    }

    private fun formatLocationTitle(on: Boolean): String {
        return resources.getString(R.string.home_location_permission, getPermissionEnabledTitle(on))
    }

    private fun formatNonBatteryOptimizationTitle(on: Boolean): String {
        return resources.getString(R.string.home_non_battery_optimization_permission, getPermissionEnabledTitle(on))
    }

    private fun formatPushNotificationTitle(on: Boolean): String {
        return resources.getString(R.string.home_push_notification_permission, getPermissionEnabledTitle(on))
    }

    private fun getPermissionEnabledTitle(on: Boolean): String {
        return resources.getString(if (on) R.string.home_permission_on else R.string.home_permission_off)
    }

}
