package sfedu.ictis.walkOfInterest.presentation.poi.add.form

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.FragmentAddPoiFormBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPickSubcategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.presentation.BaseFragment
import sfedu.ictis.walkOfInterest.utils.ToastManager
import sfedu.ictis.walkOfInterest.utils.calculateColorByCategory

class AddPoiFormFragment : BaseFragment<FragmentAddPoiFormBinding>() {

    private val viewModel: AddPoiFormViewModel by viewModel()

    private val categoryAdapter by lazy {
        CategoryPickAdapter(onCategoryClicked = { cat -> openSubcategorySheet(cat) })
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddPoiFormBinding =
        FragmentAddPoiFormBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val point = readPointArg()
        val targetId = arguments?.getLong(ARG_TARGET_POI_ID, -1L)?.takeIf { it > 0 }

        if (point == null) {
            ToastManager.show(requireContext(), "Точка не выбрана")
            parentFragmentManager.popBackStack()
            return
        }

        viewModel.init(point, targetId)

        setupRecyclers()
        setupListeners()
        observeState()
        observeEvents()
    }

    private fun setupRecyclers() {
        binding.recyclerCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
            itemAnimator = null
        }
    }

    private fun setupListeners() {
        binding.fieldBtnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameChanged(s?.toString().orEmpty())
            }
        })

        binding.editDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onDescriptionChanged(s?.toString().orEmpty())
            }
        })

        binding.btnLangRu.setOnClickListener { viewModel.onLangSelected(FormLang.RU) }
        binding.btnLangEn.setOnClickListener { viewModel.onLangSelected(FormLang.EN) }

        binding.btnSubmit.setOnClickListener { viewModel.onSubmitClicked() }
        binding.btnRetryCategories.setOnClickListener { viewModel.retryLoadCategories() }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.uiState
                        .map { it.lang }
                        .distinctUntilChanged()
                        .collect { lang ->
                            val isRu = lang == FormLang.RU
                            binding.btnLangRu.isSelected = isRu
                            binding.btnLangEn.isSelected = !isRu
                        }
                }

                launch {
                    viewModel.uiState
                        .map { Pair(it.categories, it.selectedSubcategoryIds) }
                        .distinctUntilChanged()
                        .collect { (cats, selected) ->
                            renderCategories(cats, selected)
                        }
                }

                launch {
                    viewModel.uiState
                        .map { it.selectedSubcategories }
                        .distinctUntilChanged()
                        .collect { list ->
                            renderSelectedChips(list)
                            binding.titleSelected.visibility =
                                if (list.isEmpty()) View.GONE else View.VISIBLE
                        }
                }

                launch {
                    viewModel.uiState
                        .map { Triple(it.isLoadingCategories, it.errorBanner, it.categories.isEmpty()) }
                        .distinctUntilChanged()
                        .collect { (loading, banner, empty) ->
                            binding.progressCategories.visibility = if (loading) View.VISIBLE else View.GONE
                            val hasBanner = banner != null && empty
                            binding.errorCategoriesBlock.visibility = if (hasBanner) View.VISIBLE else View.GONE
                            binding.errorCategoriesText.text = banner.orEmpty()
                        }
                }

                launch {
                    viewModel.uiState
                        .map { Pair(it.isSubmitting, it.isFormValid) }
                        .distinctUntilChanged()
                        .collect { (submitting, valid) ->
                            binding.btnSubmit.isEnabled = !submitting && valid
                            binding.btnSubmit.alpha = if (!submitting && valid) 1f else 0.5f
                            binding.textBtnSubmit.text = if (submitting) "Отправка..." else "Отправить на модерацию"

                            // Disable inputs while submitting.
                            val enabled = !submitting
                            binding.editName.isEnabled = enabled
                            binding.editDescription.isEnabled = enabled
                            binding.btnLangRu.isEnabled = enabled
                            binding.btnLangEn.isEnabled = enabled
                            binding.recyclerCategories.alpha = if (enabled) 1f else 0.5f
                            binding.chipGroupSelected.alpha = if (enabled) 1f else 0.5f
                            binding.progressSubmit.visibility = if (submitting) View.VISIBLE else View.GONE
                        }
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is AddPoiFormEvent.ShowError ->
                            ToastManager.show(requireContext(), event.message)

                        is AddPoiFormEvent.ShowRateLimit ->
                            ToastManager.show(requireContext(), event.message)

                        is AddPoiFormEvent.PrefillApplied -> applyPrefill(event)

                        AddPoiFormEvent.SubmittedSuccessfully -> {
                            ToastManager.show(
                                requireContext(),
                                "Место отправлено на модерацию.\nОно появится на карте после проверки"
                            )
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun renderCategories(
        cats: List<DomainPickCategory>,
        selectedIds: Set<Int>
    ) {
        val items = cats.map { cat ->
            CategoryPickAdapter.Item(
                category = cat,
                selectedCount = cat.subcategories.count { selectedIds.contains(it.id) }
            )
        }
        categoryAdapter.submitList(items)
    }

    private fun renderSelectedChips(list: List<DomainPickSubcategory>) {
        val group = binding.chipGroupSelected
        group.removeAllViews()

        list.forEach { sub ->
            val chip = Chip(requireContext()).apply {
                text = sub.name
                isCloseIconVisible = true
                isClickable = true
                isCheckable = false

                val color = calculateColorByCategory(sub.categoryId ?: 0)
                chipBackgroundColor = android.content.res.ColorStateList.valueOf(color)
                setTextColor(
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white)
                )
                setOnCloseIconClickListener {
                    viewModel.onSubcategoryRemoved(sub.id)
                }
            }
            group.addView(chip)
        }
    }

    private fun applyPrefill(event: AddPoiFormEvent.PrefillApplied) {
        if (binding.editName.text?.toString().orEmpty() != event.name) {
            binding.editName.setText(event.name)
            binding.editName.setSelection(event.name.length)
        }
        if (binding.editDescription.text?.toString().orEmpty() != event.description) {
            binding.editDescription.setText(event.description)
            binding.editDescription.setSelection(event.description.length)
        }
    }

    private fun openSubcategorySheet(cat: DomainPickCategory) {
        SubcategoryPickBottomSheet
            .newInstance(cat)
            .show(childFragmentManager, SubcategoryPickBottomSheet.TAG)
    }

    @Suppress("DEPRECATION")
    private fun readPointArg(): DomainPoint? {
        val args = arguments ?: return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getParcelable(ARG_POINT, DomainPoint::class.java)
        } else {
            args.getParcelable(ARG_POINT)
        }
    }

    companion object {
        const val BACKSTACK_TAG = "AddPoiFormFragment"
        private const val ARG_POINT = "arg_point"
        private const val ARG_TARGET_POI_ID = "arg_target_poi_id"

        fun newInstance(point: DomainPoint, targetPoiId: Long?): AddPoiFormFragment =
            AddPoiFormFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_POINT, point)
                    if (targetPoiId != null) putLong(ARG_TARGET_POI_ID, targetPoiId)
                }
            }
    }
}
