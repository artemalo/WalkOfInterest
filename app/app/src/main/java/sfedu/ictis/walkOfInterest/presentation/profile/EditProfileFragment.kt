package sfedu.ictis.walkOfInterest.presentation.profile

import android.os.Bundle
import android.util.Log
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

        binding.fieldBtnOk.setOnClickListener {
            val updatedFirstName = binding.name.text.toString().trim()
            val updatedLastName = binding.lastname.text.toString().trim()
            val updatedBio = binding.bio.text.toString().trim()

            viewModel.applyEditScreen(updatedFirstName, updatedLastName, updatedBio)
        }

        binding.username.setOnClickListener { showEditNicknameDialog() }

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
                    loadedEditing(state.isUpdatingNickname, state.isUpdatingProfile)
                    state.profile?.let { renderProfile(it) }
                    renderLogoutButtons(state.isLoggingOut)
                }
            }
        }
    }

    private fun loadedEditing(isUpdatingNickname: Boolean, isUpdatingProfile: Boolean) {
        binding.progressBar.visibility =
            if (isUpdatingNickname || isUpdatingProfile) View.VISIBLE else View.GONE

        binding.username.isEnabled = !isUpdatingNickname

        binding.name.isEnabled = !isUpdatingProfile
        binding.lastname.isEnabled = !isUpdatingProfile
        binding.bio.isEnabled = !isUpdatingProfile
    }

    private fun renderProfile(profile: DomainUserProfile) {
        binding.profileUsername.text = profile.username
        binding.username.text = profile.username

        if (binding.name.text.toString() != profile.firstName) {
            binding.name.setText(profile.firstName)
        }
        if (binding.lastname.text.toString() != profile.lastName) {
            binding.lastname.setText(profile.lastName)
        }
        if (binding.bio.text.toString() != profile.bio) {
            binding.bio.setText(profile.bio)
        }
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