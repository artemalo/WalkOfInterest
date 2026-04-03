package sfedu.ictis.walkOfInterest.presentation.main

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.data.model.PointDto
import sfedu.ictis.walkOfInterest.databinding.ActivityMainBinding
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepositoryImpl
import sfedu.ictis.walkOfInterest.infrastructure.network.NetworkModule

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(RouteRepositoryImpl(NetworkModule.routeApi))
    }

    private var isSelectingFrom: Boolean? = null

    private var markerFrom: Marker? = null
    private var markerTo: Marker? = null

    private var routePolyline: org.osmdroid.views.overlay.Polyline? = null

    private var isMapReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ВАЖНО: Инициализация OSMDroid ДО setContentView! Иначе будет черный экран.
        Configuration.getInstance().load(
            applicationContext,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()
        setupListeners()
        observeState()
    }

    private fun setupMap() {
        val map = binding.map

        map.addOnFirstLayoutListener { _, _, _, _, _ ->
            isMapReady = true
            // Как только карта готова — принудительно обновляем состояние из ViewModel
            // Чтобы отрисовать то, что уже пришло, пока карта "спала"
            lifecycleScope.launch {
                drawCurrentState(viewModel.uiState.value)
            }
        }

        map.setMultiTouchControls(true) // Включаем зум щипком

        val startPoint = GeoPoint(47.2220, 39.7190)
        map.controller.setZoom(15.0)
        map.controller.setCenter(startPoint)


        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (p != null && isSelectingFrom != null) {
                    handleMapClick(p)
                    return true
                }
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        }
        map.overlays.add(MapEventsOverlay(mapEventsReceiver))
    }

    private fun handleMapClick(geoPoint: GeoPoint) {
        val selectingFrom = isSelectingFrom ?: return
        val mockAddress = "${geoPoint.latitude.toString().take(6)}, ${geoPoint.longitude.toString().take(6)}"

        // Просто уведомляем вьюмодель. Она обновит стейт -> стейт обновит карту.
        viewModel.onPointSelected(selectingFrom, geoPoint.latitude, geoPoint.longitude, mockAddress)

        isSelectingFrom = null
    }

//    private fun addMarker(geoPoint: GeoPoint, isFrom: Boolean) {
//        val map = binding.map
//
//        if (isFrom) {
//            markerFrom?.let { map.overlays.remove(it) }
//        } else {
//            markerTo?.let { map.overlays.remove(it) }
//        }
//
//        val marker = Marker(map).apply {
//            position = geoPoint
//            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//            title = if (isFrom) "Откуда" else "Куда"
//
//            val idMarker = if (isFrom) R.drawable.ic_a else R.drawable.ic_b
//            icon = ContextCompat.getDrawable(this@MainActivity, idMarker)
//        }
//
//        if (isFrom) markerFrom = marker else markerTo = marker
//        map.overlays.add(marker)
//        map.invalidate() // Заставляем карту перерисоваться
//    }

    private fun setupListeners() {
        binding.fieldFrom.setOnClickListener {
            isSelectingFrom = true
        }

        binding.fieldTo.setOnClickListener {
            isSelectingFrom = false
        }

        binding.fieldClock.setOnClickListener {
            if (viewModel.uiState.value.isTimePickerEnabled) {
                showTimePicker()
            } else {
                Toast.makeText(this, "Сначала выберите обе точки на карте", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCalculate.setOnClickListener {
            viewModel.onCalculateClicked()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUiText(state)

                    if (isMapReady) {
                        drawCurrentState(state)
                    }
                }
            }
        }
    }

    private fun drawCurrentState(state: MainUiState) {
        // Рисуем маркеры и маршрут (внутри drawRoute уже есть наш зум)
        updateMapMarkers(state.pointFrom, state.pointTo)
        if (state.route != null) {
            drawRoute(state.route)
        } else {
            clearRoute()
        }
    }

    private fun updateMapMarkers(from: PointDto?, to: PointDto?) {
        val map = binding.map

        markerFrom?.let { map.overlays.remove(it) }
        markerTo?.let { map.overlays.remove(it) }

        from?.let {
            markerFrom = Marker(map).apply {
                position = GeoPoint(it.lat, it.lon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_a)
            }
            map.overlays.add(markerFrom)
        }

        to?.let {
            markerTo = Marker(map).apply {
                position = GeoPoint(it.lat, it.lon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_b)
            }
            map.overlays.add(markerTo)
        }

        map.invalidate()
    }

    private fun updateUiText(state: MainUiState) {
        binding.textFrom.text = state.addressFrom
        binding.textTo.text = state.addressTo

        binding.textClock.text = state.minTimeMinutes?.let { formatMinutes(it) } ?: "Выберите время"

        binding.fieldClock.alpha = if (state.isTimePickerEnabled) 1.0f else 0.5f
        binding.btnCalculate.isEnabled = state.isCalculateEnabled
        binding.btnCalculate.alpha = if (state.isCalculateEnabled) 1.0f else 0.5f

        binding.btnCalculateText.text =
            if (state.isLoading) "Загрузка..." else "Рассчитать"
    }

    private fun formatMinutes(totalMinutes: Int): String {
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        return "${h}ч ${m}м"
    }

    private fun drawRoute(routePoints: List<PointDto>) {
        val map = binding.map
        clearRoute()
        if (routePoints.isEmpty()) return

        val geoPoints = routePoints.map { GeoPoint(it.lat, it.lon) }

        routePolyline = Polyline(map).apply {
            setPoints(geoPoints)
            outlinePaint.color = ContextCompat.getColor(this@MainActivity, R.color.general)
            outlinePaint.strokeWidth = 12f
            outlinePaint.isAntiAlias = true
        }

        map.overlays.add(routePolyline)

        // ВАЖНО: используем post, чтобы карта успела получить размеры перед зумом
        map.post {
            if (geoPoints.isNotEmpty()) {
                val boundingBox = BoundingBox.fromGeoPoints(geoPoints)
                // Добавляем проверку на случай, если Activity уже закрывается
                if (!isFinishing) {
                    map.zoomToBoundingBox(boundingBox, true, 150)
                }
            }
        }
        map.invalidate()
    }

    private fun clearRoute() {
        routePolyline?.let {
            binding.map.overlays.remove(it)
            binding.map.invalidate()
            routePolyline = null
        }
    }

    override fun onDestroy() {
        Log.w("MainActivity", "Map Destroy")
        super.onDestroy()
        binding.map.onDetach()
    }

    override fun onResume() {
        Log.w("MainActivity", "Map Resume")
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        Log.w("MainActivity", "Map Pause")
        super.onPause()
        binding.map.onPause()
    }

    private fun showTimePicker() {
        val currentMin = viewModel.uiState.value.minTimeMinutes ?: 0
        TimePickerDialog(this, { _, hour, minute ->
            viewModel.onTimeSelected(hour * 60 + minute)
        }, currentMin / 60, currentMin % 60, true).show()
    }
}