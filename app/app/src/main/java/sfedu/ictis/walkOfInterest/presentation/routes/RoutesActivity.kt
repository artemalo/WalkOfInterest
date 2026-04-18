package sfedu.ictis.walkOfInterest.presentation.routes

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import sfedu.ictis.walkOfInterest.databinding.ActivityRoutesBinding
import sfedu.ictis.walkOfInterest.domain.model.RoutePoint
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class RoutesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoutesBinding
    private val viewModel: RoutesViewModel by viewModel()
    private lateinit var adapter: RoutesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        binding = ActivityRoutesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeState()

        // TODO: Получить отфильтрованные данные (isSelect == true) из кэша/репозитория
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.trip?.let { trip ->
                    binding.userTime.text = formatMinutes(trip.totalTime)

                    updateMapMarkers(state.mapPoints, trip.from.lat, trip.from.lon, trip.to.lat, trip.to.lon)
                }

                adapter.submitList(state.routes)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = RoutesAdapter(
            onRouteClicked = { viewModel.selectRoute(it) },
            onAboutClicked = { /* TODO переход к редактированию */ }
        )
        binding.itemList.layoutManager = LinearLayoutManager(this)
        binding.itemList.adapter = adapter
    }

    private fun setupListeners() {
        binding.fieldBtnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun calculateColor(id: Int): Int {
        if (id == 0) return Color.GRAY
        return Color.HSVToColor(floatArrayOf((id * 45f) % 360f, 0.8f, 0.9f))
    }

    private fun updateMapMarkers(points: List<RoutePoint>, latF: Double, lonF: Double, latT: Double, lonT: Double) {
        binding.map.overlays.clear()

        points.forEach { point ->
            val marker = Marker(binding.map).apply {
                position = GeoPoint(point.lat, point.lon)
                title = "Point ${point.id}"
                icon.setTint(calculateColor(point.categoryId))
            }
            binding.map.overlays.add(marker)
        }

        val center = GeoPoint((latF + latT) / 2.0, (lonF + lonT) / 2.0)
        binding.map.controller.setCenter(center)
        binding.map.controller.setZoom(14.5)

        binding.map.invalidate()
    }

    /**
     * Формула генерации цвета на основе ID.
     * Если 0 -> Серый. Иначе раскидываем по цветовому кругу (Hue от 0 до 360).
     */
    private fun calculateColorByCategory(categoryId: Int): Int {
        if (categoryId == 0) return Color.GRAY

        // Сдвигаем цвет на 45 градусов по кругу для каждой новой категории
        val hue = (categoryId * 45f) % 360f
        val hsv = floatArrayOf(hue, 0.8f, 0.9f) // hue, saturation, value

        return Color.HSVToColor(hsv)
    }

    companion object {
        const val EXTRA_USER_TIME = "extra_user_time"
    }
}