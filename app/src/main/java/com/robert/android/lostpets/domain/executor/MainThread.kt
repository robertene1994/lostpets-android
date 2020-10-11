package com.robert.android.lostpets.domain.executor

/**
 * Interfaz MainThread responsable de la ejecución de métodos en el hilo principal de la
 * aplicación (hilo de la interfaz de usuario). Se utiliza para notificar al hilo principal de la
 * aplicación de que los resultados de los interactors ejecutados en background están listos.
 *
 * @author Robert Ene
 */
interface MainThread {

    /**
     * Método que ejecuta la operación en el hilo principal de la aplicación.
     *
     * @param runnable el método que se desea ejecutar.
     */
    fun post(runnable: Runnable)
}
