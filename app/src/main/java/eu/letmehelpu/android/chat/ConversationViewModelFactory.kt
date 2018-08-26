package eu.letmehelpu.android.conversation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Handler
import eu.letmehelpu.android.chat.ConversationViewModel
import eu.letmehelpu.android.messaging.SendMessage
import java.util.concurrent.Executor
import eu.letmehelpu.android.messaging.model.Conversation

class ConversationViewModelFactory(
        val userId:Long,
        val conversation: Conversation,
        private val sendMessage: SendMessage) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val handler = Handler()
        val e = Executor { p0 -> handler.post(p0) }
        @Suppress("UNCHECKED_CAST")
        return ConversationViewModel(userId, conversation, e, sendMessage) as T
    }
}