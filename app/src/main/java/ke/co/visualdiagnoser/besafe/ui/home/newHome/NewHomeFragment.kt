package ke.co.visualdiagnoser.besafe.ui.home.newHome

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelcan.inkpageindicator.InkPageIndicator
import ke.co.visualdiagnoser.besafe.BuildConfig
import ke.co.visualdiagnoser.besafe.Preference
import ke.co.visualdiagnoser.besafe.R
import ke.co.visualdiagnoser.besafe.WebViewActivity
import ke.co.visualdiagnoser.besafe.extensions.*
import ke.co.visualdiagnoser.besafe.ui.BaseFragment
import ke.co.visualdiagnoser.besafe.ui.home.HomeFragmentDirections
import ke.co.visualdiagnoser.besafe.ui.home.HomePresenter
import ke.co.visualdiagnoser.besafe.ui.upload.UploadActivity

import kotlinx.android.synthetic.main.fragment_new_home.*
import kotlinx.android.synthetic.main.fragment_new_home.view.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class NewHomeFragment : BaseFragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var presenter: HomePresenter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mIndicator : InkPageIndicator
    val TAG = "NewHomeFragment"
    private var mIsBroadcastListenerRegistered = false

    private var counter: Int = 0

    private val mBroadcastListener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                    BluetoothAdapter.STATE_OFF -> {
                        homeViewModel.setBluettoothStatus( BluetoothAdapter.STATE_OFF)
//                        bluetooth_card_view.render(formatBlueToothTitle(false), false)
//                        refreshSetupCompleteOrIncompleteUi()
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        homeViewModel.setBluettoothStatus( BluetoothAdapter.STATE_TURNING_OFF)
//                        bluetooth_card_view.render(formatBlueToothTitle(false), false)
//                        refreshSetupCompleteOrIncompleteUi()
                    }
                    BluetoothAdapter.STATE_ON -> {
                        homeViewModel.setBluettoothStatus( BluetoothAdapter.STATE_ON)
//                        bluetooth_card_view.render(formatBlueToothTitle(true), true)
//                        refreshSetupCompleteOrIncompleteUi()
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_home, container, false)
        presenter = HomePresenter(this)
        homeViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)

        mIndicator = view.findViewById<InkPageIndicator>(R.id.indicator)
        val viewPager = view.findViewById<ViewPager>(R.id.pager)
        val mAdapter = SliderItemFragmentAdapter(activity?.supportFragmentManager)

        viewPager?.setAdapter(mAdapter)

//        val mIndicator = findViewById<InkPageIndicator>(R.id.indicator)
        mIndicator.setViewPager(viewPager)

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.d(TAG, "Position: $position")
            }

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.home_header_help.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToHelpFragment())
        }
        if (BuildConfig.ENABLE_DEBUG_SCREEN) {
            view.header_background.setOnClickListener {
                counter++
                if (counter >= 2) {
                    counter = 0
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPeekActivity())
                }
            }
        }
        home_version_number.text = getString(R.string.home_version_number, BuildConfig.VERSION_NAME)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)




