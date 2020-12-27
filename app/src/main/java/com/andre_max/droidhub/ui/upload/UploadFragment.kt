package com.andre_max.droidhub.ui.upload

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andre_max.droidhub.MainActivity
import com.andre_max.droidhub.databinding.UploadFragmentBinding
import com.andre_max.droidhub.utils.LocalStorageUtils
import com.andre_max.droidhub.utils.Status
import com.andre_max.droidhub.utils.showLongSnackbar


class UploadFragment : Fragment() {

    private val viewModel: UploadViewModel by viewModels()
    private lateinit var binding: UploadFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UploadFragmentBinding.inflate(inflater).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).intentUri?.let {
            viewModel.uploadFileUri.value = it
        }

        viewModel.uploadFileUri.observe(viewLifecycleOwner) {
            it?.also {
                binding.fileSelectedText.apply {
                    text = LocalStorageUtils.getLocalFileNameFromUri(it)
                    visibility = View.VISIBLE
                }
            }
        }

        viewModel.startFilePickerIntent.observe(viewLifecycleOwner) {
            if (it == true) {
                sendIntent()
                viewModel.resetFilePickerIntentToDefault()
            }
        }

        viewModel.status.observe(viewLifecycleOwner) {
            if (it == Status.Completed) {
                findNavController().popBackStack()
            }
        }

        viewModel.snackbarMessage.observe(viewLifecycleOwner) {
            it?.let {
                showLongSnackbar(it)
                viewModel.snackbarMessage.value = null
            }
        }
    }

    private fun sendIntent() {
        // Create an intent to the filePicker
        val intentChooser = Intent.createChooser(
            Intent(Intent.ACTION_GET_CONTENT).setType("*/*"),
            "Choose a file"
        )
        startActivityForResult(intentChooser, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            viewModel.uploadFileUri.value = data.data
        }
    }

    companion object {
        const val REQUEST_CODE = 1234
    }
}