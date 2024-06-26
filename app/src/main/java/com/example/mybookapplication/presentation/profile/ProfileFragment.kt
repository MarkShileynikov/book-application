package com.example.mybookapplication.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.mybookapplication.R
import com.example.mybookapplication.databinding.FragmentProfileBinding
import com.example.mybookapplication.domain.entity.UserProfile
import com.example.mybookapplication.domain.util.Event
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userProfile: UserProfile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        viewModel.fetchUserProfile()
        observeUserProfile()
    }

    private fun bindViews() {
        binding.settingsButton.setOnClickListener {
            moveToSettingsScreen()
        }
    }

    private fun observeUserProfile() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect {
                    when(it) {
                        is Event.Success -> {
                            userProfile = it.data
                            setUpProfile(userProfile)
                        }
                        is Event.Failure -> {

                        }
                    }
                }
            }
        }
    }

    private fun setUpProfile(userProfile : UserProfile) {
        binding.userName.text = userProfile.username
        binding.authorizationButton.visibility = View.GONE
        if (userProfile.avatar != "") {
            binding.avatar.load(userProfile.avatar) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }
    }

    private fun moveToSettingsScreen() {
        findNavController().navigate(R.id.action_profileFragment_to_fragmentSettings)
    }

}