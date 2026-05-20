package sfedu.ictis.walkOfInterest.presentation.generate

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private lateinit var behavior: BottomSheetBehavior<View>

    private var markerFrom: Marker? = null
    private var markerTo: Marker? = null
    private var routePolyline: Polyline? = null
    private var isMapReady = false

    // Защита от петли state -> UI -> state
    private var isUpdatingSeekBarFromState = false

    override fun inflateBinding(): ActivityGenerateBinding {
        return ActivityGenerateBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        setupBottomSheet()
        setupMap()
        setupListeners()
        observeState()
        observeEvents()
    }



    private fun setupBottomSheet() {
        behavior = BottomSheetBehavior.from(binding.bottomSheet)

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) = Unit

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                updateMapPadding(slideOffset)
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomSheet) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }
    }

    private fun updateMapPadding(slideOffset: Float) {
        val peekHeight = behavior.peekHeight
        val fullHeight = binding.bottomSheet.height
        if (fullHeight == 0) return

        val currentHeight = (peekHeight + (fullHeight - peekHeight) * slideOffset.coerceIn(0f, 1f)).toInt()
        val extraMargin = resources.getDimensionPixelSize(R.dimen.padding_title)

        binding.map.setPadding(0, 0, 0, currentHeight + extraMargin)
    }



    private fun setupMap() {
        val map = binding.map
        map.setMultiTouchControls(true)

        val start = viewModel.defaultCenter
        map.controller.setZoom(15.0)
        map.controller.setCenter(GeoPoint(start.lat, start.lon))

        map.addOnFirstLayoutListener { _, _, _, _, _ ->
            isMapReady = true

            binding.map.setPadding(0, 0, 0, behavior.peekHeight)
            drawCurrentState(viewModel.uiState.value)
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
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.fieldTo.setOnClickListener {
            viewModel.onSelectToClicked()
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.fieldClock.setOnClickListener {
            viewModel.onClockClicked()
        }

        binding.fieldBtnCalculate.setOnClickListener {
            viewModel.onCalculateClicked()
        }

        binding.seekBarPoi.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser || isUpdatingSeekBarFromState) return

                val count = progress + 1

                binding.textPoiCount.text = count.toString()
                viewModel.onPoiCountSelected(count)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) = Unit
            override fun onStopTrackingTouch(sb: SeekBar?) = Unit
        })
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUiText(state)

                    if (isMapReady) {
                        drawCurrentState(state)
                    }

                    updateSliderUi(state)
                }
            }
        }
    }

    private fun updateSliderUi(state: GenerateUiState) {
        val sliderEnabled = state.isTimePickerEnabled && state.maxPoiLimit > 0

        binding.sliderBlock.alpha = if (sliderEnabled) 1f else 0.4f
        binding.seekBarPoi.isEnabled = sliderEnabled

        if (!sliderEnabled) {
            binding.textPoiCount.text = "-"
            binding.textPoiMax.text = "-"
            return
        }

        val newMax = (state.maxPoiLimit - 1).coerceAtLeast(0)
        val newProgress = (state.selectedPoiCount - 1).coerceIn(0, newMax)

        isUpdatingSeekBarFromState = true
        if (binding.seekBarPoi.max != newMax) {
            binding.seekBarPoi.max = newMax
        }
        if (binding.seekBarPoi.progress != newProgress) {
            binding.seekBarPoi.progress = newProgress
        }
        isUpdatingSeekBarFromState = false

        binding.textPoiCount.text = state.selectedPoiCount.toString()
        binding.textPoiMax.text = state.maxPoiLimit.toString()
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        GenerateEvent.ExpandBottomSheet -> {
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED

                            binding.map.postDelayed({
                                val state = viewModel.uiState.value
                                if (state.route != null) {
                                    drawRoute(state.route)
                                }
                            }, 300)
                        }

                        GenerateEvent.OpenTimePicker -> showTimePicker()

                        is GenerateEvent.ShowError ->
                            ToastManager.show(this@GenerateActivity, event.message)

                        is GenerateEvent.NavigateToCategories -> {
                            val state = viewModel.uiState.value
                            val from = state.pointFrom ?: return@collect
                            val to = state.pointTo ?: return@collect

                            val intent = Intent(
                                this@GenerateActivity,
                                CategoriesActivity::class.java
                            ).apply {
                                putExtra(CategoriesActivity.EXTRA_ADDRESS_FROM, state.addressFrom)
                                putExtra(CategoriesActivity.EXTRA_ADDRESS_TO, state.addressTo)
                                putExtra(CategoriesActivity.EXTRA_TIME_SELECTED, state.selectedTimeMinutes)
                                putExtra(CategoriesActivity.EXTRA_FROM, from)
                                putExtra(CategoriesActivity.EXTRA_TO, to)
                                putParcelableArrayListExtra(
                                    CategoriesActivity.EXTRA_CATEGORIES,
                                    ArrayList(event.categories)
                                )
                            }
                            startActivity(intent)
                        }
                    }
                }
            }
        }
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
        binding.btnCalculate.isEnabled = state.isCalculateEnabled && !state.isLoading
        binding.btnCalculate.alpha = if (state.isCalculateEnabled && !state.isLoading) 1.0f else 0.5f
        binding.btnCalculateText.text = if (state.isLoading) "Загрузка..." else "Рассчитать"
    }

    private fun drawCurrentState(state: GenerateUiState) {
        updateMapMarkers(state.pointFrom, state.pointTo)
        if (state.route != null) drawRoute(state.route) else clearRoute()
    }

    private fun updateMapMarkers(from: DomainPoint?, to: DomainPoint?) {
        val map = binding.map

        markerFrom?.let { map.overlays.remove(it) }
        markerTo?.let { map.overlays.remove(it) }
        val color = ContextCompat.getColor(this@GenerateActivity, R.color.from_to)

        from?.let {
            markerFrom = Marker(map).apply {
                position = GeoPoint(it.lat, it.lon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(this@GenerateActivity, R.drawable.ic_a)
                    ?.mutate()?.apply {
                        setTint(color)
                    }
            }
            map.overlays.add(markerFrom)
        }

        to?.let {
            markerTo = Marker(map).apply {
                position = GeoPoint(it.lat, it.lon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(this@GenerateActivity, R.drawable.ic_b)
                    ?.mutate()?.apply {
                        setTint(color)
                    }
            }
            map.overlays.add(markerTo)
        }

        map.invalidate()
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
                val originalBox = BoundingBox.fromGeoPoints(geoPoints)

                val sheetHeight = binding.bottomSheet.height.toDouble()
                val mapHeight = map.height.toDouble()

                if (mapHeight > 0 && sheetHeight > 0) {
                    val visibleHeightRatio = (mapHeight - sheetHeight) / mapHeight

                    if (visibleHeightRatio > 0) {
                        val currentLatSpan = originalBox.latitudeSpan
                        val newLatSpan = currentLatSpan / visibleHeightRatio

                        val paddedBox = BoundingBox(
                            originalBox.latNorth,
                            originalBox.lonEast,
                            originalBox.latNorth - newLatSpan,
                            originalBox.lonWest
                        )

                        map.zoomToBoundingBox(paddedBox, true, 200)
                    }
                } else {
                    map.zoomToBoundingBox(originalBox, true, 200)
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

    private fun showTimePicker() {
        val currentTime = viewModel.uiState.value.selectedTimeMinutes
        TimePickerDialog(this, { _, hour, minute ->
            viewModel.onTimeSelected(hour * 60 + minute)
        }, currentTime / 60, currentTime % 60, true).show()
    }



    override fun onDestroy() {
        Log.i(localClassName, "onDestroy")
        super.onDestroy()
        binding.map.onDetach()
    }

    override fun onResume() {
        Log.i(localClassName, "onResume")
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        Log.i(localClassName, "onPause")
        super.onPause()
        binding.map.onPause()
    }
}