package sfedu.ictis.walkOfInterest.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import sfedu.ictis.walkOfInterest.databinding.FragmentEditProfileBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.presentation.BaseFragment

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {
    private val viewModel: ProfileViewModel by activityViewModel()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditProfileBinding =
        FragmentEditProfileBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.fieldBtnClose.setOnClickListener { viewModel.closeEditScreen() }

        binding.fieldBtnOk.setOnClickListener { viewModel.applyEditScreen() }

        binding.username.setOnClickListener { showEditNicknameDialog() }
        binding.textUsername.setOnClickListener { showEditNicknameDialog() }

        binding.fieldBtnLogout.setOnClickListener { viewModel.logout() }
        binding.fieldBtnLogoutAll.setOnClickListener { viewModel.logoutAll() }
    }

    private fun showEditNicknameDialog() {
        if (parentFragmentManager.findFragmentByTag(EditNicknameDialog.TAG) != null) return
        EditNicknameDialog().show(parentFragmentManager, EditNicknameDialog.TAG)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.profile?.let { renderProfile(it) }
                    renderLogoutButtons(state.isLoggingOut)
                }
            }
        }
    }

    private fun renderProfile(profile: DomainUserProfile) {
        binding.profileUsername.text = profile.username
        binding.username.text = profile.username

        binding.name.text = profile.firstName
        binding.lastname.text = profile.lastName
        binding.bio.text = profile.bio ?: ""
    }

    private fun renderLogoutButtons(isLoggingOut: Boolean) {
        val enabled = !isLoggingOut
        binding.fieldBtnLogout.isEnabled = enabled
        binding.fieldBtnLogoutAll.isEnabled = enabled
    }

    companion object {
        const val TAG = "EditProfileFragment"
    }
}