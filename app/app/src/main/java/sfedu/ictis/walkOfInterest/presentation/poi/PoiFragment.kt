package sfedu.ictis.walkOfInterest.presentation.poi

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.FragmentPoiBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoi
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.presentation.BaseFragment
import sfedu.ictis.walkOfInterest.presentation.poi.review.ReviewMakeFragment
import sfedu.ictis.walkOfInterest.presentation.profile.ProfileActivity
import sfedu.ictis.walkOfInterest.presentation.profile.ReviewsAdapter
import sfedu.ictis.walkOfInterest.utils.ToastManager
import kotlin.math.roundToInt

class PoiFragment : BaseFragment<FragmentPoiBinding>() {
    private val viewModel: PoiViewModel by viewModel()

    private val tagsAdapter = PoiTagsAdapter()
    private val reviewsAdapter by lazy {
        ReviewsAdapter(
            mode = ReviewsAdapter.Mode.POI,
            onReviewClicked = { review ->
                val currentUsername = viewModel.uiState.value.currentUsername
                if (currentUsername != null && review.authorUsername.equals(currentUsername, ignoreCase = true)) {
                    startActivity(android.content.Intent(requireContext(), ProfileActivity::class.java))
                } else {
                    startActivity(ProfileActivity.startFor(requireContext(), review.authorUsername))
                }
            },
            onLikeClicked = { review -> viewModel.onLikeClicked(review) },
            onDislikeClicked = { review -> viewModel.onDislikeClicked(review) }
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPoiBinding = FragmentPoiBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclers()
        setupListeners()
        observeState()
        observeEvents()
        observeReviewMakeResult()

        val poiId = resolvePoiId()
        if (poiId == null) {
            ToastManager.show(requireContext(), "Нет идентификатора точки")
            parentFragmentManager.popBackStack()
            return
        }

        readPoiArg()?.let { poi ->
            viewModel.preload(name = poi.name, rating = poi.rate, count = poi.count)
        }

        viewModel.load(poiId)
    }

    private fun setupRecyclers() {
        binding.itemList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = tagsAdapter
        }

        binding.recyclerReviews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewsAdapter
            itemAnimator = null
        }
    }

    private fun setupListeners() {
        binding.fieldBtnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.fieldLikePoi.setOnClickListener { viewModel.toggleSaved() }
        binding.frameAddressShow.setOnClickListener { openPoiMap() }
        binding.btnSort.setOnClickListener { viewModel.toggleSortOrder() }
        binding.btnEdit.setOnClickListener { viewModel.onAddOrEditReviewClicked() }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.poi?.let { renderPoi(it) }
                    renderReviews(state)
                    renderEditButton(state)
                    renderSavedState(state.isSaved)
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is PoiEvent.ShowError ->
                            ToastManager.show(requireContext(), event.message)
                        is PoiEvent.OpenReviewMake -> openReviewMake(event)
                    }
                }
            }
        }
    }

    private fun observeReviewMakeResult() {
        setFragmentResultListener(ReviewMakeFragment.RESULT_KEY) { _, _ ->
            viewModel.refreshAfterReviewSaved()
        }
    }

    private fun openReviewMake(event: PoiEvent.OpenReviewMake) {
        val fragment = ReviewMakeFragment.newInstance(
            poiId = event.poiId,
            poiName = event.poiName,
            poiAddress = event.poiAddress,
            existingRating = event.existingReview?.rating,
            existingContent = event.existingReview?.content
        )

        val parent = (view?.parent as? View)
        val containerId = parent?.id ?: R.id.fragment_container

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun renderPoi(poi: DomainPoiInfo) {
        binding.name.text = poi.name ?: "—"

        val hasPoint = poi.point != null
        binding.address.text = poi.point
            ?.let { "%.6f, %.6f".format(it.lat, it.lon) }
            ?: "Адрес недоступен"
        binding.showText.visibility = if (hasPoint) View.VISIBLE else View.GONE

        binding.description.text = poi.description.orEmpty()

        binding.rate.text = if (poi.countRate > 0) {
            "%.1f".format(poi.rating)
        } else {
            "—"
        }
        binding.countRate.text = getString(R.string.count_rate, poi.countRate)

        renderStars(poi.rating)

        tagsAdapter.submitList(poi.tags)
    }

    private fun renderStars(rating: Double) {
        val activeColor = R.color.object_orange
        val inactiveColor = R.color.object_not_active
        val rounded = rating.roundToInt().coerceIn(0, 5)

        val stars = listOf(
            binding.star1, binding.star2, binding.star3, binding.star4, binding.star5
        )
        stars.forEachIndexed { index, star ->
            val color = if (index < rounded) activeColor else inactiveColor
            star.setColorFilter(
                androidx.core.content.ContextCompat.getColor(requireContext(), color)
            )
        }
    }

    private fun renderReviews(state: PoiUiState) {
        val list = state.sortedReviews
        reviewsAdapter.submitList(list)

        binding.textView3.text = list.size.toString()
        binding.titleAllReviews.text = pluralizeReviews(list.size)
    }

    private fun renderEditButton(state: PoiUiState) {
        binding.textBtnEdit.text = if (state.myReview != null) "Изменить" else "Добавить"
    }

    private fun renderSavedState(isSaved: Boolean) {
        binding.likePoi.isSelected = isSaved
    }

    private fun pluralizeReviews(count: Int): String {
        val mod10 = count % 10
        val mod100 = count % 100
        return when {
            mod100 in 11..14 -> "отзывов"
            mod10 == 1 -> "отзыв"
            mod10 in 2..4 -> "отзыва"
            else -> "отзывов"
        }
    }

    private fun resolvePoiId(): Long? {
        val direct = arguments?.getLong(ARG_POI_ID, -1L)?.takeIf { it > 0 }
        if (direct != null) return direct

        return readPoiArg()?.id
    }

    private fun readPoiArg(): DomainPoi? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_POI, DomainPoi::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_POI)
        }
    }

    private fun openPoiMap() {
        val poi = viewModel.uiState.value.poi ?: return
        val point = poi.point ?: return

        val fragment = PoiMapFragment.newInstance(point.lat, point.lon, poi.name, poi.tags.first().categoryId ?: 0)
        val containerId = (view?.parent as? View)?.id ?: R.id.fragment_container

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val ARG_POI_ID = "arg_poi_id"
        private const val ARG_POI = "arg_poi"

        fun newInstance(poiId: Long): PoiFragment = PoiFragment().apply {
            arguments = Bundle().apply { putLong(ARG_POI_ID, poiId) }
        }

        fun newInstance(poi: DomainPoi): PoiFragment = PoiFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_POI_ID, poi.id)
                putParcelable(ARG_POI, poi)
            }
        }
    }
}