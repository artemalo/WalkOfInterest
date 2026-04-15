package sfedu.ictis.walkOfInterest.utils

import android.content.Context
import android.widget.Toast

class ToastManager {
    companion object {
        fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(context, message, duration).show()
        }
    }
}