package org.damienoreilly.threeutils.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.damienoreilly.threeutils.databinding.FragmentThreePlusSetupBinding
import org.damienoreilly.threeutils.viewmodel.ThreePlusSetupViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ThreePlusSetupFragment : Fragment() {

    private lateinit var binding: FragmentThreePlusSetupBinding
    private val threePlusSetupViewModel: ThreePlusSetupViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentThreePlusSetupBinding
                .inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = threePlusSetupViewModel

        return binding.root
    }

}
