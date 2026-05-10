package sfedu.ictis.walkOfInterest.presentation.poi.add

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ActivityAddPoiBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiNearby
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.presentation.BaseActivity
import sfedu.ictis.walkOfInterest.presentation.poi.PoiFragment
import sfedu.ictis.walkOfInterest.presentation.poi.add.form.AddPoiFormFragment
import sfedu.ictis.walkOfInterest.presentation.poi.add.my.MyPoisActivity
import sfedu.ictis.walkOfInterest.utils.ToastManager

class AddPoiActivity : BaseActivity<ActivityAddPoiBinding>(),
    SimilarPoisBottomSheet.Listener {

    private val viewModel: AddPoiPickViewModel by viewModel()

    private var similarMarkers: List<Marker> = emptyList()
    private var isMapReady = false

    override fun inflateBinding(): ActivityAddPoiBinding =
        ActivityAddPoiBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        setupMap()
        setupListeners()
        observeState()
        observeEvents()

        updateOverlaysVisibility()
        supportFragmentManager.addOnBackStackChangedListener {
            updateOverlaysVisibility()
        }
    }

    private fun setupMap() {
        val map = binding.map
        map.setMultiTouchControls(true)

        val center = viewModel.defaultCenter
        map.controller.setZoom(15.0)
        map.controller.setCenter(GeoPoint(center.lat, center.lon))

        map.addOnFirstLayoutListener { _, _, _, _, _ ->
            isMapReady = true
        }
    }

    private fun setupListeners() {
        binding.fieldBtnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.fieldBtnMyPois.setOnClickListener {
            startActivity(Intent(this, MyPoisActivity::class.java))
        }

        binding.btnConfirmPoint.setOnClickListener {
            val center = binding.map.mapCenter
            viewModel.onConfirmPointClicked(center.latitude, center.longitude)
        }

        binding.btnToggleSimilarSheet.setOnClickListener {
            viewModel.onShowSimilarSheetAgain()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState
                        .map { it.isChecking }
                        .distinctUntilChanged()
                        .collect { checking ->
                            binding.progress.visibility = if (checking) View.VISIBLE else View.GONE
                            binding.btnConfirmPoint.isEnabled = !checking
                            binding.btnConfirmPoint.alpha = if (checking) 0.5f else 1f
                            binding.imgPin.alpha = if (checking) 0.5f else 1f
                        }
                }

                launch {
                    viewModel.uiState
                        .map { it.similarPois }
                        .distinctUntilChanged()
                        .collect { pois ->
                            renderSimilarMarkers(pois)
                        }
                }

                launch {
                    viewModel.uiState
                        .map { it.showSimilarSheet }
                        .distinctUntilChanged()
                        .collect { show ->
                            if (show) showSimilarSheet()
                            updateToggleButtonVisibility()
                        }
                }

                launch {
                    viewModel.uiState
                        .map { it.similarPois.isNotEmpty() }
                        .distinctUntilChanged()
                        .collect {
                            updateToggleButtonVisibility()
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
                        is AddPoiPickEvent.ShowError ->
                            ToastManager.show(this@AddPoiActivity, event.message)

                        is AddPoiPickEvent.OpenForm ->
                            openForm(event.point, event.targetPoiId)

                        is AddPoiPickEvent.OpenExistingPoi ->
                            openExistingPoi(event.poiId)
                    }
                }
            }
        }
    }

    private fun showSimilarSheet() {
        val pois = viewModel.uiState.value.similarPois
        if (pois.isEmpty()) return
        val existing = supportFragmentManager.findFragmentByTag(SimilarPoisBottomSheet.TAG)
        if (existing != null) return

        SimilarPoisBottomSheet.newInstance(pois)
            .show(supportFragmentManager, SimilarPoisBottomSheet.TAG)
    }

    private fun renderSimilarMarkers(pois: List<DomainPoiNearby>) {
        val map = binding.map
        similarMarkers.forEach { map.overlays.remove(it) }

        val newMarkers = pois.mapNotNull { poi ->
            val point = poi.point ?: return@mapNotNull null
            Marker(map).apply {
                position = GeoPoint(point.lat, point.lon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(this@AddPoiActivity, R.drawable.ic_spot_on)
                title = poi.name
            }
        }
        newMarkers.forEach { map.overlays.add(it) }
        similarMarkers = newMarkers
        map.invalidate()
    }

    private fun openForm(point: DomainPoint, targetPoiId: Long?) {
        binding.fragmentContainer.visibility = View.VISIBLE

        val fragment = AddPoiFormFragment.newInstance(point, targetPoiId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(AddPoiFormFragment.BACKSTACK_TAG)
            .commit()
    }

    private fun openExistingPoi(poiId: Long) {
        binding.fragmentContainer.visibility = View.VISIBLE

        val fragment = PoiFragment.newInstance(poiId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateToggleButtonVisibility() {
        val state = viewModel.uiState.value
        val hasSimilarPois = state.similarPois.isNotEmpty()
        val isSheetVisible = state.showSimilarSheet

        binding.btnToggleSimilarSheet.visibility = if (hasSimilarPois && !isSheetVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun updateOverlaysVisibility() {
        val hasFragment = supportFragmentManager.backStackEntryCount > 0
        binding.fragmentContainer.visibility = if (hasFragment) View.VISIBLE else View.GONE
        binding.imgPin.visibility = if (hasFragment) View.GONE else View.VISIBLE
        binding.bottomBar.visibility = if (hasFragment) View.GONE else View.VISIBLE
        binding.fieldBtnMyPois.visibility = if (hasFragment) View.GONE else View.VISIBLE
    }

    override fun onOpenSimilar(poi: DomainPoiNearby) {
        viewModel.onOpenExistingPoi(poi.id)
    }

    override fun onAddInfoToSimilar(poi: DomainPoiNearby) {
        viewModel.onAddInfoToExisting(poi.id)
    }

    override fun onAlwaysCreateNew() {
        viewModel.onAlwaysCreateNew()
    }

    override fun onSimilarSheetDismissed() {
        viewModel.onSimilarSheetDismissed()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDetach()
    }
}