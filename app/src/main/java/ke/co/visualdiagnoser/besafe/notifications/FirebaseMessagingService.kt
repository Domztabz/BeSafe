package ke.co.visualdiagnoser.besafe.notifications



import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ke.co.visualdiagnoser.besafe.BuildConfig
import ke.co.visualdiagnoser.besafe.HomeActivity
import ke.co.visualdiagnoser.besafe.R


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        private const val TAG = "MyFirebaseMsgService"

        private const val NOTIFICATION_ID = 17154
        private const val CHANNEL_ID = BuildConfig.SERVICE_FOREGROUND_CHANNEL_ID
        const val CHANNEL_SERVICE = BuildConfig.SERVICE_FOREGROUND_CHANNEL_NAME
    }
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage?.from}")

        // Check if message contains a data payload.
        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
//           TODO  sendNotification()
            val title = remoteMessage.data.get("title")
            Log.d(TAG, "Title $title")
            val message = remoteMessage.data.get("message")
            Log.d(TAG, "Message $message")

            title?.let {
                message?.let {
                    Log.d(TAG, "Both are present")
                    sendNotification(title, message)
                }
            }


        }

        // Check if message contains a notification payload.
        remoteMessage?.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        token?.let {
            uploadDeviceToken(it)
        }

    }

    fun uploadDeviceToken(token: String) {
        FirebaseAuth.getInstance().uid?.let {
            val db = FirebaseFirestore.getInstance();
            db.collection("customers").document(it).update("deviceToken", token)
                    .addOnSuccessListener {

                    };
        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(title: String, message: String) {
        Log.d(TAG, "sendNotification called")

        setupNotifications()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = beenInCloseContact(this, CHANNEL_ID, title, message)
        notificationManager.notify(NOTIFICATION_ID /* ID of notification */, notif)



//        val intent = Intent(this, ProductsPageActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

//        val intent = Intent(this, ProductsPageActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT)
//
//        val channelId = getString(R.string.default_notification_channel_id)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.mipmap.ic_launcher_logo_round)
//                .setContentTitle(title)
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        createNotificationChannel()
//        // Since android Oreo notification channel is needed.
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            Log.d(TAG, "Post Android Oreo")
////            val channel = NotificationChannel(channelId,
////                "Channel human readable title",
////                NotificationManager.IMPORTANCE_DEFAULT)
////            notificationManager.createNotificationChannel(channel)
////        }
//        val timeMillis = System.currentTimeMillis().toInt();
//
//        notificationManager.notify(timeMillis /* ID of notification */, notificationBuilder.build())
    }

     val CLOSE_CONTACT_NOTIFICATION_CODE = 14

    fun beenInCloseContact(context: Context, channel: String, title: String, description: String): Notification {
        val intent = Intent(context, HomeActivity::class.java)
        intent.putExtra("inCloseContactNotification", true)

        val activityPendingIntent = PendingIntent.getActivity(
                context, CLOSE_CONTACT_NOTIFICATION_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channel)
                .setContentTitle( title/*context.getText(R.string.in_close_contact_your_data_title)*/)
                .setContentText(description/*context.getText(R.string.in_close_contact_data_description)*/)
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setTicker(description/*context.getText(R.string.in_close_contact_data_description)*/)
                .setStyle(NotificationCompat.BigTextStyle().bigText(description/*context.getText(R.string.in_close_contact_data_description)*/))
                .setAutoCancel(true)

                .addAction(
                        R.drawable.ic_notification_setting,
                        context.getText(R.string.upload_data_action),
                        activityPendingIntent
                )
                .setContentIntent(activityPendingIntent)
                .setWhen(System.currentTimeMillis())
                .setSound(null)
                .setVibrate(null)
                .setColor(ContextCompat.getColor(context, R.color.notification_tint))

        return builder.build()
    }

    private fun setupNotifications() {

        val mNotificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_SERVICE
            // Create the channel for the notification
            val mChannel =
                    NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)
            mChannel.enableLights(false)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(0L)
            mChannel.setSound(null, null)
            mChannel.setShowBadge(true)

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }

//    private fun createNotificationChannel() {
//        Log.d(TAG, "Post Android Oreo")
//
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(getString(R.string.default_notification_channel_id), name, importance).apply {
//                description = descriptionText
//            }
//            // Register the channel with the system
//            val notificationManager: NotificationManager =
//                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

}
