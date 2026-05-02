package sfedu.ictis.walkOfInterest.presentation.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.databinding.ItemSubcategoryBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoi
import sfedu.ictis.walkOfInterest.domain.model.DomainSubCategory

class SubcategoryAdapter(
    private val onSeeAllClick: (DomainSubCategory) -> Unit,
    private val onPoiClick: (subcategoryId: Int, poi: DomainPoi) -> Unit
) : ListAdapter<DomainSubCategory, SubcategoryAdapter.SubcatViewHolder>(SubcatDiffCallback()) {
    inner class SubcatViewHolder(private val binding: ItemSubcategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private var currentSubcategoryId: Int = -1

        private val poiAdapter = PoiAdapter { poi ->
            if (currentSubcategoryId != -1) {
                onPoiClick(currentSubcategoryId, poi)
            }
        }

        init {
            binding.itemList.adapter = poiAdapter
        }

        fun bind(subcategory: DomainSubCategory) {
            currentSubcategoryId = subcategory.id
            binding.nameSubcategory.text = subcategory.name

            binding.SeeAll.setOnClickListener {
                onSeeAllClick(subcategory)
            }

            updatePois(subcategory.pois)
        }

        fun updatePois(pois: List<DomainPoi>) {
            poiAdapter.submitList(pois)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcatViewHolder {
        val binding = ItemSubcategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubcatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubcatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: SubcatViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            if (payloads[0] == "POIS_CHANGED") {
                holder.updatePois(getItem(position).pois)
                return
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }
}

class SubcatDiffCallback : DiffUtil.ItemCallback<DomainSubCategory>() {
    override fun areItemsTheSame(oldItem: DomainSubCategory, newItem: DomainSubCategory) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: DomainSubCategory, newItem: DomainSubCategory) = oldItem == newItem

    override fun getChangePayload(oldItem: DomainSubCategory, newItem: DomainSubCategory): Any? {
        if (oldItem.pois != newItem.pois) {
            return "POIS_CHANGED"
        }
        return super.getChangePayload(oldItem, newItem)
    }
}