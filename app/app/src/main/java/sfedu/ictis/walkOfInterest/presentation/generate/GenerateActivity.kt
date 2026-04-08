package sfedu.ictis.walkOfInterest.presentation.generate

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
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
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.data.repository.RouteRepositoryImpl
import sfedu.ictis.walkOfInterest.databinding.ActivityGenerateBinding
import sfedu.ictis.walkOfInterest.domain.usecase.CalculateWalkUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetBaseRouteUseCase
import sfedu.ictis.walkOfInterest.infrastructure.network.NetworkModule
import sfedu.ictis.walkOfInterest.notification.ToastManager

class GenerateActivity : AppCompatActivity() {
    private companion object {
        const val MSG_WARN = "Выберите обе точки на карте"
        const val MSG_WHAT = "Что-то пошло не так"

        const val NAME_ACTIVITY = "GenerateActivity"
    }

    private lateinit var binding: ActivityGenerateBinding
    private val viewModel: GenerateViewModel by viewModels {
        val repository = RouteRepositoryImpl(NetworkModule.routeApi)

        val getBaseRouteUseCase = GetBaseRouteUseCase(repository)
        val calculateWalkUseCase = CalculateWalkUseCase(repository)

        GenerateViewModelFactory(getBaseRouteUseCase, calculateWalkUseCase)
    }

    private var isSelectingFrom: Boolean? = null
    private var markerFrom: Marker? = null
    private var markerTo: Marker? = null
    private var routePolyline: Polyline? = null
    private var isMapReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ВАЖНО: Инициализация OSMDroid ДО setContentView
        Configuration.getInstance().load(
            applicationContext,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        binding = ActivityGenerateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()
        setupListeners()
        observeState()
    }

    private fun setupMap() {
        val map = binding.map

        map.addOnFirstLayoutListener { _, _, _, _, _ ->
            isMapReady = true

            lifecycleScope.launch {
                drawCurrentState(viewModel.uiState.value)
            }
        }

        map.setMultiTouchControls(true)

        val startPoint = GeoPoint(47.207564,38.938756)
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

    private fun setupListeners() {
        binding.fieldBtnBack.setOnClickListener {
            if (viewModel.uiState.value.isLoading) {
                viewModel.cancelAllRequests()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.fieldFrom.setOnClickListener {
            isSelectingFrom = true
        }

        binding.fieldTo.setOnClickListener {
            isSelectingFrom = false
        }

        binding.fieldClock.setOnClickListener {
            handleClockClick()
        }

        binding.fieldBtnCalculate.setOnClickListener {
           handleCalculateClick()
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

    private fun drawCurrentState(state: GenerateUiState) {
        updateMapMarkers(state.pointFrom, state.pointTo)

        if (state.route != null) {
            drawRoute(state.route)
        } else {
            clearRoute()
        }
    }

    private fun updateMapMarkers(from: DomainPoint?, to: DomainPoint?) {
        val map = binding.map

        markerFrom?.let { map.overlays.remove(it) }
        markerTo?.let { map.overlays.remove(it) }

        from?.let {
            markerFrom = Marker(map).apply {
                position = GeoPoint(it.lat, it.lon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(this@GenerateActivity, R.drawable.ic_a)
            }
            map.overlays.add(markerFrom)
        }

        to?.let {
            markerTo = Marker(map).apply {
                position = GeoPoint(it.lat, it.lon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(this@GenerateActivity, R.drawable.ic_b)
            }
            map.overlays.add(markerTo)
        }

        map.invalidate()
    }

    private fun updateUiText(state: GenerateUiState) {
        binding.textFrom.text = state.addressFrom
        binding.textTo.text = state.addressTo

        binding.textClock.text = state.minTimeMinutes?.let { formatMinutes(it) } ?: "Выберите точки"

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

    private fun drawRoute(routePoints: List<DomainPoint>) {
        val map = binding.map
        clearRoute()
        if (routePoints.isEmpty()) return

        val geoPoints = routePoints.map { GeoPoint(it.lat, it.lon) }

        routePolyline = Polyline(map).apply {
            setPoints(geoPoints)

            outlinePaint.color = ContextCompat.getColor(this@GenerateActivity, R.color.route_color)

            outlinePaint.strokeWidth = 14f

            outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
            outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND

            outlinePaint.isAntiAlias = true
        }

        map.overlays.add(routePolyline)

        map.post {
            if (!isFinishing && !isDestroyed && geoPoints.isNotEmpty()) {
                val boundingBox = BoundingBox.fromGeoPoints(geoPoints)
                map.zoomToBoundingBox(boundingBox, true, 100)
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
        Log.w(NAME_ACTIVITY, "Destroy")
        super.onDestroy()
        binding.map.onDetach()
    }

    override fun onResume() {
        Log.w(NAME_ACTIVITY, "Resume")
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        Log.w(NAME_ACTIVITY, "Pause")
        super.onPause()
        binding.map.onPause()
    }

    private fun showTimePicker() {
        val currentMin = viewModel.uiState.value.minTimeMinutes ?: 0
        TimePickerDialog(this, { _, hour, minute ->
            viewModel.onTimeSelected(hour * 60 + minute)
        }, currentMin / 60, currentMin % 60, true).show()
    }

    private fun handleClockClick() {
        val state = viewModel.uiState.value
        if (state.isLoading) return

        when {
            state.isTimePickerEnabled -> showTimePicker()
            else -> showValidationError(state)
        }
    }

    private fun handleCalculateClick() {
        val state = viewModel.uiState.value
        if (state.isLoading) return
        
        when {
            state.isCalculateEnabled -> viewModel.onCalculateClicked()
            else -> showValidationError(state)
        }
    }

    private fun showValidationError(state: GenerateUiState) {
        val message = if (state.pointTo == null || state.pointFrom == null) {
            MSG_WARN
        } else {
            MSG_WHAT
        }
        ToastManager(this).showToast(message)
    }
}