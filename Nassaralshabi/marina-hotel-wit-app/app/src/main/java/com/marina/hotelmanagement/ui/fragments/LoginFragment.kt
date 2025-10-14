package com.marina.hotelmanagement.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.marina.hotelmanagement.HotelApplication
import com.marina.hotelmanagement.R
import com.marina.hotelmanagement.databinding.FragmentLoginBinding
import com.marina.hotelmanagement.ui.viewmodel.LoginViewModel
import com.marina.hotelmanagement.ui.viewmodel.factory.HotelViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel
    private lateinit var viewModelFactory: HotelViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        
        val repository = (requireActivity().application as HotelApplication).repository
            ?: throw IllegalStateException("Repository not initialized")
        
        viewModelFactory = HotelViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            
            viewModel.updateUsername(username)
            viewModel.updatePassword(password)
            viewModel.login()
        }

        binding.usernameEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.updateUsername(s.toString().trim())
            }
        })

        binding.passwordEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.updatePassword(s.toString().trim())
            }
        })
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.errorMessage.collect { errorMessage ->
                    if (errorMessage != null) {
                        binding.errorMessage.text = errorMessage
                        binding.errorMessage.visibility = View.VISIBLE
                    } else {
                        binding.errorMessage.visibility = View.GONE
                    }
                }
            }
            
            launch {
                viewModel.isLoading.collect { isLoading ->
                    binding.loginButton.isEnabled = !isLoading
                    binding.loginProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    binding.loginButton.text = if (isLoading) getString(R.string.message_loading) else getString(R.string.login_button_text)
                }
            }
            
            launch {
                viewModel.loginSuccess.collect { success ->
                    if (success) {
                        findNavController().navigate(R.id.action_login_to_dashboard)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}