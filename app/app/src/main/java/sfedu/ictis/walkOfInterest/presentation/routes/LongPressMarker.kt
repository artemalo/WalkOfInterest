package sfedu.ictis.walkOfInterest.presentation.routes

import android.view.MotionEvent
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class LongPressMarker(
    mapView: MapView,
    private val onLongClick: (LongPressMarker) -> Unit
) : Marker(mapView) {

    override fun onLongPress(event: MotionEvent, mapView: MapView): Boolean {
        if (hitTest(event, mapView)) {
            onLongClick(this)
            return true
        }
        return super.onLongPress(event, mapView)
    }
}