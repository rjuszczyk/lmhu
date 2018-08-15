package eu.letmehelpu.android.messaging

import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import eu.letmehelpu.android.AppConstant
import eu.letmehelpu.android.model.Conversation
import eu.letmehelpu.android.model.Message
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*

class LoadMessages {
    val MESSAGES_LIMIT = 10L

    public fun loadMessagesAfterTimestamp(conversationId: String, time: Timestamp, mustInclude: Long): Single<List<Message>> {

        return Single.create(object : SingleOnSubscribe<List<Message>> {
            override fun subscribe(emitter: SingleEmitter<List<Message>>) {

                var registration: ListenerRegistration? = null

                val db = FirebaseFirestore.getInstance()
                registration = db.collection(AppConstant.COLLECTION_CONVERSATION)
                        .document(conversationId)
                        .collection("messages")
                        .whereGreaterThanOrEqualTo("sendTimestamp", time)
                        .limit(MESSAGES_LIMIT)
                        .orderBy("sendTimestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener(object : EventListener<QuerySnapshot> {
                            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                                p0?.let {
                                    val messages = it.toObjects(Message::class.java)

                                    if (!includes(messages, mustInclude)) {
                                        return
                                    }


                                    registration!!.remove()

                                    if (!emitter.isDisposed) {
                                        emitter.onSuccess(messages)
                                    }
                                }
                            }
                        })

                emitter.setDisposable(object : Disposable {
                    var disposed = false

                    override fun isDisposed(): Boolean {
                        return disposed
                    }

                    override fun dispose() {
                        disposed = true
                        registration!!.remove()
                    }
                })
            }
        })
    }

    private fun includes(messages: List<Message>, mustInclude: Long): Boolean {
        for (message in messages) {
            if (message.sendTimestamp.toDate().time == mustInclude) {
                return true
            }
        }
        return false
    }
}