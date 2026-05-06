package sfedu.ictis.walkOfInterest.presentation.poi.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.databinding.ItemSimilarPoiBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiNearby
import kotlin.math.roundToInt

class SimilarPoisAdapter(
    private val onOpenClicked: (DomainPoiNearby) -> Unit,
    private val onAddInfoClicked: (DomainPoiNearby) -> Unit
) : ListAdapter<DomainPoiNearby, SimilarPoisAdapter.VH>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSimilarPoiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemSimilarPoiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DomainPoiNearby) {
            binding.name.text = item.name
            binding.subtitle.text = listOfNotNull(
                item.categoryName,
                item.subcategoryName
            ).joinToString(" / ").ifBlank { "—" }

            binding.distance.text = item.distanceMeters?.let {
                "${it.roundToInt()} м"
            } ?: ""

            binding.btnOpen.setOnClickListener { onOpenClicked(item) }
            binding.btnAddInfo.setOnClickListener { onAddInfoClicked(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DomainPoiNearby>() {
            override fun areItemsTheSame(o: DomainPoiNearby, n: DomainPoiNearby) = o.id == n.id
            override fun areContentsTheSame(o: DomainPoiNearby, n: DomainPoiNearby) = o == n
        }
    }
}
