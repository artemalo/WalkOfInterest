package sfedu.ictis.walkOfInterest.presentation.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import sfedu.ictis.walkOfInterest.databinding.DialogBackEditNicknameBinding

class EditNicknameDialog : DialogFragment() {
    private var _binding: DialogBackEditNicknameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by activityViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBackEditNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val current = viewModel.uiState.value.profile?.username.orEmpty()
        if (binding.editUsername.text.isNullOrEmpty() && current.isNotEmpty()) {
            binding.editUsername.setText(current)
            binding.editUsername.setSelection(current.length)
        }

        binding.editUsername.requestFocus()

        binding.btnOkUsername.setOnClickListener { submit() }
        binding.fieldBtnOkUsername.setOnClickListener { submit() }

        binding.editUsername.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (binding.textInputLayout.error != null) {
                    binding.textInputLayout.error = null
                    viewModel.clearNicknameError()
                }
            }
        })

        observeState()
        observeEvents()
    }

    private fun submit() {
        val newName = binding.editUsername.text?.toString()?.trim().orEmpty()
        viewModel.updateNickname(newName)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.fieldBtnOkUsername.isEnabled = !state.isUpdatingNickname
                    binding.btnOkUsername.isEnabled = !state.isUpdatingNickname
                    binding.editUsername.isEnabled = !state.isUpdatingNickname

                    binding.textInputLayout.error = state.nicknameError
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    if (event is ProfileEvent.NicknameUpdated) {
                        dismissAllowingStateLoss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EditNicknameDialog"
    }
}