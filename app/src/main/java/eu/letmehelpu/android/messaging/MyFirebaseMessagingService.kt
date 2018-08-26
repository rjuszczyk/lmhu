package eu.letmehelpu.android.messaging

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.android.AndroidInjection
import eu.letmehelpu.android.AppConstant

import eu.letmehelpu.android.login.entity.LoginGateway
import eu.letmehelpu.android.messaging.model.Conversation
import eu.letmehelpu.android.messaging.model.ConversationDocument
import eu.letmehelpu.android.messaging.model.Message
import eu.letmehelpu.android.messaging.model.MessageFcm


import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import java.util.*
import javax.inject.Inject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val gson = Gson()
    val TAG="RADEK"

    @Inject
    lateinit var loadMessages: LoadMessages
    @Inject
    lateinit var messagesNotificationManager: MessagesNotificationManager
    @Inject
    lateinit var loginGateway: LoginGateway
    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "messages"
            val description = "messages from chat"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("messages", name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting conversations here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {

            val conversationId = remoteMessage.data["conversationId"]!!
            val messageFcm = gson.fromJson(remoteMessage.data["data"], MessageFcm::class.java)

            Log.d(TAG, "Message Notification Body: " + messageFcm.text)

//            if(conversationId != ConversationActivity.startedConversation) {
                loadConversation(conversationId, messageFcm.getSendTimestamp().time)
//            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {

        }
    }

    val conversationDisposables = HashMap<String, Disposable>()
    private fun loadConversation(conversationId: String, noOlderThan: Long) {
        val db = FirebaseFirestore.getInstance()

        conversationDisposables.remove(conversationId)?.dispose()

        db.collection(AppConstant.COLLECTION_CONVERSATION).document(conversationId).get().addOnSuccessListener {
            it?.let {
                val conversationDocument = it.toObject(ConversationDocument::class.java)!!
                val documentId = it.id
                if (conversationDocument.timestamp.toDate().time >= noOlderThan) {
                    val conversation = Conversation(conversationDocument, documentId)

                    val userId = loginGateway.loggedUser.userDetails.id

                    val lastRead = conversation.lastRead[userId.toString()]?: 0
                    Log.d("RADEK_LAST_READ", "" + lastRead)
                    var disposable = loadMessages.loadMessagesAfterTimestamp(conversation.documentId, Timestamp(Date(lastRead)), noOlderThan)
                            .subscribe(object : Consumer<List<Message>> {
                                override fun accept(t: List<Message>) {
                                    messagesNotificationManager.displayConversationNotification(conversation, userId, t)
                                }
                            })

                    conversationDisposables.put(conversationId, disposable)
                } else {
                    Log.d(TAG, "no in store")
                }
            }
        }
    }

    private fun getNotificationIdForConversation(conversationId: String): Int {
        return conversationId.hashCode()
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    companion object {
        fun startServiceForegroundCompat(context:Context, intent:Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun isMyServiceRunning(context: Context, service:Class<Any>) : Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (runningService in manager.getRunningServices(Integer.MAX_VALUE)) {
                if(service.name.equals(service.name)) {
                    return true
                }
            }
            return false
        }
    }
}