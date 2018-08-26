package eu.letmehelpu.android.messaging

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.v4.app.RemoteInput
import android.util.Log
import dagger.android.AndroidInjection
import eu.letmehelpu.android.login.entity.LoginGateway
import eu.letmehelpu.android.messaging.model.Conversation
import javax.inject.Inject

class SendMessageService : IntentService("SendMessageService") {

    @Inject lateinit var messagesNotificationManager: MessagesNotificationManager
    @Inject lateinit var sendMessageAndLoadConversation : SendMessageAndLoadConversation
    @Inject lateinit var loginGateway: LoginGateway

    companion object {
        val SEND_MESSAGE_TO_CONVERSATION_ACTION = "SEND_MESSAGE_TO_CONVERSATION_ACTION"
        val DISMISS_CONVERSATION_ACTION = "DISMISS_CONVERSATION_ACTION"

        fun createSendMessageIntent(context: Context, conversation: Conversation): Intent {
            val intent = Intent(context, SendMessageService::class.java)
            intent.action = SEND_MESSAGE_TO_CONVERSATION_ACTION
            intent.putExtra("conversation", conversation)
            intent.addCategory(conversation.documentId)
            return intent
        }

        fun createDismissConversationIntent(context: Context, conversation: Conversation): Intent {
            val intent = Intent(context, SendMessageService::class.java)
            intent.action = DISMISS_CONVERSATION_ACTION
            intent.putExtra("conversation", conversation)
            intent.addCategory(conversation.documentId)
            return intent
        }
    }
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            if(it.action == SEND_MESSAGE_TO_CONVERSATION_ACTION) {

                val conversation = intent.getSerializableExtra("conversation") as Conversation
                val message = getReplyMessage(intent)
                Log.d("RADEK", "sending ["+message+"] to [" + conversation.documentId+"]")
                sendMessage(conversation, message)
            }
            if(it.action == DISMISS_CONVERSATION_ACTION) {
                val conversation = intent.getSerializableExtra("conversation") as Conversation
                dismissConversation(conversation)
            }
        }
    }

    private fun dismissConversation(conversation: Conversation) {
        messagesNotificationManager.cancelConversationNotification(conversation)
    }

    private fun getReplyMessage(intent: Intent): String {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val charSequence = remoteInput.getCharSequence(
                "message")
        return charSequence!!.toString()
    }

    fun sendMessage(conversation:Conversation, message: String) {
        val userId = loginGateway.loggedUser.userDetails.id
        val messages = sendMessageAndLoadConversation.sendAndLoad(conversation, userId, message).blockingGet()

        messagesNotificationManager.displayConversationNotification(conversation, userId, messages)
    }
}
