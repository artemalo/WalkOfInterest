package sfedu.ictis.walkOfInterest.presentation.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ActivityMainBinding
import sfedu.ictis.walkOfInterest.presentation.details.TripDetailsFragment
import sfedu.ictis.walkOfInterest.utils.ToastManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainFeedViewModel by viewModel()
    private val feedAdapter = FeedAdapter { tripId ->
        openTripDetails(tripId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
        observeState()
    }

    private fun openTripDetails(tripId: String) {
        val fragment = TripDetailsFragment.newInstance(tripId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupUI() {
         binding.itemList.adapter = feedAdapter
         binding.itemList.layoutManager = LinearLayoutManager(this)

        // Добавляем фрагмент в контейнер (если его там еще нет)
        if (supportFragmentManager.findFragmentById(R.id.fragmentMainCreate) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentMainCreate, MainCreateFragment())
                .commit()
        }
    }

    private fun setupListeners() {
        binding.fieldBtnTrips.setOnClickListener {
            viewModel.onTabClicked(MainTab.TRIPS)
        }

        binding.fieldBtnSpots.setOnClickListener {
            viewModel.onTabClicked(MainTab.SPOTS)
        }

        binding.fieldBtnPlus.setOnClickListener {
            viewModel.onPlusClicked()
        }

        binding.fieldBtnBack.setOnClickListener {
            if (viewModel.uiState.value.isCreateMenuVisible) {
                viewModel.hideCreateMenu()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.fieldBtnProfile.setOnClickListener {
            ToastManager.show(this, "Профиль в разработке")
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.fieldBtnTrips.isSelected = (state.selectedTab == MainTab.TRIPS)
                    binding.fieldBtnSpots.isSelected = (state.selectedTab == MainTab.SPOTS)
                    binding.fieldBtnPlus.isSelected = state.isCreateMenuVisible

                    binding.fragmentMainCreate.visibility =
                        if (state.isCreateMenuVisible) View.VISIBLE else View.GONE

                    binding.titleList.text = when (state.selectedTab) {
                        MainTab.TRIPS -> "Маршруты"
                        MainTab.SPOTS -> "Интересные места"
                    }

                    feedAdapter.submitList(state.items)
                }
            }
        }
    }



    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }
}