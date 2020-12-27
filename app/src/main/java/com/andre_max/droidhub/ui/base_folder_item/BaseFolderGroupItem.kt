package com.andre_max.droidhub.ui.base_folder_item

import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import com.andre_max.droidhub.R
import com.andre_max.droidhub.databinding.BaseFolderLayoutBinding
import com.andre_max.droidhub.utils.FileTypes
import com.xwray.groupie.viewbinding.BindableItem


class BaseFolderGroupItem(
    private val fileType: FileTypes,
    val baseFolderLambda: (FileTypes) -> Unit
) : BindableItem<BaseFolderLayoutBinding>() {
    override fun bind(binding: BaseFolderLayoutBinding, position: Int) {
        binding.baseFolderButton.also {
            it.text = fileType.name
            it.setOnClickListener { baseFolderLambda(fileType) }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                it.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    binding.getDrawableFromDrawableId(), null, null, null
                )
            else
                it.setCompoundDrawablesWithIntrinsicBounds(
                    binding.getDrawableFromDrawableId(),
                    null,
                    null,
                    null
                )
        }
    }


    override fun getLayout(): Int = R.layout.base_folder_layout

    override fun initializeViewBinding(view: View) =
        BaseFolderLayoutBinding.bind(view)

    private fun BaseFolderLayoutBinding.getDrawableFromDrawableId() =
        ContextCompat.getDrawable(root.context, fileType.getDrawable())

}