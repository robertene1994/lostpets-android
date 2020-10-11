package com.robert.android.lostpets.presentation.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.util.Calendar

/**
 * Fragment que se encarga de manejar la vista correspondiente a la opción mediante la cuál el
 * usuario puede introducir una determinada fecha.
 *
 * @author Robert Ene
 */
class DatePickerFragment : DialogFragment() {

    companion object {

        /**
         * Método que instancia el fragment para la vista correspondiente a la opción mediante la
         * cuál el usuario puede introducir una determinada fecha.
         *
         * @return el fragment creado e instanciado para la vista correspondiente.
         */
        fun newInstance(): DatePickerFragment {
            return DatePickerFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(activity!!,
                targetFragment as DatePickerDialog.OnDateSetListener, year, month, day)
        dialog.datePicker.maxDate = calendar.time.time
        return dialog
    }
}
