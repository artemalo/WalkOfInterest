package sfedu.ictis.walkOfInterest.presentation.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ActivityMainBinding
import sfedu.ictis.walkOfInterest.presentation.BaseActivity
import sfedu.ictis.walkOfInterest.presentation.details.TripDetailsFragment
import sfedu.ictis.walkOfInterest.presentation.profile.ProfileActivity
import sfedu.ictis.walkOfInterest.utils.openPoiFragment

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel: MainFeedViewModel by viewModel()
    private val feedAdapter = FeedAdapter(
        onTripClicked = { tripId -> openTripDetails(tripId) },
        onSpotClicked = { poiId ->
            if (poiId > 0) {
                openPoiFragment(poiId, binding.fragmentContainer)
            }
        }
    )

    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
        setupListeners()
        observeState()
        onBackStackChangedListener()
    }

    private fun onBackStackChangedListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            val isFragmentVisible = supportFragmentManager.backStackEntryCount > 0
            if (!isFragmentVisible) {
                binding.fragmentContainer.visibility = View.GONE
                viewModel.refreshData()
            }
        }
    }
    private fun openTripDetails(tripId: String) {
        binding.fragmentContainer.visibility = View.VISIBLE

        val fragment = TripDetailsFragment.newInstance(tripId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupUI() {
        binding.itemList.adapter = feedAdapter
        binding.itemList.layoutManager = LinearLayoutManager(this)

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

        binding.fieldBtnProfile.setOnClickListener {
            val intent = android.content.Intent(this, ProfileActivity::class.java)
            startActivity(intent)
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
                        MainTab.SPOTS -> "Сохраненные места"
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