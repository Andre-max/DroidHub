package com.andre_max.droidhub.ui.sign_up

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andre_max.droidhub.R
import com.andre_max.droidhub.databinding.SignUpFragmentBinding

class SignUpFragment : Fragment() {

    private val viewModel by viewModels<SignUpViewModel>()
    private lateinit var binding: SignUpFragmentBinding
    var isPasswordVisible = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignUpFragmentBinding.inflate(inflater).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val passwordInput = binding.signUpPasswordInput

        viewModel.shouldNavigate.observe(viewLifecycleOwner) {
            it?.let { _ ->
                findNavController().navigate(it)
                viewModel.shouldNavigate.value = null
            }
        }

        passwordInput.transformationMethod = PasswordTransformationMethod()
        binding.signUpChangeVisibility.setOnClickListener {
            isPasswordVisible = isPasswordVisible != true
            val start = passwordInput.selectionStart
            val end = passwordInput.selectionEnd

            if (isPasswordVisible) {
                passwordInput.transformationMethod = null
                binding.signUpChangeVisibility.setImageResource(R.drawable.ic_visibility_off)
            } else {
                passwordInput.transformationMethod = PasswordTransformationMethod()
                binding.signUpChangeVisibility.setImageResource(R.drawable.ic_visibility_on)
            }

            passwordInput.setSelection(start, end)
        }
    }
}