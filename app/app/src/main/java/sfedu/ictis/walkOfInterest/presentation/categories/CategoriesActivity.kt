package sfedu.ictis.walkOfInterest.presentation.categories

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import sfedu.ictis.walkOfInterest.databinding.ActivityCategoriesBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainCategory

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
    }

    private fun setupUI() {
        val addressFrom = intent.getStringExtra(EXTRA_ADDRESS_FROM) ?: "Откуда"
        val addressTo = intent.getStringExtra(EXTRA_ADDRESS_TO) ?: "Куда"

        // (безопасное извлечение для новых версий)
        val categories = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(EXTRA_CATEGORIES, DomainCategory::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(EXTRA_CATEGORIES)
        }

        binding.textFrom.text = addressFrom
        binding.textTo.text = addressTo

        if (categories != null) {
            adapter.submitList(categories)
        } else {
            Toast.makeText(this, "Категории не найдены", Toast.LENGTH_SHORT).show()
        }
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

    companion object {
        const val EXTRA_ADDRESS_FROM = "extra_address_from"
        const val EXTRA_ADDRESS_TO = "extra_address_to"
        const val EXTRA_CATEGORIES = "extra_categories"

        // requestId
    }
}