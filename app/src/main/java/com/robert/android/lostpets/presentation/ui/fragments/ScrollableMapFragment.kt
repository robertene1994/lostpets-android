package com.robert.android.lostpets.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.maps.SupportMapFragment

/**
 * Fragment que se encarga de manejar la vista correspondiente al mapa personalizado (scrollable).
 *
 * @author Robert Ene
 */
class ScrollableMapFragment : SupportMapFragment() {

    /**
     * Interfaz que debe ser implementado por los componentes que contienen un mapa scrollable.
     *
     * @author Robert Ene
     */
    interface OnTouchListener {

        /**
         * Método que transmite los toques del mapa scrollable.
         */
        fun onTouch()
    }

    private inner class TouchableWrapper(context: Context) : FrameLayout(context) {
        override fun dispatchTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> mTouchListener.onTouch()
                MotionEvent.ACTION_UP -> mTouchListener.onTouch()
            }
            return super.dispatchTouchEvent(event)
        }
    }

    private lateinit var mTouchListener: OnTouchListener

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?,
                              savedInstance: Bundle?): View? {
        val layout = super.onCreateView(layoutInflater, viewGroup, savedInstance)
        (layout as ViewGroup).addView(TouchableWrapper(activity!!),
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT))
        return layout
    }

    /**
     * Método que establece el listener asociado al mapa scrollable.
     *
     * @param touchListener el listener del mapa scrollable.
     */
    fun setOnTouchListener(touchListener: OnTouchListener) {
        mTouchListener = touchListener
    }
}
