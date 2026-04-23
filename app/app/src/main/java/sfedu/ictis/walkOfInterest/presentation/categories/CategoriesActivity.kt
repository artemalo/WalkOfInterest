package sfedu.ictis.walkOfInterest.presentation.categories

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ActivityCategoriesBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.presentation.BaseActivity
import sfedu.ictis.walkOfInterest.presentation.category.CategoryFragment
import sfedu.ictis.walkOfInterest.presentation.routes.RoutesActivity
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class CategoriesActivity : BaseActivity<ActivityCategoriesBinding>() {
    private lateinit var adapter: CategoriesAdapter
    private val viewModel: CategoriesViewModel by viewModel()

    override fun inflateBinding(): ActivityCategoriesBinding {
        return ActivityCategoriesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupRecyclerView()
        setupUI()
        setupListeners()
        observeState()
        observeEvents()
    }

    private fun setupUI() {
        val from = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_FROM, DomainPoint::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_FROM)
        }

        val to = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TO, DomainPoint::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TO)
        }

        if (from == null || to == null) {
            Toast.makeText(this, "Критично: Точки не найдены", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        val addressFrom = intent.getStringExtra(EXTRA_ADDRESS_FROM) ?: "Откуда"
        val addressTo = intent.getStringExtra(EXTRA_ADDRESS_TO) ?: "Куда"
        val totalTime = intent.getIntExtra(EXTRA_TIME_SELECTED, 0)

        // (безопасное извлечение для новых версий)
        val categories = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(EXTRA_CATEGORIES, DomainCategory::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(EXTRA_CATEGORIES)
        }

        viewModel.initData(
            categories = categories ?: emptyList(),
            addressFrom = addressFrom,
            addressTo = addressTo,
            from = from,
            to = to,
            totalTime = totalTime
        )

        if (categories == null) {
            Toast.makeText(this, "Категории не найдены", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoriesAdapter(
            onItemClicked = { category ->
                viewModel.toggleCategorySelection(category.id)
            },
            onPictureClicked = { category ->
                openCategoryFragment(category)
            }
        )

        binding.itemList.apply {
            layoutManager = LinearLayoutManager(this@CategoriesActivity)
            adapter = this@CategoriesActivity.adapter
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.categories)

                binding.textFrom.text = state.addressFrom
                binding.textTo.text = state.addressTo
                binding.timeTotal.text = formatMinutes(state.totalAvailableTime)
                binding.timeCurrent.text = formatMinutes(state.currentSelectedTime)

                val timeColor = if (state.currentSelectedTime > state.totalAvailableTime) {
                    getColor(R.color.object_red)
                } else {
                    getColor(R.color.white_gray)
                }
                binding.timeCurrent.setTextColor(timeColor)
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    is CategoriesEvent.NavigateToRoutes -> {
                        val intent = Intent(this@CategoriesActivity, RoutesActivity::class.java)
                        startActivity(intent)
                    }
                    is CategoriesEvent.ShowError -> {
                        Toast.makeText(this@CategoriesActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.imageButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.fieldBtn.setOnClickListener {
            viewModel.onGenerateRouteClicked()
        }

        binding.btnSwap.setOnClickListener {
            // TODO: sort categories
        }
    }

    private fun openCategoryFragment(category: DomainCategory) {
        binding.fragmentContainer.visibility = View.VISIBLE

        val fragment = CategoryFragment.newInstance(category)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.fragmentContainer.visibility = View.GONE
            }
        }
    }

    companion object {
        const val EXTRA_FROM = "extra_from"
        const val EXTRA_TO = "extra_to"
        const val EXTRA_ADDRESS_FROM = "extra_address_from"
        const val EXTRA_ADDRESS_TO = "extra_address_to"
        const val EXTRA_CATEGORIES = "extra_categories"
        const val EXTRA_TIME_SELECTED = "extra_time_selected"

        // requestId
    }
}