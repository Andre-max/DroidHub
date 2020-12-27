package com.andre_max.droidhub.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andre_max.droidhub.R
import com.andre_max.droidhub.databinding.HomeFragmentBinding
import com.andre_max.droidhub.databinding.RenameDialogBinding
import com.andre_max.droidhub.models.RemoteFile
import com.andre_max.droidhub.ui.base_folder_item.BaseFolderGroupItem
import com.andre_max.droidhub.ui.remote_file_item.RemoteFileGroupItem
import com.andre_max.droidhub.utils.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
class HomeFragment : Fragment() {

    lateinit var binding: HomeFragmentBinding

    val viewModel by viewModels<HomeViewModel>()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val itemClickListener: (RemoteFile) -> Unit = { clickRemoteFile ->
        Timber.d("itemClickListener invoked with clickRemoteFile as $clickRemoteFile")
        sendOpenFileIntent(clickRemoteFile)
    }
    private val longItemClickListener: (RemoteFileGroupItem, View) -> Boolean =
        { longRemoteFileGroupItem: RemoteFileGroupItem, anchorView: View ->
            Timber.d("longItemClickListener invoked with longRemoteFile as ${longRemoteFileGroupItem.remoteFile}")
            createDialog(longRemoteFileGroupItem, anchorView)
            true
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.yourFilesRecyclerview.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = groupAdapter
        }

        viewModel.shouldNavigate.observe(viewLifecycleOwner) {
            if (it == true) {
                findNavController().navigate(R.id.uploadFragment)
                viewModel.resetListOfRemoteFiles()
                viewModel.shouldNavigate.value = false
            }
        }

        viewModel.listOfRemoteFiles.observe(viewLifecycleOwner) { listOfRemoteFiles ->
            Timber.d("listOfRemoteFiles observed as $listOfRemoteFiles")
            binding.emptyFolderImage.visibility =
                if (listOfRemoteFiles?.isEmpty() == true) View.VISIBLE else View.GONE
            binding.emptyFolderText.visibility =
                if (listOfRemoteFiles?.isEmpty() == true) View.VISIBLE else View.GONE

            // The forEach will be executed only when the viewModel.listOfRemoteFiles is not null
            // If it is null, it will call loadBaseFolders()
            listOfRemoteFiles?.let {
                it.forEach { remoteFile ->
                    val remoteFileGroupItem =
                        RemoteFileGroupItem(remoteFile, itemClickListener, longItemClickListener)
                    groupAdapter.add(remoteFileGroupItem)
                }
            } ?: run {
                groupAdapter.clear()
                loadBaseFolders()
            }
        }
    }


    // I'm not really sure if this is a best practice, kindly leave a comment if any changes should be
    // done to make the code cleaner
    private fun sendOpenFileIntent(remoteFile: RemoteFile) {
        lifecycleScope.launch {
            FirebaseStorageUtils.getStorageUrlFromRemoteFile(remoteFile)
                .collect {
                    Timber.d("Url in sendOpenFileIntent() is $it")

                    try {
                        val intent = Intent(Intent.ACTION_VIEW, it)
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        showLongSnackbar("File cannot be opened. Reason: No app found to open it")
                        Timber.i("App not found. $e")
                    }
                }
        }
    }

    private fun createDialog(remoteFileGroupItem: RemoteFileGroupItem, anchorView: View) {
        val remoteFile = remoteFileGroupItem.remoteFile
        val popupMenu = PopupMenu(requireContext(), anchorView, Gravity.END)

        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.download_menu_item -> {
                    LocalStorageUtils.downloadRemoteFile(requireView(), remoteFile)
                    true
                }
                R.id.rename_menu_item -> {
                    createRenameDialog(remoteFileGroupItem)
                    true
                }
                R.id.delete_menu_item -> {
                    viewModel.deleteRemoteFile(remoteFile)
                    groupAdapter.removeGroupAtAdapterPosition(remoteFileGroupItem.index)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun createRenameDialog(remoteFileGroupItem: RemoteFileGroupItem) {
        val renameDialogBinding = RenameDialogBinding.inflate(layoutInflater)
        val renameDialog = AlertDialog.Builder(requireContext())
            .setView(renameDialogBinding.root)
            .create()

        renameDialogBinding.also {
            it.cancelBtn.setOnClickListener { renameDialog.dismiss() }
            it.proceedBtn.setOnClickListener {
                Timber.d("proceedBtn clicked!")
                val newDisplayName = renameDialogBinding.newDisplayNameInput.text.toString()
                FireStoreUtils.renameRemoteFile(remoteFileGroupItem.remoteFile, newDisplayName)

                groupAdapter.notifyItemChanged(remoteFileGroupItem.index, newDisplayName)
                renameDialog.dismiss()
            }
        }

        renameDialog.show()
    }


    private fun loadBaseFolders() {
        FileTypes.values().forEach { loopFileType ->
            val baseFolderGroupItem = BaseFolderGroupItem(loopFileType) {
                binding.storagePath.text = "Root  >  $it"
                groupAdapter.clear()
                viewModel.getRemoteFilesFromFileType(it)
            }
            groupAdapter.add(baseFolderGroupItem)
        }
    }
}