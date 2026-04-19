package sfedu.ictis.walkOfInterest.presentation.routes

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import org.osmdroid.views.overlay.infowindow.InfoWindow
import sfedu.ictis.walkOfInterest.databinding.ActivityRoutesBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.RoutePoint
import sfedu.ictis.walkOfInterest.presentation.generate.GenerateUiState
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class RoutesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoutesBinding
    private val viewModel: RoutesViewModel by viewModel()
    private lateinit var adapter: RoutesAdapter

    private var isMapReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        binding = ActivityRoutesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupMap()
        setupListeners()
        observeState()

        // TODO: Получить отфильтрованные данные (isSelect == true) из кэша/репозитория
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
    }

    private fun setupListeners() {
        binding.fieldBtnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.trip?.let { trip ->
                    binding.userTime.text = formatMinutes(trip.totalTime)

                    updateMapMarkers(state.mapPoints, trip.from, trip.to)
                }

                adapter.submitList(state.routes)
            }
        }
    }

    private fun updateMapMarkers(points: List<RoutePoint>, from: DomainPoint, to: DomainPoint) {
        binding.map.overlays.clear()

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                // Закрываем все открытые title (InfoWindow)
                InfoWindow.closeAllInfoWindowsOn(binding.map)
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        binding.map.overlays.add(MapEventsOverlay(mapEventsReceiver))

        points.forEach { point ->
            val marker = Marker(binding.map).apply {
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
            binding.map.overlays.add(marker)
        }

        if (!isMapReady) {
            val center = GeoPoint((from.lat + to.lat) / 2.0, (from.lon + to.lon) / 2.0)
            binding.map.controller.setCenter(center)
            binding.map.controller.setZoom(14.5)
            isMapReady = true
        }

        binding.map.invalidate()
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
        val hsv = floatArrayOf(hue, 0.8f, 0.9f) // hue, saturation, value

        return Color.HSVToColor(hsv)
    }
}