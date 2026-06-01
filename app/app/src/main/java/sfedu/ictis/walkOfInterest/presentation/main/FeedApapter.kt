package sfedu.ictis.walkOfInterest.presentation.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ItemSpotBinding
import sfedu.ictis.walkOfInterest.databinding.ItemTripBinding
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class FeedAdapter(
    private val onTripClicked: (String) -> Unit,
    private val onSpotClicked: (Long) -> Unit
) : ListAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {
    private companion object {
        const val TYPE_TRIP = 0
        const val TYPE_SPOT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FeedItem.Trip -> TYPE_TRIP
            is FeedItem.Spot -> TYPE_SPOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.i("FeedAdapter", "onCreateViewHolder: $viewType")
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TRIP -> TripViewHolder(ItemTripBinding.inflate(inflater, parent, false))
            TYPE_SPOT -> SpotViewHolder(ItemSpotBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is FeedItem.Trip -> (holder as TripViewHolder).bind(item)
            is FeedItem.Spot -> (holder as SpotViewHolder).bind(item)
        }
    }

    inner class TripViewHolder(private val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FeedItem.Trip) {
            binding.titleTrip.text = item.title
            binding.textFrom.text = item.addressFrom
            binding.textTo.text = item.addressTo
            binding.countSelected.text = item.totalPois.toString()
            binding.time.text = formatMinutes(item.totalTime)

            binding.bestTime.text = item.bestTime?.let {
                if (it != 0) formatMinutes(it) else "-"
            } ?: "-"

            binding.root.setOnClickListener {
                onTripClicked(item.id)
            }
        }
    }

    inner class SpotViewHolder(private val binding: ItemSpotBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FeedItem.Spot) {
            binding.titleSpot.text = item.title
            binding.addressSpot.text = item.address

            binding.imgTrip.load(item.photo) {
                crossfade(true)
                placeholder(R.color.white_empty)
                error(R.color.white_empty)
            }

            binding.root.setOnClickListener {
                onSpotClicked(item.id)
            }
        }
    }
}

class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return when {
            oldItem is FeedItem.Trip && newItem is FeedItem.Trip -> oldItem.id == newItem.id
            oldItem is FeedItem.Spot && newItem is FeedItem.Spot -> oldItem.id == newItem.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}