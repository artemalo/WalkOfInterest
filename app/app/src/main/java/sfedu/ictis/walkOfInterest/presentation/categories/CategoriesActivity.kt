package sfedu.ictis.walkOfInterest.presentation.categories

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import sfedu.ictis.walkOfInterest.data.model.dto.CategoryDto
import sfedu.ictis.walkOfInterest.databinding.ActivityCategoriesBinding

class CategoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var adapter: CategoriesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupUI()
        setupListeners()


        loadMockData()
    }

    private fun setupUI() {
        val addressFrom = intent.getStringExtra(EXTRA_ADDRESS_FROM) ?: "Откуда"
        val addressTo = intent.getStringExtra(EXTRA_ADDRESS_TO) ?: "Куда"

        binding.textFrom.text = addressFrom
        binding.textTo.text = addressTo
    }

    private fun setupRecyclerView() {
        adapter = CategoriesAdapter { selectedCategory ->
            Toast.makeText(this, "Выбрана категория: ${selectedCategory.name}", Toast.LENGTH_SHORT).show()
            // TODO логика выделения/перехода в подкатегории
        }

        binding.itemList.layoutManager = LinearLayoutManager(this)
        binding.itemList.adapter = adapter
    }

    private fun setupListeners() {
        binding.imageButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.fieldBtn.setOnClickListener {
            // TODO логика перехода дальше, когда категории выбраны
            Toast.makeText(this, "Идем дальше!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMockData() {
        val mockCategories = listOf(
            CategoryDto(1, "Парки и скверы", "Зеленые зоны", null, 0, 12, 120),
            CategoryDto(2, "Музеи", "Исторические места", null, 0, 5, 240),
            CategoryDto(3, "Кафе и рестораны", "Где перекусить", null, 0, 20, 60)
        )
        adapter.submitList(mockCategories)
    }

    companion object {
        const val EXTRA_ADDRESS_FROM = "extra_address_from"
        const val EXTRA_ADDRESS_TO = "extra_address_to"

        // requestId или сами категории
    }
}