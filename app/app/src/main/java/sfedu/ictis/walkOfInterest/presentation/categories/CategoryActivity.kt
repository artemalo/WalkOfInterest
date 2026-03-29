package sfedu.ictis.walkOfInterest.presentation.categories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.R

class CategoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        recyclerView = findViewById(R.id.rvCategories)

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val spacing = resources.getDimensionPixelSize(R.dimen.padding_small)

        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = 2,
                spacing = spacing,
                includeEdge = true
            )
        )

        // adapter потом подключишь
        // recyclerView.adapter = CategoryAdapter(...)
    }
}