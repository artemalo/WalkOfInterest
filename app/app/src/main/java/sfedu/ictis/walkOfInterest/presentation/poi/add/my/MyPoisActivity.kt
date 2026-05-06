package sfedu.ictis.walkOfInterest.presentation.poi.add.my

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ActivityMyPoisBinding
import sfedu.ictis.walkOfInterest.domain.model.PoiStatus
import sfedu.ictis.walkOfInterest.presentation.BaseActivity
import sfedu.ictis.walkOfInterest.presentation.poi.PoiFragment

class MyPoisActivity : BaseActivity<ActivityMyPoisBinding>() {
    private val viewModel: MyPoisViewModel by viewModel()

    private val adapter by lazy {
        MyPoisAdapter(onClicked = { item -> openPoi(item.id) })
    }

    override fun inflateBinding(): ActivityMyPoisBinding =
        ActivityMyPoisBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTabs()
        setupRecycler()
        setupListeners()
        observeState()
    }

    private fun setupTabs() {
        listOf(
            "На модерации" to PoiStatus.PENDING,
            "Опубликовано" to PoiStatus.APPROVED,
            "Отклонено" to PoiStatus.REJECTED
        ).forEach { (label, status) ->
            val tab = binding.tabs.newTab().setText(label).apply { tag = status }
            binding.tabs.addTab(tab)
        }
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val status = tab.tag as? PoiStatus ?: return
                viewModel.onTabSelected(status)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
    }

    private fun setupRecycler() {
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter
    }

    private fun setupListeners() {
        binding.fieldBtnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnRetry.setOnClickListener { viewModel.refresh() }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    val visible = state.visible
                    adapter.submitList(visible)

                    val hasError = state.errorMessage != null
                    binding.errorBlock.visibility = if (hasError) View.VISIBLE else View.GONE
                    binding.errorText.text = state.errorMessage.orEmpty()

                    val showEmpty = !state.isLoading && !hasError && visible.isEmpty()
                    binding.empty.visibility = if (showEmpty) View.VISIBLE else View.GONE
                    binding.empty.text = when (state.selectedTab) {
                        PoiStatus.PENDING -> "Нет точек на модерации"
                        PoiStatus.APPROVED -> "Нет опубликованных мест"
                        PoiStatus.REJECTED -> "Нет отклонённых мест"
                    }
                }
            }
        }
    }

    private fun openPoi(id: Long) {
        binding.fragmentContainer.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PoiFragment.newInstance(id))
            .addToBackStack(null)
            .commit()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.fragmentContainer.visibility = View.GONE
            }
        }
    }
}
