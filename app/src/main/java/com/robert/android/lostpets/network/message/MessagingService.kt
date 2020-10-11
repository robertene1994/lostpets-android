package com.robert.android.lostpets.network.message

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationManagerCompat
import com.robert.android.lostpets.domain.executor.impl.ThreadExecutor
import com.robert.android.lostpets.domain.interactors.HandleStompConnectionInteractor
import com.robert.android.lostpets.domain.interactors.HandleUserTopicConnectionInteractor
import com.robert.android.lostpets.domain.interactors.impl.HandleStompConnectionInteractorImpl
import com.robert.android.lostpets.domain.interactors.impl.HandleUserTopicConnectionInteractorImpl
import com.robert.android.lostpets.domain.interactors.impl.SendMessageInteractorImpl
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.MessageStatus
import com.robert.android.lostpets.domain.utils.Jwt
import com.robert.android.lostpets.network.MessagingServiceGenerator
import com.robert.android.lostpets.presentation.ui.broadcasts.MessageReceiver
import com.robert.android.lostpets.presentation.ui.broadcasts.impl.MessageBroadcastReceiverImpl
import com.robert.android.lostpets.presentation.ui.broadcasts.impl.MessagingServiceBroadcastReceiverImpl
import com.robert.android.lostpets.presentation.ui.notifications.NotificationsManager
import com.robert.android.lostpets.storage.SessionRepositoryImpl
import com.robert.android.lostpets.threading.MainThreadImpl
import ua.naiksoftware.stomp.StompClient

/**
 * Servicio que extiende la clase Service e implementa las interfaces de los callbacks de los
 * interactors HandleStompConnectionInteractor y HandleUserTopicConnectionInteractor. Además,
 * este servicio implementa la interfaz MessageReceiver con el objetivo de recibir mensajes
 * procedentes de la vista del chat correspondiente. Es el controlador que se encarga de manejar
 * toda la lógica del servicio de mensajería, tanto en background (la aplicación no se ha
 * lanzado aún) como en foreground (la aplicación se encuentra funcionando).
 *
 * @see android.app.Service
 * @see com.robert.android.lostpets.domain.interactors.HandleStompConnectionInteractor.Callback
 * @see com.robert.android.lostpets.domain.interactors.HandleUserTopicConnectionInteractor.Callback
 * @see com.robert.android.lostpets.presentation.ui.broadcasts.MessageReceiver
 */
