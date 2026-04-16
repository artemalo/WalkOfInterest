package sfedu.ictis.walkOfInterest.presentation.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.data.model.dto.CategoryDto
import sfedu.ictis.walkOfInterest.databinding.ItemCategoryBinding
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class CategoriesAdapter(
    private val onItemClicked: (CategoryDto) -> Unit
) : ListAdapter<CategoryDto, CategoriesAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

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
        }

        fun bind(category: CategoryDto) {
            binding.nameCategory.text = category.name

            binding.totalCountPois.text = category.totalPois.toString()
            binding.detailTime.text = formatMinutes(category.time)
            binding.detailPoi.text = "${category.totalPois}"

            // TODO: Для загрузки иконки (category.icon) в binding.imgCategory
            // Glide или Coil.
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryDto>() {
        override fun areItemsTheSame(oldItem: CategoryDto, newItem: CategoryDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryDto, newItem: CategoryDto): Boolean {
            return oldItem == newItem
        }
    }
}