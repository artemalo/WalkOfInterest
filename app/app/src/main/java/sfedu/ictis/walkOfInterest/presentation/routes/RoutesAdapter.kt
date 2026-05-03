package sfedu.ictis.walkOfInterest.presentation.routes

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ItemRouteBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.utils.calculateRouteColor
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class RoutesAdapter(
    private val onRouteClicked: (DomainRoute) -> Unit,
    private val onAboutClicked: (DomainRoute) -> Unit
) : ListAdapter<DomainRoute, RoutesAdapter.RouteViewHolder>(RouteDiffCallback()) {

    inner class RouteViewHolder(private val binding: ItemRouteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.route.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRouteClicked(getItem(position))
                }
            }

            binding.btnAbout.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAboutClicked(getItem(position))
                }
            }
        }

        fun bind(route: DomainRoute) {
            binding.routeTime.text = formatMinutes(route.minTime)
            binding.routeSteps.text = route.steps.toString()

            val context = binding.root.context

            val bestTime = currentList.firstOrNull()?.minTime ?: route.minTime
            val timeDiff = route.minTime - bestTime
            val routeColor = calculateRouteColor(context, timeDiff)

            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_pressed),
                    intArrayOf()
                ),
                intArrayOf(
                    ContextCompat.getColor(context, R.color.object_selected),
                    routeColor
                )
            )

            binding.constraintLayout4.backgroundTintList = colorStateList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = ItemRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RouteDiffCallback : DiffUtil.ItemCallback<DomainRoute>() {
    override fun areItemsTheSame(oldItem: DomainRoute, newItem: DomainRoute) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: DomainRoute, newItem: DomainRoute) = oldItem == newItem
}