//        shareContent()

    }

    override fun onResume() {
        super.onResume()



//        bluetooth_card_view.setOnClickListener { requestBlueToothPermissionThenNextPermission() }
//        location_card_view.setOnClickListener { askForLocationPermission() }
//        battery_card_view.setOnClickListener { excludeFromBatteryOptimization() }
        home_been_tested_button.setOnClickListener {
            startActivity(Intent(requireActivity(), UploadActivity::class.java))
//            navigateTo(R.id.action_newHomeFragment_to_uploadMasterFragment)
        }
        home_setup_complete_share.setOnClickListener {
            shareThisApp()
        }
//        home_setup_complete_news.setOnClickListener {
//            goToNewsWebsite()
//        }
//        home_setup_complete_app.setOnClickListener {
//            goToCovidApp()
//        }

        if (!mIsBroadcastListenerRegistered) {
            registerBroadcast()
        }
        refreshSetupCompleteOrIncompleteUi()
    }

    override fun onPause() {
        super.onPause()
//        bluetooth_card_view.setOnClickListener(null)
//        location_card_view.setOnClickListener(null)
//        battery_card_view.setOnClickListener(null)
        home_been_tested_button.setOnClickListener(null)
        home_setup_complete_share.setOnClickListener(null)
//        home_setup_complete_news.setOnClickListener(null)
//        home_setup_complete_app.setOnClickListener(null)
        activity?.let { activity ->
            if (mIsBroadcastListenerRegistered) {
                activity.unregisterReceiver(mBroadcastListener)
                mIsBroadcastListenerRegistered = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        home_root?.removeAllViews()
    }

    private fun refreshSetupCompleteOrIncompleteUi() {
        val isUploaded = context?.let {
            Preference.isDataUploaded(it)
        } ?: run {
            false
        }
        home_been_tested_button.visibility = if (isUploaded) GONE else VISIBLE
        when {
            !allPermissionsEnabled() -> {
//                home_header_setup_complete_header_uploaded.visibility = GONE
//                home_header_setup_complete_header_divider.visibility = GONE
//                home_header_setup_complete_header.setText(R.string.home_header_inactive_title)
//                home_header_picture_setup_complete.setImageResource(R.drawable.ic_logo_home_inactive)
                home_header_help.setImageResource(R.drawable.ic_help_outline_black)
                logo.setImageResource(R.drawable.vdd_black_logo)

                context?.let { context ->
                    val backGroundColor = ContextCompat.getColor(context, R.color.grey)
                    header_background.setBackgroundColor(backGroundColor)
//                    header_background_overlap.setBackgroundColor(backGroundColor)

                    val textColor = ContextCompat.getColor(context, R.color.slack_black)
//                    home_header_setup_complete_header_uploaded.setTextColor(textColor)
//                    home_header_setup_complete_header.setTextColor(textColor)
                }
//                content_setup_incomplete_group.visibility = VISIBLE
//                updateBlueToothStatus()
//                updatePushNotificationStatus()
//                updateBatteryOptimizationStatus()
//                updateLocationStatus()
            }
            isUploaded -> {
//                home_header_setup_complete_header_uploaded.visibility = VISIBLE
//                home_header_setup_complete_header_divider.visibility = VISIBLE
//                home_header_setup_complete_header.setText(R.string.home_header_active_title)
//                home_header_picture_setup_complete.setImageResource(R.drawable.ic_logo_home_uploaded)
//                home_header_picture_setup_complete.setAnimation("spinner_home_upload_complete.json")
                home_header_help.setImageResource(R.drawable.ic_help_outline_white)
//                content_setup_incomplete_group.visibility = GONE
                context?.let { context ->
                    val backGroundColor = ContextCompat.getColor(context, R.color.dark_green)
                    header_background.setBackgroundColor(backGroundColor)
//                    header_background_overlap.setBackgroundColor(backGroundColor)

                    val textColor = ContextCompat.getColor(context, R.color.white)
//                    home_header_setup_complete_header_uploaded.setTextColor(textColor)
//                    home_header_setup_complete_header.setTextColor(textColor)
                }
            }

            else -> {
//                home_header_setup_complete_header_uploaded.visibility = GONE
//                home_header_setup_complete_header_divider.visibility = GONE
//                home_header_setup_complete_header.setText(R.string.home_header_active_title)
                home_header_help.setImageResource(R.drawable.ic_help_outline_black)
                logo.setImageResource(R.drawable.vdd_blue)

//                home_header_picture_setup_complete.setAnimation("spinner_home.json")
//                content_setup_incomplete_group.visibility = GONE
                context?.let { context ->
                    val backGroundColor = ContextCompat.getColor(context, R.color.white)
                    header_background.setBackgroundColor(backGroundColor)
//                    header_background_overlap.setBackgroundColor(backGroundColor)

                    val textColor = ContextCompat.getColor(context, R.color.slack_black)
//                    home_header_setup_complete_header_uploaded.setTextColor(textColor)
//                    home_header_setup_complete_header.setTextColor(textColor)
                }
            }
        }
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

    private fun registerBroadcast() {
        activity?.let { activity ->
            var f = IntentFilter()
            activity.registerReceiver(mBroadcastListener, f)
            // bluetooth on/off
            f = IntentFilter()
            f.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            activity.registerReceiver(mBroadcastListener, f)
            mIsBroadcastListenerRegistered = true
        }
    }

    var shareLink : String? = null

    fun shareContent() {

        val link = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"

        Log.d(TAG, "shareContent method called")

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://immicart.page.link")
                // Set parameters
                // ...
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnSuccessListener { result ->
                    // Short link created
                    val shortLink = result.shortLink
                    homeViewModel.setShareAppDynamicLink(shortLink.toString())
                    Log.d(TAG, "shortLink : $shortLink")
                    shareLink = shortLink.toString()
//                shareDeepLink(shortLink.toString())
                    val flowchartLink = result.previewLink
                }.addOnFailureListener {
                    // Error
                    // ...
                }

    }

//    private fun shareThisApp() {
//        val newIntent = Intent(Intent.ACTION_SEND)
//        newIntent.type = "text/plain"
//        newIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_this_app_content))
//        newIntent.putExtra(Intent.EXTRA_HTML_TEXT, getString(R.string.share_this_app_content_html))
//        startActivity(Intent.createChooser(newIntent, null))
//    }

    private fun shareThisApp() {

        val link = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_this_app_title))
//                var shareMessage = "\n Get the things you love from the stores you trust. Try Immicart now\n\n"
//                shareMessage  = shareMessage + it
//                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_this_app_content) +"  $link")
        startActivity(Intent.createChooser(shareIntent, "Share BeSafe app with: "))

//        homeViewModel.shareAppDynamicLink.observe(this, Observer {
//            try {
//                val shareIntent = Intent(Intent.ACTION_SEND)
//                shareIntent.type = "text/plain"
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_this_app_title))
////                var shareMessage = "\n Get the things you love from the stores you trust. Try Immicart now\n\n"
////                shareMessage  = shareMessage + it
////                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
//                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_this_app_content) +"  $it")
//                startActivity(Intent.createChooser(shareIntent, "Share BeSafe app with: "))
//            } catch (e: Exception) {
//                //e.toString();
//            }
//        })
    }

