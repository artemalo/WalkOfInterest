package sfedu.ictis.walkOfInterest.presentation.poi.add.form

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.databinding.ItemPickSubcategoryBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPickSubcategory

class SubcategoryPickAdapter(
    private val onToggle: (DomainPickSubcategory) -> Unit
) : ListAdapter<SubcategoryPickAdapter.Item, SubcategoryPickAdapter.VH>(DIFF) {
    data class Item(
        val sub: DomainPickSubcategory,
        val isSelected: Boolean
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPickSubcategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemPickSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = item.isSelected
            binding.name.text = item.sub.name

            val toggle = { onToggle(item.sub) }
            binding.root.setOnClickListener { toggle() }
            binding.checkbox.setOnClickListener { toggle() }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(o: Item, n: Item) = o.sub.id == n.sub.id
            override fun areContentsTheSame(o: Item, n: Item) = o == n
        }
    }
}
