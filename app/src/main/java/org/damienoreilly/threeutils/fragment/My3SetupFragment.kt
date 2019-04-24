package org.damienoreilly.threeutils.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.damienoreilly.threeutils.databinding.FragmentMy3SetupBinding
import org.damienoreilly.threeutils.viewmodel.My3SetupViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class My3SetupFragment : Fragment() {

    private lateinit var binding: FragmentMy3SetupBinding
    private val my3SetupViewModel: My3SetupViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMy3SetupBinding
                .inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = my3SetupViewModel
        return binding.root
    }


}
