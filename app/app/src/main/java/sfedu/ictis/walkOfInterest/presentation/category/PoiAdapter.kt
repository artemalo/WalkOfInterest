package sfedu.ictis.walkOfInterest.presentation.category

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ItemPoiBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoi

class PoiAdapter(
    private val isVerticalList: Boolean = false,
    private val onPoiClick: (DomainPoi) -> Unit
) : ListAdapter<DomainPoi, PoiAdapter.PoiViewHolder>(PoiDiffCallback()) {

    inner class PoiViewHolder(private val binding: ItemPoiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(poi: DomainPoi) {
            binding.nameCategory.text = poi.name ?: "-"
            binding.tvRate.text = if (poi.rate != null) "${poi.rate} (${poi.count ?: 0})" else "Нет оценок"

            bindSelected(poi.selected)

            binding.root.setOnClickListener {
                onPoiClick(poi)
            }
        }

        fun bindSelected(selected: Boolean) {
            Log.i("PoiViewHolder", "bindSelected: $selected")

            binding.root.alpha = if (selected) 1.0f else 0.5f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        Log.i("PoiAdapter", "onCreateViewHolder")

        val binding = ItemPoiBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        if (isVerticalList) {
            val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.marginEnd = 0
            layoutParams.bottomMargin = parent.context.resources.getDimensionPixelSize(R.dimen.padding_general)
            binding.root.layoutParams = layoutParams
        }

        return PoiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val isSelected = payloads[0] as? Boolean
            if (isSelected != null) {
                holder.bindSelected(isSelected)
                return
            }
        }

        super.onBindViewHolder(holder, position, payloads)
    }
}

class PoiDiffCallback : DiffUtil.ItemCallback<DomainPoi>() {
    override fun areItemsTheSame(oldItem: DomainPoi, newItem: DomainPoi) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: DomainPoi, newItem: DomainPoi) = oldItem == newItem

    override fun getChangePayload(oldItem: DomainPoi, newItem: DomainPoi): Any? {
        if (oldItem.selected != newItem.selected) {
            return newItem.selected
        }
        return super.getChangePayload(oldItem, newItem)
    }
}