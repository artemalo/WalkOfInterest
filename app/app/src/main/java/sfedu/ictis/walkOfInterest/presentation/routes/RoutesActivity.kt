package sfedu.ictis.walkOfInterest.presentation.routes

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.infowindow.InfoWindow
import sfedu.ictis.walkOfInterest.databinding.ActivityRoutesBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.RoutePoint
import sfedu.ictis.walkOfInterest.utils.ToastManager
import sfedu.ictis.walkOfInterest.utils.formatMinutes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.osmdroid.util.BoundingBox
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.presentation.BaseActivity

class RoutesActivity : BaseActivity<ActivityRoutesBinding>() {
    private val viewModel: RoutesViewModel by viewModel()
    private lateinit var adapter: RoutesAdapter

    private lateinit var behavior: BottomSheetBehavior<View>

    private var markerFrom: Marker? = null
    private var markerTo: Marker? = null
    private var poiMarkers: List<Marker> = emptyList()
    private var routePolyline: Polyline? = null
    private var isMapReady = false
    private var wasCentered = false

    override fun inflateBinding(): ActivityRoutesBinding {
        return ActivityRoutesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        binding = ActivityRoutesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupMap()
        setupListeners()
        setupBottomSheet()
        observeState()
        observeEvents()
    }

    private fun setupRecyclerView() {
        adapter = RoutesAdapter(
            onRouteClicked = { viewModel.selectRoute(it) },
            onAboutClicked = { /* TODO переход к редактированию */ }
        )
        binding.itemList.layoutManager = LinearLayoutManager(this)
        binding.itemList.adapter = adapter
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
            viewModel.uiState.value.trip?.let { trip ->
                centerMapOnce(trip.from, trip.to)
            }
        }
    }

    private fun setupListeners() {
        binding.fieldBtnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupBottomSheet() {
        behavior = BottomSheetBehavior.from(binding.bottomSheet)

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Сообщаем ViewModel, что состояние изменилось (например, юзер потянул рукой)
                viewModel.onBottomSheetStateChanged(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Живая анимация: отодвигаем карту или меняем прозрачность
                // slideOffset: 0.0 (свернуто) до 1.0 (развернуто)
                updateMapPadding(slideOffset)
            }
        })
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.map { it.bottomSheetState }
                        .distinctUntilChanged()
                        .collect { state ->
                            if (state != BottomSheetBehavior.STATE_DRAGGING &&
                                state != BottomSheetBehavior.STATE_SETTLING) {
                                behavior.state = state
                            }
                        }
                }

                launch {
                    viewModel.uiState
                        .map { it.trip }
                        .distinctUntilChanged()
                        .collect { trip ->
                            trip?.let {
                                binding.userTime.text = formatMinutes(it.totalTime)
                                updateMapMarkers(it.selectedPois, it.from, it.to)
                            }
                        }
                }

                launch {
                    viewModel.uiState
                        .map { it.route }
                        .distinctUntilChanged()
                        .collect { routePoints ->
                            drawRoute(routePoints)
                        }
                }

                launch {
                    viewModel.uiState.collect { state ->
                        adapter.submitList(state.routes)

                        if (state.isLoading) {
                            binding.textEmpty.visibility = View.GONE
                            binding.itemList.visibility = View.GONE
                        } else {
                            if (state.routes.isEmpty() && state.trip != null) {
                                binding.textEmpty.visibility = View.VISIBLE
                                binding.itemList.visibility = View.GONE
                            } else {
                                binding.textEmpty.visibility = View.GONE
                                binding.itemList.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is RoutesEvent.ShowError -> {
                            ToastManager.show(this@RoutesActivity, event.message, Toast.LENGTH_LONG)
                        }
                        RoutesEvent.CollapseBottomSheet -> {
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }
            }
        }
    }

    private fun updateMapMarkers(points: List<RoutePoint>, from: DomainPoint, to: DomainPoint) {
        val map = binding.map

        markerFrom?.let { map.overlays.remove(it) }
        markerTo?.let { map.overlays.remove(it) }
        poiMarkers.forEach { map.overlays.remove(it) }

        markerFrom = Marker(map).apply {
            position = GeoPoint(from.lat, from.lon)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(this@RoutesActivity, R.drawable.ic_a)
        }
        map.overlays.add(markerFrom)
        markerTo = Marker(map).apply {
            position = GeoPoint(to.lat, to.lon)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(this@RoutesActivity, R.drawable.ic_b)
        }
        map.overlays.add(markerTo)

        val newPoiMarkers = mutableListOf<Marker>()
        points.forEach { point ->
            val marker = Marker(map).apply {
                position = GeoPoint(point.lat, point.lon)
                title = """
                    |${point.name}
                    |Категория: ${point.nameCat}
                    |Подкатегория: ${point.nameSubcat}
                    """.trimMargin()


                val newIcon = this.icon?.constantState?.newDrawable()?.mutate()
                newIcon?.setTint(calculateColorByCategory(point.categoryId))
                this.icon = newIcon
            }
            map.overlays.add(marker)
            newPoiMarkers.add(marker)
        }
        poiMarkers = newPoiMarkers

        if (map.overlays.none { it is MapEventsOverlay }) {
            val mapEventsReceiver = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    InfoWindow.closeAllInfoWindowsOn(map)
                    return true
                }
                override fun longPressHelper(p: GeoPoint?): Boolean = false
            }
            map.overlays.add(0, MapEventsOverlay(mapEventsReceiver))
        }

        if (isMapReady && !wasCentered) {
            centerMapOnce(from, to)
            wasCentered = true
        }

        map.invalidate()
    }

    private fun drawRoute(points: List<DomainPoint>) {
        val map = binding.map

        routePolyline?.let { map.overlays.remove(it) }

        if (points.isEmpty()) {
            map.invalidate()
            return
        }

        val geoPoints = points.map { GeoPoint(it.lat, it.lon) }

        routePolyline = Polyline().apply {
            setPoints(geoPoints)
            outlinePaint.color = ContextCompat.getColor(this@RoutesActivity, R.color.route_color)
            outlinePaint.strokeWidth = 14f
            outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
            outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
            outlinePaint.isAntiAlias = true
        }

        map.overlays.add(0, routePolyline) // под маркер

        map.post {
            if (!isFinishing && !isDestroyed && geoPoints.isNotEmpty()) {
                val boundingBox = BoundingBox.fromGeoPoints(geoPoints)
                map.zoomToBoundingBox(boundingBox, true, 100)
            }
        }

        map.invalidate()
    }

    /**
     * Формула генерации цвета на основе ID.
     * Если 0 -> Серый. Иначе раскидываем по цветовому кругу (Hue от 0 до 360).
     */
    private fun calculateColorByCategory(categoryId: Int): Int {
        Log.i("color category", "$categoryId")
        if (categoryId == 0) return Color.GRAY

        // Сдвигаем цвет на 45 градусов по кругу для каждой новой категории
        val hue = (categoryId * 45f) % 360f
        val hsv = floatArrayOf(hue, 0.8f, 0.9f)

        return Color.HSVToColor(hsv)
    }

    private fun centerMapOnce(from: DomainPoint, to: DomainPoint) {
        if (isMapReady) {
            val center = GeoPoint((from.lat + to.lat) / 2.0, (from.lon + to.lon) / 2.0)

            binding.map.post {
                binding.map.controller.setZoom(14.5)
                binding.map.controller.animateTo(center)
                Log.i("centerMapOnce", "${center.latitude}, ${center.longitude}")
            }
        }
    }

    private fun updateMapPadding(slideOffset: Float) {
        val peekHeight = behavior.peekHeight
        val fullHeight = binding.bottomSheet.height
        val currentHeight = peekHeight + (fullHeight - peekHeight) * slideOffset

        binding.map.setPadding(0, 0, 0, currentHeight.toInt())
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
}