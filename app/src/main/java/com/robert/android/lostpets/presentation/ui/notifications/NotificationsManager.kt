package com.robert.android.lostpets.presentation.ui.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.presentation.ui.activities.MainActivity

/**
 * Clase que gestiona todas las notificaciones de la aplicación.
 *
 * @author Robert Ene
 */
class NotificationsManager {

    companion object {
        private const val NOTIFICATION_REQUEST_CODE = 1938

        var foregroundServiceNotificationId: Int? = null
        private var notificationChannel: NotificationChannel? = null
        private lateinit var channelId: String
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        private lateinit var notifications: HashMap<Long, MutableList<Message>>

        /**
         * Método que crea un canal para todas las notificaciones de la aplicación.
         *
         * @param ctx el contexto de la aplicación.
         */
        fun createNotificationChannel(ctx: Context) {
            context = ctx
            if (notificationChannel == null) {
                createNotificationChannelGroup()
                foregroundServiceNotificationId = System.currentTimeMillis().toInt()
                channelId = context.getString(R.string.notification_channel_id)
                val name = context.getString(R.string.notification_channel_name)
                val importance = NotificationManager.IMPORTANCE_HIGH

                notificationChannel = NotificationChannel(channelId, name, importance).apply {
                    group = context.getString(R.string.notification_channel_group_id)
                }
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as
                        NotificationManager).createNotificationChannel(notificationChannel!!)
            }
        }

        /**
         * Método que genera y devuelve la notificación que se muestra durante la ejecución del
         * servicio que se encarga de la gestión de los mensajes.
         *
         * @return la notificación generada para el servicio de la gestión de los mensajes.
         */
        fun getForegroundServiceNotification(): Notification {
            return NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_pet)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setContentTitle(context.getString((R.string.app_name)))
                    .setContentText(context.getString(R.string.foreground_service_notification_content))
                    .setLargeIcon(BitmapFactory.decodeResource(
                            context.resources, R.mipmap.service_notification_logo))
                    .build()
        }

        /**
         * Método que se encarga de eliminar una determinada notificación, basada en el id del
         * usuario.
         *
         * @param userId el id del usuario para el que se debe eliminar la notificación.
         */
        fun removeMessageNotification(userId: Long) {
            if (::notifications.isInitialized)
                notifications.remove(userId)
        }

        /**
         * Método que genera y devuelve una notificación, basándose en el contenido de un
         * determinado mensaje.
         *
         * @param message el mensaje en el que se basa la notificación que se debe generar.
         * @return la notificación generada para el correspondiente mensaje.
         */
        fun getMessageNotification(message: Message): Notification {
            if (!::notifications.isInitialized)
                notifications = HashMap()

            if (!notifications.containsKey(message.fromUser.id))
                notifications[message.fromUser.id!!] = mutableListOf()

            val messages = notifications[message.fromUser.id]!!
            if (messages.find { m -> m.code == message.code } == null)
                messages.add(0, message)
            return addStyle(createNotificationBuilder(message), messages).build()
        }

        private fun createNotificationChannelGroup() {
            val notificationChannelGroup = NotificationChannelGroup(
                    context.getString(R.string.notification_channel_group_id),
                    context.getString(R.string.notification_channel_group_name))
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager).createNotificationChannelGroup(notificationChannelGroup)
        }

        private fun createNotificationBuilder(message: Message): NotificationCompat.Builder {
            return NotificationCompat.Builder(context, channelId)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_pet)
                    .setLargeIcon(BitmapFactory.decodeResource(
                            context.resources, R.mipmap.notification_logo))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setContentText(message.content)
                    .setGroup(message.fromUser.email)
                    .setContentIntent(createPendingIntentForNotification(message))
        }

        private fun createPendingIntentForNotification(message: Message): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(MainActivity.CHAT_FROM_OUTSIDE, MainActivity.CHAT_FROM_OUTSIDE)
            intent.putExtra(MainActivity.USER_TO_CHAT, message.fromUser)
            return TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(NOTIFICATION_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        private fun addStyle(
            builder: NotificationCompat.Builder,
            messages: MutableList<Message>
        ): NotificationCompat.Builder {
            val message = messages[0]

            val title = String.format(
                    context.getString(R.string.nav_user_profile_info),
                    message.fromUser.firstName, message.fromUser.lastName)
            val style = NotificationCompat.InboxStyle()
                    .setBigContentTitle(title)
                    .setSummaryText(message.fromUser.email)

            messages.forEach { style.addLine(it.content) }
            return builder.setContentTitle(title).setStyle(style)
        }
    }
}
