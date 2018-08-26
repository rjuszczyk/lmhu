package eu.letmehelpu.android.conversations

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import dagger.android.support.DaggerFragment
import eu.letmehelpu.android.R
import eu.letmehelpu.android.chat.ChatConversationActivity
import eu.letmehelpu.android.messaging.model.Conversation

class ConversationsFragment : DaggerFragment() , ConversationListAdapter.OnConversationSelectedListener {
    override fun onConversationSelected(conversation: Conversation) {
        startActivity(ChatConversationActivity.createIntent(context!!, 10, conversation))
    }


    private lateinit var conversationListAdapter:ConversationListAdapter
    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = arguments!!.getLong(EXTRA_USER_ID, -1)
        if(userId == -1L) throw RuntimeException()
        conversationListAdapter = ConversationListAdapter(userId, this)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_conversations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(null)


        val testImage = view.findViewById <ImageView>(R.id.testImage)
//            GlideApp.with(this).load("http://goo.gl/gEgYUd").into(testImage)

        val conversations = view.findViewById <RecyclerView>(R.id.conversations)
        conversations.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        conversations.adapter = conversationListAdapter

        val conversationViewModel = ViewModelProviders.of(this, ConversationListViewModelFactory(userId)).get(ConversationListViewModel::class.java)
        conversationViewModel.getConversations().observe(this, Observer {
            it?.let { conversationListAdapter.setConversations(it) }
        })
    }


    companion object {
        val EXTRA_USER_ID = "userId"
        public fun createFragment(userId: Long): ConversationsFragment {
            val fragment = ConversationsFragment()
            val arguments = Bundle()
            arguments.putLong(EXTRA_USER_ID, userId)
            fragment.arguments = arguments
            return fragment
        }
    }
}
