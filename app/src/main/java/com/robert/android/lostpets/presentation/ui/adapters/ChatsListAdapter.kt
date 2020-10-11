package com.robert.android.lostpets.presentation.ui.adapters

import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.types.MessageStatus
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.android.synthetic.main.card_chat_item.view.*

/**
 * Clase adapter que se tiene la función de mostrar los chats del usuario.
 *
 * @author Robert Ene
 */
class ChatsListAdapter(
    private val items: List<Chat>,
    private val itemClick: (Chat) -> Unit
) :
    RecyclerView.Adapter<ChatsListAdapter.ViewHolder>() {

    companion object {

        /**
         * Método que devuelve una fecha basándose en un formato corto.
         *
         * @param date la fecha que se formatea.
         */
        fun formatChatLastMessageShortDate(date: Date?): String {
            val formatter = DateFormat
                    .getDateInstance(DateFormat.SHORT, Locale.getDefault())
            return formatter.format(date)
        }

        /**
         * Método que devuelve una hora basándose en un formato corto.
         *
         * @param date la fecha para la que se formatea la hora.
         */
        fun formatChatLastMessageShortTime(date: Date?): String {
            val formatter = DateFormat
                    .getTimeInstance(DateFormat.SHORT, Locale.getDefault())
            return formatter.format(date)
        }
    }

    /**
     * Clase que se encarga de generar la vista (cardview) para cada uno de los chats del usuario.
     *
     * @author Robert Ene
     */
    class ViewHolder(
        cardView: CardView,
        private val itemClick: (Chat) -> Unit
    ) :
        RecyclerView.ViewHolder(cardView) {

        /**
         * Método que se encarga de enlazar los detalles de un determinado chat a la
         * correspondiente vista (cardview).
         *
         * @param chat el chat del usuario.
         */
        fun bindChat(chat: Chat) {
            with(chat) {
                val context = itemView.context
                itemView.userChatNameTextView.text = String.format(
                        context.getString(R.string.nav_user_profile_info),
                        toUser.lastName, toUser.firstName)
                if (isToday(lastMessage?.date))
                    itemView.userChatLastMessageTimeTextView.text =
                            formatChatLastMessageShortTime(lastMessage?.date)
                else
                    itemView.userChatLastMessageTimeTextView.text =
                            formatChatLastMessageShortDate(lastMessage?.date)

                val density = itemView.context.resources.displayMetrics.density
                if (lastMessage?.fromUser != chat.fromUser) {
                    (itemView.userChatLastMessageTextView.layoutParams
                            as ConstraintLayout.LayoutParams).leftMargin = (8 * density).roundToInt()
                    (itemView.userChatLastMessageStatusImageView.layoutParams
                            as ConstraintLayout.LayoutParams).leftMargin = 0
                    itemView.userChatLastMessageStatusImageView.visibility = View.GONE
                    if (lastMessage?.messageStatus == MessageStatus.DELIVERED) {
                        itemView.userChatLastMessageTextView
                                .setTextColor(context.getColor(R.color.colorPrimary))
                        itemView.userChatLastMessageTextView.setTypeface(
                                itemView.userChatLastMessageTextView.typeface, Typeface.BOLD)
                        itemView.userChatLastMessageTimeTextView
                                .setTextColor(context.getColor(R.color.colorPrimary))
                        itemView.userChatLastMessageTimeTextView.setTypeface(
                                itemView.userChatLastMessageTimeTextView.typeface, Typeface.BOLD)
                    }
                } else {
                    (itemView.userChatLastMessageTextView.layoutParams
                            as ConstraintLayout.LayoutParams).leftMargin = 0
                    (itemView.userChatLastMessageStatusImageView.layoutParams
                            as ConstraintLayout.LayoutParams).leftMargin = (8 * density).roundToInt()
                    itemView.userChatLastMessageStatusImageView.visibility = View.VISIBLE
                    itemView.userChatLastMessageStatusImageView
                            .setImageResource(when (lastMessage.messageStatus) {
                        MessageStatus.SENT -> R.drawable.ic_up
                        MessageStatus.DELIVERED -> R.drawable.ic_down
                        MessageStatus.READ -> R.drawable.ic_eye
                    })
                }

                itemView.userChatLastMessageTextView.text = lastMessage?.content

                if (chat.unreadMessages > 0) {
                    itemView.userChatUnreadMessagesTextView.visibility = View.VISIBLE
                    itemView.userChatUnreadMessagesTextView.text = chat.unreadMessages.toString()
                } else
                    itemView.userChatUnreadMessagesTextView.visibility = View.GONE

                itemView.setOnClickListener { itemClick(this) }
            }
        }

        private fun isToday(date: Date?): Boolean {
            val today = Calendar.getInstance()
            val specifiedDate = Calendar.getInstance()
            specifiedDate.time = date
            return today[Calendar.DAY_OF_MONTH] == specifiedDate[Calendar.DAY_OF_MONTH] &&
                    today[Calendar.MONTH] == specifiedDate[Calendar.MONTH] &&
                    today[Calendar.YEAR] == specifiedDate[Calendar.YEAR]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_chat_item, parent, false) as CardView
        return ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindChat(items[position])
    }
}
