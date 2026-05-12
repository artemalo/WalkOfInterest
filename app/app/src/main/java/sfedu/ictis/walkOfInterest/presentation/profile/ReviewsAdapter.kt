package sfedu.ictis.walkOfInterest.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ItemReviewBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.ReactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ReviewsAdapter(
    private val mode: Mode,
    private val onReviewClicked: (DomainReview) -> Unit = {},
    private val onLikeClicked: ((DomainReview) -> Unit)? = null,
    private val onDislikeClicked: ((DomainReview) -> Unit)? = null
) : ListAdapter<DomainReview, ReviewsAdapter.ReviewViewHolder>(DIFF) {

    enum class Mode { PROFILE, POI }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemReviewBinding.inflate(inflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: DomainReview) {
            when (mode) {
                Mode.PROFILE -> {
                    binding.tvUserName.text = review.poiName
                    binding.ivUserAvatar.visibility = View.GONE
                }
                Mode.POI -> {
                    binding.tvUserName.text = review.authorUsername
                    binding.ivUserAvatar.visibility = View.VISIBLE
                    binding.ivUserAvatar.load(review.authorAvatarUrl) {
                        placeholder(R.drawable.ic_profile)
                        error(R.drawable.ic_profile)
                        crossfade(true)
                    }
                }
            }

            binding.tvReviewText.text = review.content
            binding.tvReviewRate.text = review.rating.toString()
            binding.tvReviewDate.text = DATE_FORMAT.format(Date(review.createdAtMillis))

            binding.likes.text = review.likes.toString()
            binding.dislikes.text = review.dislikes.toString()

            binding.btnLike.isChecked = review.myReaction == ReactionType.LIKE
            binding.btnDislike.isChecked = review.myReaction == ReactionType.DISLIKE

            if (mode == Mode.POI && onLikeClicked != null) {
                binding.btnLike.isEnabled = true
                binding.btnLike.setOnClickListener { onLikeClicked.invoke(review) }
            } else {
                binding.btnLike.isEnabled = false
                binding.btnLike.setOnClickListener(null)
            }

            if (mode == Mode.POI && onDislikeClicked != null) {
                binding.btnDislike.isEnabled = true
                binding.btnDislike.setOnClickListener { onDislikeClicked.invoke(review) }
            } else {
                binding.btnDislike.isEnabled = false
                binding.btnDislike.setOnClickListener(null)
            }

            binding.root.setOnClickListener { onReviewClicked(review) }
        }
    }

    private companion object DIFF : DiffUtil.ItemCallback<DomainReview>() {
        val DATE_FORMAT = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        override fun areItemsTheSame(oldItem: DomainReview, newItem: DomainReview) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DomainReview, newItem: DomainReview) =
            oldItem == newItem
    }
}