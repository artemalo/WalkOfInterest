package sfedu.ictis.walkOfInterest.presentation.poi.add.form

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.databinding.ItemPickCategoryBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory

class CategoryPickAdapter(
    private val onCategoryClicked: (DomainPickCategory) -> Unit
) : ListAdapter<CategoryPickAdapter.Item, CategoryPickAdapter.VH>(DIFF) {
    data class Item(
        val category: DomainPickCategory,
        val selectedCount: Int
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPickCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemPickCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.name.text = item.category.name
            binding.count.text = if (item.selectedCount > 0) {
                "Выбрано: ${item.selectedCount}"
            } else {
                "Выбрать"
            }
            binding.root.setOnClickListener { onCategoryClicked(item.category) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(o: Item, n: Item) = o.category.id == n.category.id
            override fun areContentsTheSame(o: Item, n: Item) = o == n
        }
    }
}
