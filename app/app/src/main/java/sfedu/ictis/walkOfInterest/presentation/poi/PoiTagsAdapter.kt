package sfedu.ictis.walkOfInterest.presentation.poi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.databinding.ItemPoiTagBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiTag
import sfedu.ictis.walkOfInterest.utils.calculateColorByCategory

class PoiTagsAdapter : ListAdapter<DomainPoiTag, PoiTagsAdapter.TagViewHolder>(DIFF) {
    class TagViewHolder(private val binding: ItemPoiTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: DomainPoiTag) {
            binding.tagText.text = tag.subcategoryName

            val color = calculateColorByCategory(tag.categoryId ?: 0)
            binding.root.setCardBackgroundColor(color)

            // TODO: иконку подбираем по subcategoryId/categoryId, пока — дефолт из layout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPoiTagBinding.inflate(inflater, parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object DIFF : DiffUtil.ItemCallback<DomainPoiTag>() {
        override fun areItemsTheSame(oldItem: DomainPoiTag, newItem: DomainPoiTag) =
            oldItem.subcategoryId == newItem.subcategoryId

        override fun areContentsTheSame(oldItem: DomainPoiTag, newItem: DomainPoiTag) =
            oldItem == newItem
    }
}