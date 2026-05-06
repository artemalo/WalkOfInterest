package sfedu.ictis.walkOfInterest.presentation.poi.add.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.databinding.ItemMyPoiBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainMyPoi
import sfedu.ictis.walkOfInterest.domain.model.PoiStatus

class MyPoisAdapter(
    private val onClicked: (DomainMyPoi) -> Unit
) : ListAdapter<DomainMyPoi, MyPoisAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMyPoiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemMyPoiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DomainMyPoi) {
            binding.name.text = item.name?.takeIf { it.isNotBlank() } ?: "Без названия"
            val tags = item.tags.joinToString(", ") { it.subcategoryName }
            binding.tags.text = tags.ifBlank { "—" }

            val rejected = item.status == PoiStatus.REJECTED
            binding.rejectionBlock.visibility = if (rejected) View.VISIBLE else View.GONE
            binding.rejectionText.text = item.rejectionReason ?: "Причина не указана"

            binding.statusLabel.text = when (item.status) {
                PoiStatus.PENDING -> "На модерации"
                PoiStatus.APPROVED -> "Опубликовано"
                PoiStatus.REJECTED -> "Отклонено"
            }

            binding.root.setOnClickListener {
                if (item.status == PoiStatus.APPROVED) onClicked(item)
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DomainMyPoi>() {
            override fun areItemsTheSame(o: DomainMyPoi, n: DomainMyPoi) = o.id == n.id
            override fun areContentsTheSame(o: DomainMyPoi, n: DomainMyPoi) = o == n
        }
    }
}
