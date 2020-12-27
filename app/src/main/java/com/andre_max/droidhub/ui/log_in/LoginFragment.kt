package com.andre_max.droidhub.ui.log_in

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andre_max.droidhub.R
import com.andre_max.droidhub.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()
    lateinit var binding: LoginFragmentBinding
    var isPasswordVisible = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val passwordInput = binding.logInPasswordInput

        viewModel.shouldNavigate.observe(viewLifecycleOwner) {
            it?.let { _ ->
                findNavController().navigate(it)
                viewModel.shouldNavigate.value = null
            }
        }

        passwordInput.transformationMethod = PasswordTransformationMethod()
        binding.logInChangeVisibility.setOnClickListener {
            isPasswordVisible = isPasswordVisible != true
            val start = passwordInput.selectionStart
            val end = passwordInput.selectionEnd

            if (isPasswordVisible) {
                passwordInput.transformationMethod = null
                binding.logInChangeVisibility.setImageResource(R.drawable.ic_visibility_off)
            } else {
                passwordInput.transformationMethod = PasswordTransformationMethod()
                binding.logInChangeVisibility.setImageResource(R.drawable.ic_visibility_on)
            }
            passwordInput.setSelection(start, end)
        }
    }
}