//    private fun updateBlueToothStatus() {
//        isBlueToothEnabled()?.let {
//            bluetooth_card_view.visibility = VISIBLE
//            bluetooth_card_view.render(formatBlueToothTitle(it), it)
//        } ?: run {
//            bluetooth_card_view.visibility = GONE
//        }
//    }




    private fun formatBlueToothTitle(on: Boolean): String {
        return resources.getString(R.string.home_bluetooth_permission, getPermissionEnabledTitle(on))
    }

    private fun getPermissionEnabledTitle(on: Boolean): String {
        return resources.getString(if (on) R.string.home_permission_on else R.string.home_permission_off)
    }

    private fun goToNewsWebsite() {
        val url = getString(R.string.home_set_complete_external_link_news_url)
        try {
            Intent(Intent.ACTION_VIEW).run {
                data = Uri.parse(url)
                startActivity(this)
            }
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(WebViewActivity.URL_ARG, url)
            startActivity(intent)
        }
    }

    private fun goToCovidApp() {
        val url = getString(R.string.home_set_complete_external_link_app_url)
        try {
            Intent(Intent.ACTION_VIEW).run {
                data = Uri.parse(url)
                startActivity(this)
            }
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(WebViewActivity.URL_ARG, url)
            startActivity(intent)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == LOCATION && EasyPermissions.somePermissionPermanentlyDenied(this, listOf(Manifest.permission.ACCESS_FINE_LOCATION))) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == LOCATION) {
            checkBLESupport()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    fun getAccountInfo() {
        val db = FirebaseFirestore.getInstance();
        val userUID = FirebaseAuth.getInstance().uid
        val documentPath = "users/$userUID"
        db.document(documentPath).get().addOnSuccessListener {
            val user = it.toObject(UserInfo::class.java)
            user?.let {
                user_name?.text = "Hi, ${user.name}"
            }
        }
    }

}

data class UserInfo(val name: String? = null, val age: String? = null, val county: String? = null, val phoneNumber: String? = null)

