package com.robert.android.lostpets.presentation.ui.adapters

import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.MessageStatus
import kotlinx.android.synthetic.main.card_message_item.view.*
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Clase adapter que se tiene la función de mostrar los mensajes pertenecientes a un determinado
 * chat.
 *
 * @author Robert Ene
 */
class MessagesListAdapter(private val items: List<Message>,
                          private val user: User)
    : RecyclerView.Adapter<MessagesListAdapter.ViewHolder>() {

    companion object {

        /**
         * Método que devuelve una hora basándose en un formato corto.
         *
         * @param date la fecha para la que se formatea la hora.
         */
        fun formatMessageTime(date: Date): String {
            val formatter = DateFormat
                    .getTimeInstance(DateFormat.SHORT, Locale.getDefault())
            return formatter.format(date)
        }
    }

    /**
     * Clase que se encarga de generar la vista (cardview) para cada uno de los mensajes de un
     * determinado chat del usuario.
     *
     * @author Robert Ene
     */
    class ViewHolder(cardView: CardView)
        : RecyclerView.ViewHolder(cardView) {

        /**
         * Método que se encarga de enlazar los detalles de un determinado mensaje a la
         * correspondiente vista (cardview).
         *
         * @param message el mensaje perteneciente al chat.
         * @param user el usuario al que pertenece el correspondiente mensaje.
         */
        fun bindMessage(message: Message, user: User) {
            if (message.fromUser.email == user.email) {
                itemView.messageConstraintLayout.background = ContextCompat.getDrawable(itemView
                        .context, R.drawable.layout_message_background_user)
                (itemView.messageLinearLayout.layoutParams
                        as FrameLayout.LayoutParams).gravity = Gravity.END
                (itemView.messageDateTextView.layoutParams
                        as ConstraintLayout.LayoutParams).rightMargin = 0

                itemView.messageStatusImageView.visibility = View.VISIBLE
                itemView.messageStatusImageView
                        .setImageResource(when (message.messageStatus) {
                            MessageStatus.SENT -> R.drawable.ic_up
                            MessageStatus.DELIVERED -> R.drawable.ic_down
                            MessageStatus.READ -> R.drawable.ic_eye
                        })
            } else {
                itemView.messageConstraintLayout.background = ContextCompat.getDrawable(itemView
                        .context, R.drawable.layout_message_background)
                val density = itemView.context.resources.displayMetrics.density
                (itemView.messageLinearLayout.layoutParams
                        as FrameLayout.LayoutParams).gravity = Gravity.START
                (itemView.messageDateTextView.layoutParams
                        as ConstraintLayout.LayoutParams).rightMargin = (4 * density).roundToInt()
                itemView.messageStatusImageView.visibility = View.GONE
                itemView.messageStatusImageView.setImageResource(0)
            }
            itemView.messageContentTextView.text = message.content
            itemView.messageDateTextView.text = formatMessageTime(message.date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_message_item, parent, false) as CardView
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessage(items[position], user)
    }
}
