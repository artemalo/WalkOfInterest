package sfedu.ictis.walkOfInterest.presentation.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ActivityProfileBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.presentation.BaseActivity
import sfedu.ictis.walkOfInterest.utils.ToastManager
import sfedu.ictis.walkOfInterest.utils.formatLargeNumber
import sfedu.ictis.walkOfInterest.utils.openPoiFragment

class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    private val viewModel: ProfileViewModel by viewModel()

    private val reviewsAdapter = ReviewsAdapter(
        mode = ReviewsAdapter.Mode.PROFILE,
        onReviewClicked = { review ->
            val poiId = review.poiId.toLongOrNull()
            if (poiId != null && poiId > 0) {
                openPoiFragment(poiId, binding.fragmentContainer)
            }
        }
    )

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            viewModel.closeEditScreen()
        }
    }

    override fun inflateBinding(): ActivityProfileBinding =
        ActivityProfileBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        viewModel.initWith(username)

        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        setupRecycler()
        setupListeners()
        observeState()
        observeEvents()
    }

    private fun setupRecycler() {
        binding.recyclerReviews.apply {
            layoutManager = LinearLayoutManager(this@ProfileActivity)
            adapter = reviewsAdapter
            itemAnimator = null
        }
    }

    private fun setupListeners() {
        binding.fieldBtnClose.setOnClickListener { finish() }

        binding.btnEdit.setOnClickListener { viewModel.openEditScreen() }

        binding.btnSort.setOnClickListener { viewModel.toggleSortOrder() }
        binding.iconSwap.setOnClickListener { viewModel.toggleSortOrder() }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.profile?.let { renderProfile(it, state.isLoading, state.isOwnProfile) }
                    renderReviews(state)
                    renderEditScreenVisibility(state.isEditMode, state.isOwnProfile)

                    backPressedCallback.isEnabled = state.isEditMode
                }
            }
        }
    }

    private fun renderProfile(profile: DomainUserProfile, isLoading: Boolean, isOwnProfile: Boolean) {
        binding.profileUsername.text = profile.username
        binding.profileFullName.text = profile.fullName.ifBlank { profile.username }
        binding.profileBio.text = profile.bio ?: ""

        binding.profilePhoto.load(profile.photoUrl) {
            placeholder(R.drawable.ic_profile)
            error(R.drawable.ic_profile)
            crossfade(true)
        }

        binding.countTrips.text = formatLargeNumber(profile.countTrips)
        binding.countSpots.text = formatLargeNumber(profile.countSpots)
        binding.profileCountComments.text = getString(R.string.profile_count_comments, profile.countComments)

        val profileReady = !isLoading && profile.id.isNotEmpty()
        binding.progressBar.visibility = if (!profileReady) View.VISIBLE else View.GONE

        if (isOwnProfile) {
            binding.fieldBtnEdit.isEnabled = profileReady
            binding.btnEdit.isEnabled = profileReady
        }
    }

    private fun renderReviews(state: ProfileUiState) {
        val list = state.sortedReviews
        reviewsAdapter.submitList(list)

        val hasReviews = list.isNotEmpty()
        binding.progressBarReviews.visibility =
            if (state.isLoading) View.VISIBLE else View.GONE
        binding.recyclerReviews.visibility =
            if (hasReviews) View.VISIBLE else View.GONE
        binding.textEmptyReviews.visibility =
            if (!hasReviews && !state.isLoadingReviews) View.VISIBLE else View.GONE
    }

    private fun renderEditScreenVisibility(isEditMode: Boolean, isOwnProfile: Boolean) {
        if (!isOwnProfile) {
            binding.fieldBtnEdit.visibility = View.GONE
            binding.btnEdit.visibility = View.GONE
            binding.editProfileContainer.visibility = View.GONE
            return
        }

        val profileChildrenVisibility = if (isEditMode) View.GONE else View.VISIBLE
        binding.scrollContent.visibility = profileChildrenVisibility
        binding.fieldBtnEdit.visibility = profileChildrenVisibility
        binding.btnEdit.visibility = profileChildrenVisibility

        binding.editProfileContainer.visibility =
            if (isEditMode) View.VISIBLE else View.GONE

        if (isEditMode) {
            val tag = EditProfileFragment.TAG
            if (supportFragmentManager.findFragmentByTag(tag) == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.editProfileContainer, EditProfileFragment(), tag)
                    .commit()
            }
        } else {
            val frag = supportFragmentManager.findFragmentByTag(EditProfileFragment.TAG)
            if (frag != null) {
                supportFragmentManager.beginTransaction().remove(frag).commit()
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ProfileEvent.ShowError ->
                            ToastManager.show(this@ProfileActivity, event.message)
                        is ProfileEvent.EmptyProfile -> {
                            binding.fieldBtnEdit.isEnabled = false
                            binding.btnEdit.isEnabled = false
                        }

                        ProfileEvent.LoggedOut -> navigateToAuth()
                        ProfileEvent.NicknameUpdated,
                        ProfileEvent.CloseEditScreen -> Unit
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"

        fun startFor(context: Context, username: String): Intent =
            Intent(context, ProfileActivity::class.java)
                .putExtra(EXTRA_USERNAME, username)
    }
}
