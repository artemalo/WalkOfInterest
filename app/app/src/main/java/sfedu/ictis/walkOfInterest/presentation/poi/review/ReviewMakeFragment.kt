package sfedu.ictis.walkOfInterest.presentation.poi.review

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.FragmentReviewMakeBinding
import sfedu.ictis.walkOfInterest.presentation.BaseFragment
import sfedu.ictis.walkOfInterest.utils.ToastManager

class ReviewMakeFragment : BaseFragment<FragmentReviewMakeBinding>() {

    private val viewModel: ReviewMakeViewModel by viewModel()

    private val starViews by lazy {
        listOf(
            binding.star1, binding.star2, binding.star3, binding.star4, binding.star5
        )
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun afterTextChanged(s: Editable?) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.onContentChanged(s?.toString().orEmpty())
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReviewMakeBinding =
        FragmentReviewMakeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = maxOf(ime.bottom, systemBars.bottom))
            insets
        }

        val poiId = arguments?.getLong(ARG_POI_ID, -1L) ?: -1L
        if (poiId <= 0) {
            ToastManager.show(requireContext(), "Нет идентификатора точки")
            parentFragmentManager.popBackStack()
            return
        }

        val existingRating = arguments?.getInt(ARG_EXISTING_RATING, -1)
            ?.takeIf { it in 1..5 }

        viewModel.init(
            poiId = poiId,
            poiName = arguments?.getString(ARG_POI_NAME),
            poiAddress = arguments?.getString(ARG_POI_ADDRESS),
            existingRating = existingRating,
            existingContent = arguments?.getString(ARG_EXISTING_CONTENT)
        )

        // TODO: photoPoi

        setupListeners()
        observeState()
        observeEvents()
    }

    override fun onDestroyView() {
        binding.reviewEditText.removeTextChangedListener(textWatcher)
        super.onDestroyView()
    }

    private fun setupListeners() {
        binding.fieldBtnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        starViews.forEachIndexed { index, frame ->
            frame.setOnClickListener { viewModel.onRatingSelected(index + 1) }
        }

        binding.reviewEditText.addTextChangedListener(textWatcher)

        binding.fieldBtnSave.setOnClickListener { viewModel.onSaveClicked() }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.poiName.text = state.poiName ?: "—"
                    binding.addressPoi.text = state.poiAddress ?: "Адрес пуст"

                    binding.textBtnSave.text = if (state.isEditMode) "Сохранить" else "Добавить"

                    val current = binding.reviewEditText.text?.toString().orEmpty()
                    if (current != state.content) {
                        binding.reviewEditText.removeTextChangedListener(textWatcher)
                        binding.reviewEditText.setText(state.content)
                        binding.reviewEditText.setSelection(state.content.length)
                        binding.reviewEditText.addTextChangedListener(textWatcher)
                    }

                    renderStars(state.rating)

                    binding.btnSave.isEnabled = !state.isSaving
                    binding.fieldBtnSave.isEnabled = !state.isSaving
                    binding.fieldBtnSave.alpha = if (state.isSaving) 0.6f else 1f
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ReviewMakeEvent.ShowError ->
                            ToastManager.show(requireContext(), event.message)
                        is ReviewMakeEvent.Saved -> {
                            ToastManager.show(requireContext(), "Отзыв сохранён")
                            setFragmentResult(RESULT_KEY, Bundle.EMPTY)
                            parentFragmentManager.popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun renderStars(rating: Int) {
        starViews.forEachIndexed { index, frame ->
            val image = frame.getChildAt(0) as? android.widget.ImageView ?: return@forEachIndexed
            image.setColorFilter(
                if (index < rating)
                    ContextCompat.getColor(requireContext(), R.color.object_orange)
                else
                    ContextCompat.getColor(requireContext(), R.color.object_not_active)
            )
        }
    }

    companion object {
        const val RESULT_KEY = "review_make_result"

        private const val ARG_POI_ID = "arg_poi_id"
        private const val ARG_POI_NAME = "arg_poi_name"
        private const val ARG_POI_ADDRESS = "arg_poi_address"
        private const val ARG_EXISTING_RATING = "arg_existing_rating"
        private const val ARG_EXISTING_CONTENT = "arg_existing_content"

        fun newInstance(
            poiId: Long,
            poiName: String?,
            poiAddress: String?,
            existingRating: Int?,
            existingContent: String?
        ): ReviewMakeFragment = ReviewMakeFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_POI_ID, poiId)
                putString(ARG_POI_NAME, poiName)
                putString(ARG_POI_ADDRESS, poiAddress)
                if (existingRating != null) putInt(ARG_EXISTING_RATING, existingRating)
                putString(ARG_EXISTING_CONTENT, existingContent)
            }
        }
    }
}