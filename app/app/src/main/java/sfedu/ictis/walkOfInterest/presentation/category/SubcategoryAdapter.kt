package sfedu.ictis.walkOfInterest.presentation.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.databinding.ItemSubcategoryBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainSubCategory

class SubcategoryAdapter(
    private val onSeeAllClick: (DomainSubCategory) -> Unit,
    private val onPoiClick: (subcategoryId: Int, poiId: Long) -> Unit
) : ListAdapter<DomainSubCategory, SubcategoryAdapter.SubcatViewHolder>(SubcatDiffCallback()) {
    inner class SubcatViewHolder(private val binding: ItemSubcategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subcategory: DomainSubCategory) {
            binding.nameSubcategory.text = subcategory.name

            binding.SeeAll.setOnClickListener {
                onSeeAllClick(subcategory)
            }

            val poiAdapter = PoiAdapter { poi ->
                onPoiClick(subcategory.id, poi.id)
            }
            binding.itemList.adapter = poiAdapter
            poiAdapter.submitList(subcategory.pois)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcatViewHolder {
        val binding = ItemSubcategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubcatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubcatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SubcatDiffCallback : DiffUtil.ItemCallback<DomainSubCategory>() {
    override fun areItemsTheSame(oldItem: DomainSubCategory, newItem: DomainSubCategory) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: DomainSubCategory, newItem: DomainSubCategory) = oldItem == newItem
}