package com.robert.android.lostpets.presentation.ui.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.util.Calendar

/**
 * Fragment que se encarga de manejar la vista correspondiente a la opción mediante la cuál el
 * usuario puede introducir una determinada hora y los minutos asociados.
 *
 * @author Robert Ene
 */
class TimePickerFragment : DialogFragment() {

    companion object {

        /**
         * Método que instancia el fragment para la vista correspondiente a la opción mediante la
         * cuál el usuario puede introducir una determinada hora y los minutos asociados.
         *
         * @return el fragment creado e instanciado para la vista correspondiente.
         */
        fun newInstance(): TimePickerFragment {
            return TimePickerFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(activity, targetFragment as TimePickerDialog.OnTimeSetListener,
                hour, minute, true)
    }
}
