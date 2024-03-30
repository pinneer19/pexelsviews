package com.example.pexelsviews.presentation.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.pexelsviews.databinding.NetworkAlertBinding


class AlertDialogFragment(private val onRefreshList: () -> Unit) : DialogFragment() {
    private lateinit var binding: NetworkAlertBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NetworkAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.refreshButton.setOnClickListener {
            onRefreshList()
            dismiss()
        }
    }
}