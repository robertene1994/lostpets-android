package com.robert.android.lostpets.presentation.ui.adapters

import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.AdStatus
import com.robert.android.lostpets.domain.model.types.PetStatus
import com.robert.android.lostpets.network.ServiceGenerator
import com.robert.android.lostpets.presentation.ui.fragments.UpdateAdFragment
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_ad_item.view.*
import java.text.DateFormat
import java.util.*

/**
 * Clase adapter que se tiene la función de mostrar los anuncios de mascotas perdidas.
 *
 * @author Robert Ene
 */
class AdsListAdapter(private val items: List<Ad>,
                     private val user: User,
                     private val fragmentManager: FragmentManager?,
                     private val itemClick: (Ad) -> Unit)
    : RecyclerView.Adapter<AdsListAdapter.ViewHolder>() {

    companion object {

        /**
         * Método que devuelve una fecha basándose en un formato largo.
         *
         * @param date la fecha que se formatea.
         */
        fun formatAdLongDate(date: Date): String {
            val formatter = DateFormat
                    .getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.getDefault())
            return formatter.format(date)
        }

        /**
         * Método que devuelve una fecha basándose en un formato corto.
         *
         * @param date la fecha que se formatea.
         */
        fun formatAdShortDate(date: Date): String {
            val formatter = DateFormat
                    .getDateInstance(DateFormat.LONG, Locale.getDefault())
            return formatter.format(date)
        }

        /**
         * Método que devuelve una hora basándose en un formato corto.
         *
         * @param date la fecha para la que se formatea la hora.
         */
        fun formatAdShortTime(date: Date): String {
            val formatter = DateFormat
                    .getTimeInstance(DateFormat.SHORT, Locale.getDefault())
            return formatter.format(date)
        }
    }

    /**
     * Clase que se encarga de generar la vista (cardview) para cada uno de los anuncios de
     * mascotas perdidas.
     *
     * @author Robert Ene
     */
    class ViewHolder(cardView: CardView,
                     private val user: User,
                     private val fragmentManager: FragmentManager?,
                     private val itemClick: (Ad) -> Unit)
        : RecyclerView.ViewHolder(cardView) {

        /**
         * Método que se encarga de enlazar los detalles de un determinado anuncio de mascota
         * perdida a la correspondiente vista (cardview).
         *
         * @param ad el anuncio de la mascota perdida.
         * @param user el usuario al que pertenece el correspondiente anuncio.
         */
        fun bindAd(ad: Ad, user: User) {
            with(ad) {
                val context = itemView.context
                val imgUrl = "${ServiceGenerator.API_BASE_URL}$photo"

                Picasso.get().load(imgUrl).memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .placeholder(R.drawable.ic_pet)
                        .into(itemView.adPetImageView)

                if (ad.adStatus == AdStatus.DISABLED)
                    itemView.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.colorDangerLight))
                else
                    itemView.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.colorSecondaryText))

                itemView.petStatusEditText.filters = itemView.petStatusEditText.filters +
                        InputFilter.AllCaps()
                if (petStatus == PetStatus.FOUND) {
                    itemView.petStatusEditText.setText(context.getString(R.string.ad_pet_status_found))
                    itemView.petStatusEditText.setTextColor(
                            ContextCompat.getColor(context, R.color.colorSuccess))
                    itemView.feelingPetStatusImageView.setImageResource(R.drawable.ic_smile)
                } else {
                    itemView.petStatusEditText.setText(context.getString(R.string.ad_pet_status_lost))
                    itemView.petStatusEditText.setTextColor(
                            ContextCompat.getColor(context, R.color.colorDanger))
                    itemView.feelingPetStatusImageView.setImageResource(R.drawable.ic_sad)
                }

                itemView.codeEditText.setText(code)
                itemView.adDateEditText.setText(formatAdLongDate(date))
                itemView.adPetNameEditText.setText(pet.name)
                itemView.adRewardEditText.setText(
                        String.format(context.getString(R.string.ad_reward), reward))

                if (ad.user == user) {
                    itemView.updateAdCardButton.visibility = View.VISIBLE
                    itemView.updateAdCardButton.setOnClickListener { onClickEditAd(ad) }
                } else {
                    itemView.updateAdCardButton.visibility = View.GONE
                }

                itemView.setOnClickListener { itemClick(this) }
            }
        }

        private fun onClickEditAd(ad: Ad) {
            val transaction = fragmentManager!!.beginTransaction()
                    .replace(R.id.fragmentContent, UpdateAdFragment.newInstance(user, ad))
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_ad_item, parent, false) as CardView
        return ViewHolder(view, user, fragmentManager, itemClick)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindAd(items[position], user)
    }
}
