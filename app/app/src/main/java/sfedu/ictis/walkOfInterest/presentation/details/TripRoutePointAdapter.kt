package sfedu.ictis.walkOfInterest.presentation.details

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.databinding.ItemTripRoutePointBinding
import sfedu.ictis.walkOfInterest.domain.model.RoutePoint
import sfedu.ictis.walkOfInterest.utils.calculateColorByCategory

class TripRoutePointAdapter(
    private val onPointClicked: (RoutePoint) -> Unit
) : ListAdapter<RoutePoint, TripRoutePointAdapter.RoutePointViewHolder>(RoutePointDiffCallback()) {

    inner class RoutePointViewHolder(private val binding: ItemTripRoutePointBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPointClicked(getItem(position))
                }
            }
        }

        fun bind(point: RoutePoint, position: Int) {
            binding.orderNumber.text = (position + 1).toString()
            binding.pointName.text = point.name
            binding.pointSubcategory.text = point.nameSubcat

            val color = calculateColorByCategory(point.categoryId)
            binding.orderBadge.backgroundTintList = ColorStateList.valueOf(color)
            binding.tagCategory.setCardBackgroundColor(color)
            binding.tagText.text = point.nameCat
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutePointViewHolder {
        val binding = ItemTripRoutePointBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RoutePointViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutePointViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}

class RoutePointDiffCallback : DiffUtil.ItemCallback<RoutePoint>() {
    override fun areItemsTheSame(oldItem: RoutePoint, newItem: RoutePoint) =
        oldItem.id == newItem.id && oldItem.order == newItem.order

    override fun areContentsTheSame(oldItem: RoutePoint, newItem: RoutePoint) =
        oldItem == newItem
}