@SuppressLint("StaticFieldLeak")
class MessagingService : Service(), HandleStompConnectionInteractor.Callback,
        HandleUserTopicConnectionInteractor.Callback, MessageReceiver {

    companion object {
        private const val CONNECTION_ATTEMPTS = 10
        private const val CONNECTION_ATTEMPT_MS = 6000L

        private var instance: MessagingService? = null
        private lateinit var context: Context
        private var mConnectionAttempts: Long = 0

        /**
         * Método que lanza el servicio de mensajería.
         *
         * @param ctx el contexto de la aplicación.
         */
        fun startService(ctx: Context) {
            context = ctx
            if (checkValidUserSession() && instance == null)
                context.startForegroundService(Intent(context, MessagingService::class.java))
        }

        /**
         * Método que detiene el servicio de mensajería.
         */
        fun stopService() {
            if (::context.isInitialized)
                context.stopService(Intent(context, MessagingService::class.java))
        }

        /**
         * Método que reinicia el servicio de mensajería. Se ha establecido un número determinado
         * de {@link CONNECTION_ATTEMPTS} reintentos que se ejecutan cada cierto {@link
         * CONNECTION_ATTEMPT_MS} intervalo.
         */
        fun restartService() {
            if (instance == null || !instance!!.mStompClient.isConnected) {
                if (mConnectionAttempts < CONNECTION_ATTEMPTS) {
                    stopService()
                    Handler().postDelayed({
                        mConnectionAttempts += 1
                        startService(context)
                    }, CONNECTION_ATTEMPT_MS)
                }
            } else
                mConnectionAttempts = 0
        }

        /**
         * Método de acceso para el cliente STOMP utilizado para las conexiones del servicio
         * de mensajería.
         *
         * @return el cliente STOMP generado.
         */
        fun getStompClient(): StompClient? {
            return instance?.mStompClient
        }

        private fun checkValidUserSession(): Boolean {
            val sessionRepository = SessionRepositoryImpl(context)
            val token = sessionRepository.getToken()
            if (sessionRepository.getUser() != null && !tokenIsExpired(token))
                return true
            return false
        }

        private fun tokenIsExpired(token: String?): Boolean {
            if (token == null || token.trim().isEmpty())
                return true
            return Jwt(token).isExpired()
        }
    }

    private lateinit var mMessagingServiceReceiver: BroadcastReceiver
    private lateinit var mStompClient: StompClient
    private var mUser: User? = null

    override fun onCreate() {
        super.onCreate()

        startForeground(NotificationsManager.foregroundServiceNotificationId!!,
                NotificationsManager.getForegroundServiceNotification())

        instance = this
        mStompClient = MessagingServiceGenerator.createStompClient(this)
        mUser = SessionRepositoryImpl(this).getUser()

        HandleStompConnectionInteractorImpl(ThreadExecutor.instance,
                MainThreadImpl.instance, mStompClient, this).execute()

        mMessagingServiceReceiver = MessagingServiceBroadcastReceiverImpl(this)
        registerReceiver(mMessagingServiceReceiver,
                IntentFilter(MessagingServiceBroadcastReceiverImpl.ACTION))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        HandleUserTopicConnectionInteractorImpl(
                ThreadExecutor.instance, MainThreadImpl.instance,
                mStompClient, SessionRepositoryImpl(this), this).execute()
        stopForeground(true)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMessagingServiceReceiver)
        instance = null
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onConnectionError() {
        sendBroadcast(Intent(MessageBroadcastReceiverImpl.ACTION).apply {
            putExtra(MessageBroadcastReceiverImpl.CONNECTION_ERROR,
                    MessageBroadcastReceiverImpl.CONNECTION_ERROR)
        })
    }

    override fun onFailedServerHeartbeat() {
        sendBroadcast(Intent(MessageBroadcastReceiverImpl.ACTION).apply {
            putExtra(MessageBroadcastReceiverImpl.FAILED_SERVER_HEARTBEAT,
                    MessageBroadcastReceiverImpl.FAILED_SERVER_HEARTBEAT)
        })
    }

    override fun onNoInternetConnection() {
        // no aplicable
    }

    override fun onServiceNotAvailable() {
        // no aplicable
    }

    override fun onMessageReceived(message: Message) {
        if (isChatDetailActivityVisible()) {
            sendBroadcast(Intent(MessageBroadcastReceiverImpl.ACTION).apply {
                putExtra(MessageBroadcastReceiverImpl.MESSAGE_RECEIVED, message)
            })
        } else
            onMessage(message)
    }

    override fun onMessageReceivedError() {
        sendBroadcast(Intent(MessageBroadcastReceiverImpl.ACTION).apply {
            putExtra(MessageBroadcastReceiverImpl.MESSAGE_RECEIVED_ERROR,
                    MessageBroadcastReceiverImpl.MESSAGE_RECEIVED_ERROR)
        })
    }

    override fun onMessage(message: Message) {
        if (message.messageStatus == MessageStatus.SENT) {
            message.messageStatus = MessageStatus.DELIVERED
            SendMessageInteractorImpl(ThreadExecutor.instance, MainThreadImpl.instance,
                    mStompClient,null, message, message.fromUser).execute()
        }

        if (message.fromUser.id != mUser!!.id) {
            Handler().postDelayed({
                sendBroadcast(Intent(MessageBroadcastReceiverImpl.ACTION).apply {
                    putExtra(MessageBroadcastReceiverImpl.MESSAGE_RECEIVED, message)
                })
            }, 500)

            val notification = NotificationsManager.getMessageNotification(message)
            with(NotificationManagerCompat.from(this)) {
                notify(message.fromUser.id!!.toInt(), notification)
            }
        }
    }

    private fun isChatDetailActivityVisible(): Boolean {
        val activityManager
                = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.appTasks.forEach {
            if (it.taskInfo.topActivity != null
                    && it.taskInfo.topActivity.toString().contains("ChatDetailActivity"))
                return true
        }
        return false
    }
}
