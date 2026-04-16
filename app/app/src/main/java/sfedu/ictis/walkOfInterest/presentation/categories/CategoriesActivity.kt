package sfedu.ictis.walkOfInterest.presentation.categories

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ActivityCategoriesBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class CategoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var adapter: CategoriesAdapter
    private val viewModel: CategoriesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupUI()
        setupListeners()
        observeState()
    }

    private fun setupUI() {
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
            from = addressFrom,
            to = addressTo,
            totalTime = totalTime
        )

//        binding.textFrom.text = addressFrom
//        binding.textTo.text = addressTo

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
                // TODO: activity Subcategories
//                val intent = Intent(this, SubcategoriesActivity::class.java).apply {
//                    putExtra("EXTRA_CATEGORY", category)
//                }
//                startActivity(intent)

                Toast.makeText(this, "Переход к ${category.name}", Toast.LENGTH_SHORT).show()
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

    private fun setupListeners() {
        binding.imageButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.fieldBtn.setOnClickListener {
            // TODO логика перехода дальше, когда категории выбраны
            Toast.makeText(this, "Идем дальше!", Toast.LENGTH_SHORT).show()
        }

        binding.btnSwap.setOnClickListener {
            // TODO: sort categories
        }
    }

    companion object {
        const val EXTRA_ADDRESS_FROM = "extra_address_from"
        const val EXTRA_ADDRESS_TO = "extra_address_to"
        const val EXTRA_CATEGORIES = "extra_categories"
        const val EXTRA_TIME_SELECTED = "extra_time_selected"

        // requestId
    }
}