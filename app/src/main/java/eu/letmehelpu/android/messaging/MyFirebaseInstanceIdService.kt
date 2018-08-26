package eu.letmehelpu.android.messaging

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import dagger.android.AndroidInjection
import eu.letmehelpu.android.messaging.MessagingManager
import javax.inject.Inject


class MyFirebaseInstanceIdService : FirebaseInstanceIdService() {

    @Inject
    lateinit var manager:MessagingManager

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("Radek", "Refreshed token: " + refreshedToken!!)

        manager.putMessagingToken(refreshedToken)
        // If you want to send conversations to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken)
    }
}