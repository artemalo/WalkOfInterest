package sfedu.ictis.walkOfInterest.presentation.generate

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ActivityGenerateBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.presentation.BaseActivity
import sfedu.ictis.walkOfInterest.presentation.categories.CategoriesActivity
import sfedu.ictis.walkOfInterest.utils.ToastManager
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class GenerateActivity : BaseActivity<ActivityGenerateBinding>() {
    private val viewModel: GenerateViewModel by viewModel()
    private var markerFrom: Marker? = null
    private var markerTo: Marker? = null
    private var routePolyline: Polyline? = null
    private var isMapReady = false

    override fun inflateBinding(): ActivityGenerateBinding {
        return ActivityGenerateBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        setupMap()
        setupListeners()
        observeState()
        observeEvents()
    }

    private fun setupMap() {
        val map = binding.map
        map.setMultiTouchControls(true)

        val start = viewModel.defaultCenter
        val startGeoPoint = GeoPoint(start.lat, start.lon)
        map.controller.setZoom(15.0)
        map.controller.setCenter(startGeoPoint)

        map.addOnFirstLayoutListener { _, _, _, _, _ ->
            isMapReady = true

            lifecycleScope.launch {
                drawCurrentState(viewModel.uiState.value)
            }
        }

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (p != null) {
                    viewModel.onMapPointClicked(p.latitude, p.longitude)
                    return true
                }
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        }
        map.overlays.add(MapEventsOverlay(mapEventsReceiver))
    }

    private fun setupListeners() {
        binding.fieldBtnBack.setOnClickListener {
            viewModel.onBackClicked()
            onBackPressedDispatcher.onBackPressed()
        }

        binding.fieldFrom.setOnClickListener {
            viewModel.onSelectFromClicked()
        }

        binding.fieldTo.setOnClickListener {
            viewModel.onSelectToClicked()
        }

        binding.fieldClock.setOnClickListener {
            viewModel.onClockClicked()
        }

        binding.fieldBtnCalculate.setOnClickListener {
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

                    binding.sliderBlock.alpha = if (state.isTimePickerEnabled) 1f else 0.4f
                    binding.seekBarPoi.isEnabled = state.isTimePickerEnabled

                    binding.seekBarPoi.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                            // TODO: if (fromUser) viewModel.onPoiCountChanged(progress)
                        }
                        override fun onStartTrackingTouch(sb: SeekBar?) = Unit
                        override fun onStopTrackingTouch(sb: SeekBar?) = Unit
                    })
                }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        GenerateEvent.OpenTimePicker -> showTimePicker()

                        is GenerateEvent.ShowError ->
                            ToastManager.show(this@GenerateActivity, event.message)

                        is GenerateEvent.NavigateToCategories -> {
                            val state = viewModel.uiState.value

                            val from = state.pointFrom ?: return@collect
                            val to = state.pointTo ?: return@collect

                            val intent = android.content.Intent(this@GenerateActivity, CategoriesActivity::class.java).apply {
                                putExtra(CategoriesActivity.EXTRA_ADDRESS_FROM, state.addressFrom)
                                putExtra(CategoriesActivity.EXTRA_ADDRESS_TO, state.addressTo)
                                putExtra(CategoriesActivity.EXTRA_TIME_SELECTED, state.selectedTimeMinutes)

                                putExtra(CategoriesActivity.EXTRA_FROM, from)
                                putExtra(CategoriesActivity.EXTRA_TO, to)

                                // TODO: исправить >1мб данные (FileCache, проще Clean Architecture Way - Кэширование в Репозитории)
                                putParcelableArrayListExtra(CategoriesActivity.EXTRA_CATEGORIES, ArrayList(event.categories))
                            }

                            startActivity(intent)
                        }
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

        binding.textClock.text = if (state.selectedTimeMinutes > 0) {
            formatMinutes(state.selectedTimeMinutes)
        } else {
            "Выберите точки"
        }

        binding.fieldClock.alpha = if (state.isTimePickerEnabled) 1.0f else 0.5f
        binding.btnCalculate.isEnabled = state.isCalculateEnabled
        binding.btnCalculate.isEnabled = !state.isLoading
        binding.btnCalculate.alpha = if (state.isCalculateEnabled) 1.0f else 0.5f

        binding.btnCalculateText.text =
            if (state.isLoading) "Загрузка..." else "Рассчитать"
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
        Log.i(this.localClassName, "onDestroy")

        super.onDestroy()
        binding.map.onDetach()
    }

    override fun onResume() {
        Log.i(this.localClassName, "onResume")

        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        Log.i(this.localClassName, "onPause")

        super.onPause()
        binding.map.onPause()
    }

    private fun showTimePicker() {
        val currentTime = viewModel.uiState.value.selectedTimeMinutes

        TimePickerDialog(this, { _, hour, minute ->
            viewModel.onTimeSelected(hour * 60 + minute)
        }, currentTime / 60, currentTime % 60, true).show()
    }
}