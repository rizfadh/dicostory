package com.rizfadh.dicostory.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.rizfadh.dicostory.R
import com.rizfadh.dicostory.databinding.FragmentLoginBinding
import com.rizfadh.dicostory.ui.main.MainActivity
import com.rizfadh.dicostory.utils.Result
import com.rizfadh.dicostory.utils.ViewModelFactory
import com.rizfadh.dicostory.utils.alert

class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelFactory: ViewModelFactory
    private val authViewModel: AuthViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory = ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startAnimation()

        authViewModel.loginResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showView(false)
                    showLoading(true)
                }
                is Result.Success -> {
                    val token = it.data.loginResult?.token.toString()
                    authViewModel.saveUserToken(token).observe(viewLifecycleOwner) { result ->
                        if (result is Result.Success) {
                            val mainIntent = Intent(requireActivity(), MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(mainIntent)
                            requireActivity().overridePendingTransition(0, 0)
                        }
                    }
                }
                is Result.Error -> {
                    showView(true)
                    showLoading(false)
                    alert(requireActivity(), getString(R.string.error), it.error)
                }
                else -> {}
            }
        }

        binding.btnLogin.setOnClickListener(this)
        binding.btnToRegister.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_to_register -> {
                parentFragmentManager.commit {
                    replace(R.id.fragment_auth_container, RegisterFragment())
                }
            }
            R.id.btn_login -> login()
        }
    }

    private fun isInputValid(): Boolean {
        val edEmail = binding.edLoginEmail
        val edPassword = binding.edLoginPassword
        return edEmail.error.isNullOrEmpty() && edPassword.error.isNullOrEmpty()
    }

    private fun login() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        val emptyMessage = getString(R.string.required)

        when {
            email.isEmpty() -> binding.edLoginEmail.error = emptyMessage
            password.isEmpty() -> binding.edLoginPassword.error = emptyMessage
            else -> if (isInputValid()) authViewModel.login(email, password)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showView(isShowing: Boolean) {
        val viewVisibility = if (isShowing) View.VISIBLE else View.INVISIBLE

        binding.apply {
            ivLoginDecoration.visibility = viewVisibility
            edLoginEmail.visibility = viewVisibility
            edLoginPassword.visibility = viewVisibility
            btnLogin.visibility = viewVisibility
            btnToRegister.visibility = viewVisibility
        }
    }

    private fun startAnimation() {
        val ivDecoration = ObjectAnimator.ofFloat(binding.ivLoginDecoration, View.ALPHA, 1f).setDuration(200)
        val edEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(200)
        val edPassword = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(200)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(200)
        val btnToRegister = ObjectAnimator.ofFloat(binding.btnToRegister, View.ALPHA, 1f).setDuration(200)
        AnimatorSet().apply {
            playSequentially(ivDecoration, edEmail, edPassword, btnLogin, btnToRegister)
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}