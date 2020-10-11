package com.robert.android.lostpets.presentation.ui.fragments

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.executor.impl.ThreadExecutor
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.network.ServiceGenerator
import com.robert.android.lostpets.network.service.ChatService
import com.robert.android.lostpets.presentation.presenters.UserChatsPresenter
import com.robert.android.lostpets.presentation.presenters.impl.UserChatsPresenterImpl
import com.robert.android.lostpets.presentation.ui.activities.ChatDetailActivity
import com.robert.android.lostpets.presentation.ui.adapters.ChatsListAdapter
import com.robert.android.lostpets.presentation.ui.broadcasts.MessageBroadcastReceiver
import com.robert.android.lostpets.presentation.ui.broadcasts.impl.MessageBroadcastReceiverImpl
import com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
import com.robert.android.lostpets.presentation.ui.utils.CodeGeneratorUtil
import com.robert.android.lostpets.presentation.ui.utils.SnackbarUtil
import com.robert.android.lostpets.threading.MainThreadImpl
import kotlinx.android.synthetic.main.fragment_user_chats.*
import kotlinx.android.synthetic.main.progress_bar.*

/**
 * Fragment que extiende la clase AbstractFragment e implementa la interfaz del callback del
 * presenter UserChatsPresenter. Además, esta activity implementa la interfaz ChatDetailReceiver
 * con el objetivo de recibir mensajes procedentes del servicio correspondiente. Es el controlador
 * que se encarga de manejar la vista asociada a los chats pertenecientes a un usuario.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
 * @see com.robert.android.lostpets.presentation.presenters.UserChatsPresenter.View
 * @see com.robert.android.lostpets.presentation.ui.broadcasts.MessageBroadcastReceiver
 */
class UserChatsFragment : AbstractFragment(), UserChatsPresenter.View, MessageBroadcastReceiver {

    companion object {
        private const val USER = "UserChatsFragment::User"
        private const val USER_TO_CHAT = "UserChatsFragment::UserToChat"

        /**
         * Método que instancia el fragment para la vista asociada a los chats pertenecientes
         * a un usuario.
         *
         * @param user el usuario que se encuentra autenticado en la aplicación.
         * @param userToChat el usuario con el que se intercambia mensajes, si es que se trata de
         * un nuevo chat.
         * @return el fragment creado e instanciado para la vista correspondiente.
         */
        fun newInstance(user: User, userToChat: User? = null): Fragment {
            val fragment = UserChatsFragment()
            val bundle = Bundle()
            bundle.putParcelable(USER, user)
            bundle.putParcelable(USER_TO_CHAT, userToChat)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var mUserChatsPresenter: UserChatsPresenter
    private lateinit var mMessageBroadcastReceiver: BroadcastReceiver
    private lateinit var mUser: User
    private var mUserToChat: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_user_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadExtras()
        init()
    }

    override fun onResume() {
        super.onResume()
        activity!!.registerReceiver(mMessageBroadcastReceiver,
                IntentFilter(MessageBroadcastReceiverImpl.ACTION))
        mUserChatsPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        mUserChatsPresenter.pause()
    }

    override fun onStop() {
        super.onStop()
        mUserChatsPresenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(mMessageBroadcastReceiver)
        mUserChatsPresenter.destroy()
    }

    override fun showUserChats(chats: List<Chat>) {
        if (userChatsRecyclerView != null) {
            userChatsRecyclerView.layoutManager = LinearLayoutManager(context)
            userChatsRecyclerView.adapter = ChatsListAdapter(chats) {
                val intent = Intent(context, ChatDetailActivity::class.java)
                intent.putExtra(ChatDetailActivity.CHAT, it)
                intent.putExtra(ChatDetailActivity.USER, mUser)
                startActivity(intent)
            }
        }
        checkForOpenChat(chats)
    }

    override fun onUserChatsEmpty() {
        SnackbarUtil.makeLong(fragmentUserChatsLayout, R.string.msg_no_chats)
        checkForOpenChat()
    }

    override fun onMessage(message: Message) {
        mUserChatsPresenter.getUserChats()
    }

    override fun onConnectionError() {
        SnackbarUtil.makeShort(fragmentUserChatsLayout, R.string.msg_messaging_connection_error)
    }

    override fun onFailedServerHeartbeat() {
        SnackbarUtil.makeShort(fragmentUserChatsLayout,
                R.string.msg_messaging_failed_server_heartbeat)
    }

    override fun onMessageReceivedError() {
        SnackbarUtil.makeShort(fragmentUserChatsLayout,
                R.string.msg_messaging_message_received_error)
    }

    override fun noInternetConnection() {
        SnackbarUtil.makeShort(fragmentUserChatsLayout, R.string.msg_no_internet_connection)
    }

    override fun serviceNotAvailable() {
        SnackbarUtil.makeLong(fragmentUserChatsLayout, R.string.msg_service_not_avabile)
    }

    private fun loadExtras() {
        with(arguments!!) {
            mUser = getParcelable(USER)!!
            mUserToChat = getParcelable(USER_TO_CHAT)
        }
    }

    private fun init() {
        (activity as AppCompatActivity).supportActionBar!!
                .setTitle(R.string.user_chats_fragment_toolbar_title)

        setProgressBarLayout(progressBarConstraintLayout)
        addViewsToHide(userChatsRecyclerView)

        mMessageBroadcastReceiver = MessageBroadcastReceiverImpl(this)

        mUserChatsPresenter = UserChatsPresenterImpl(ThreadExecutor.instance,
                MainThreadImpl.instance, this, context,
                ServiceGenerator.createService(context, ChatService::class.java), mUser)
    }

    private fun checkForOpenChat(chats: List<Chat>? = null) {
        if (mUserToChat != null) {
            var chat = chats?.find { c -> c.toUser.id == mUserToChat!!.id }

            if (chat == null)
                chat = Chat(null, CodeGeneratorUtil.random(), mUser,
                        mUserToChat!!, null, 0)
            val intent = Intent(context, ChatDetailActivity::class.java)
            intent.putExtra(ChatDetailActivity.CHAT, chat)
            intent.putExtra(ChatDetailActivity.USER, mUser)
            startActivity(intent)
            mUserToChat = null
        }
    }
}
