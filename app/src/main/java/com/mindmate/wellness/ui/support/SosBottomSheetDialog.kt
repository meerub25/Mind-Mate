package com.mindmate.wellness.ui.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindmate.wellness.databinding.DialogSosPanelBinding
import com.mindmate.wellness.ui.panic.PanicModeActivity

class SosBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: DialogSosPanelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogSosPanelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCallUmang.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:03117786264"))
            startActivity(intent)
        }

        binding.btnCallTaskeen.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:03168275336"))
            startActivity(intent)
        }

        binding.btnOpenPanicMode.setOnClickListener {
            dismiss()
            startActivity(Intent(requireContext(), PanicModeActivity::class.java))
        }

        binding.btnCloseSos.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
