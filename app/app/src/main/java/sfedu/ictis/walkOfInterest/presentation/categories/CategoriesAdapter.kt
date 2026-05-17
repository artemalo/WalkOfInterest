package sfedu.ictis.walkOfInterest.presentation.categories

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ItemCategoryBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class CategoriesAdapter(
    private val onItemClicked: (DomainCategory) -> Unit,
    private val onPictureClicked: (DomainCategory) -> Unit
) : ListAdapter<DomainCategory, CategoriesAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(position))
                }
            }

            binding.framePicture.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPictureClicked(getItem(position))
                }
            }
        }

        fun bind(category: DomainCategory) {
            binding.nameCategory.text = category.name
            binding.totalCountPois.text = category.totalPois.toString()
            binding.time.text = formatMinutes(category.time)
            binding.countSelected.text = "${category.selected}"

            val isSelected = category.isSelect
            binding.root.isSelected = isSelected

            val paddingPx = if (isSelected) {
                itemView.context.resources.getDimensionPixelSize(R.dimen.padding_middle)
            } else 0
            binding.framePicture.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)

            val context = binding.root.context
            val colorResTime = if (category.selected > 0) R.color.white_empty else R.color.object_not_active
            val colorResSelected = if (category.selected > 0) R.color.`object` else R.color.object_not_active

            TextViewCompat.setCompoundDrawableTintList(
                binding.time,
                ColorStateList.valueOf(ContextCompat.getColor(context, colorResTime))

            )
            TextViewCompat.setCompoundDrawableTintList(
                binding.countSelected,
                ColorStateList.valueOf(ContextCompat.getColor(context, colorResSelected))

            )

            binding.root.setOnClickListener {
                if (category.selected > 0) {
                    onItemClicked(category)
                }
            }

            binding.framePicture.setOnClickListener {
                onPictureClicked(category)
            }

            binding.imgCategory.load(category.icon) {
                crossfade(true)
                placeholder(R.color.white_empty)
                error(R.color.white_empty)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<DomainCategory>() {
        override fun areItemsTheSame(oldItem: DomainCategory, newItem: DomainCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DomainCategory, newItem: DomainCategory): Boolean {
            return oldItem == newItem
        }
    }
}