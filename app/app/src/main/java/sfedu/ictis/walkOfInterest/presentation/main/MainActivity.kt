package sfedu.ictis.walkOfInterest.presentation.main

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ActivityMainBinding
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepositoryImpl
import sfedu.ictis.walkOfInterest.infrastructure.network.NetworkModule

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // Заглушка для ViewModel, убедись, что используешь свою Factory из прошлого шага
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(RouteRepositoryImpl(NetworkModule.routeApi))
    }

    // Флаг: true - выбираем откуда (p1), false - куда (p2), null - ничего не выбираем
    private var isSelectingFrom: Boolean? = null

    // Храним маркеры, чтобы удалять старые при перевыборе точки
    private var markerFrom: Marker? = null
    private var markerTo: Marker? = null

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
        map.setMultiTouchControls(true) // Включаем зум щипком

        // Ставим стартовую точку (например, центр Ростова-на-Дону)
        val startPoint = GeoPoint(47.2220, 39.7190)
        map.controller.setZoom(15.0)
        map.controller.setCenter(startPoint)

        // Добавляем слушатель кликов по карте
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
        val selectingFrom = isSelectingFrom ?: return // Если null, ничего не делаем

        // Имитация геокодинга: в реальном приложении тут будет запрос к Nominatim API за адресом
        val mockAddress = "${geoPoint.latitude.toString().take(6)}, ${geoPoint.longitude.toString().take(6)}"

        // Передаем данные во ViewModel
        viewModel.onPointSelected(selectingFrom, geoPoint.latitude, geoPoint.longitude, mockAddress)

        // Отрисовываем маркер
        addMarker(geoPoint, selectingFrom)

        // Сбрасываем флаг выбора
        isSelectingFrom = null
    }

    private fun addMarker(geoPoint: GeoPoint, isFrom: Boolean) {
        val map = binding.map

        // Удаляем старый маркер, если он был
        if (isFrom) {
            markerFrom?.let { map.overlays.remove(it) }
        } else {
            markerTo?.let { map.overlays.remove(it) }
        }

        val marker = Marker(map).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = if (isFrom) "Откуда" else "Куда"
            // Тут можно задать кастомную иконку:
            val idMarker = if (isFrom) R.drawable.ic_a else R.drawable.ic_b
            icon = ContextCompat.getDrawable(this@MainActivity, idMarker)
        }

        if (isFrom) markerFrom = marker else markerTo = marker
        map.overlays.add(marker)
        map.invalidate() // Заставляем карту перерисоваться
    }

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
            viewModel.uiState.collect { state ->
                binding.textFrom.text = state.addressFrom
                binding.textTo.text = state.addressTo

                // Чтобы alpha работала корректно, кнопки нужно приглушать, если они недоступны
                binding.fieldClock.alpha = if (state.isTimePickerEnabled) 1.0f else 0.5f
                binding.btnCalculate.isEnabled = state.isCalculateEnabled
                binding.btnCalculate.alpha = if (state.isCalculateEnabled) 1.0f else 0.5f

                // Включаем лоадер (вместо alpha лучше потом добавить ProgressBar)
                binding.btnCalculateText.text = if (state.isLoading) "Загрузка..." else "Рассчитать"
            }
        }
    }

    // Жизненный цикл OSMDroid (ОБЯЗАТЕЛЬНО для работы карты!)
    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    private fun showTimePicker() {
        val currentMin = viewModel.uiState.value.minTimeMinutes ?: 0
        TimePickerDialog(this, { _, hour, minute ->
            viewModel.onTimeSelected(hour * 60 + minute)
        }, currentMin / 60, currentMin % 60, true).show()
    }

//    private fun formatMinutes(totalMinutes: Int): String {
//        val h = totalMinutes / 60
//        val m = totalMinutes % 60
//        return "${h}ч ${m}м"
//    }
}