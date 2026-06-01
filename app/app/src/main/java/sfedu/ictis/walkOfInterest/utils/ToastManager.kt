package sfedu.ictis.walkOfInterest.utils

import android.content.Context
import android.widget.Toast

class ToastManager {
    companion object {
        private var currentToast: Toast? = null

        fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
            currentToast?.cancel()

            currentToast = Toast.makeText(context.applicationContext, message, duration)

            currentToast?.show()
        }
    }
}