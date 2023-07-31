package com.rizfadh.dicostory.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.rizfadh.dicostory.R
import com.rizfadh.dicostory.databinding.FragmentRegisterBinding
import com.rizfadh.dicostory.utils.Result
import com.rizfadh.dicostory.utils.ViewModelFactory
import com.rizfadh.dicostory.utils.alert

class RegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelFactory: ViewModelFactory
    private val authViewModel: AuthViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory = ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startAnimation()

        authViewModel.registerResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showView(false)
                    showLoading(true)
                }
                is Result.Success -> {
                    showView(true)
                    showLoading(false)
                    alert(requireActivity(), getString(R.string.success), it.data.message)
                    parentFragmentManager.commit {
                        replace(R.id.fragment_auth_container, LoginFragment())
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

        binding.btnRegister.setOnClickListener(this)
        binding.btnToLogin.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_to_login -> parentFragmentManager.commit {
                replace(R.id.fragment_auth_container, LoginFragment())
            }
            R.id.btn_register -> register()
        }
    }

    private fun isInputValid(): Boolean {
        val edEmail = binding.edRegisterEmail
        val edPassword = binding.edRegisterPassword
        return edEmail.error.isNullOrEmpty() && edPassword.error.isNullOrEmpty()
    }

    private fun register() {
        val name = binding.edRegisterName.text.toString().trim()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        val emptyMessage = getString(R.string.required)

        when {
            name.isEmpty() -> binding.edRegisterName.error = emptyMessage
            email.isEmpty() -> binding.edRegisterEmail.error = emptyMessage
            password.isEmpty() -> binding.edRegisterPassword.error = emptyMessage
            else -> if (isInputValid()) authViewModel.register(name, email, password)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showView(isShowing: Boolean) {
        val viewVisibility = if (isShowing) View.VISIBLE else View.INVISIBLE

        binding.apply {
            ivRegisterDecoration.visibility = viewVisibility
            edRegisterName.visibility = viewVisibility
            edRegisterEmail.visibility = viewVisibility
            edRegisterPassword.visibility = viewVisibility
            btnRegister.visibility = viewVisibility
            btnToLogin.visibility = viewVisibility
        }
    }

    private fun startAnimation() {
        val ivDecoration = ObjectAnimator.ofFloat(binding.ivRegisterDecoration, View.ALPHA, 1f).setDuration(200)
        val edName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(200)
        val edEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(200)
        val edPassword = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(200)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(200)
        val btnToLogin = ObjectAnimator.ofFloat(binding.btnToLogin, View.ALPHA, 1f).setDuration(200)
        AnimatorSet().apply {
            playSequentially(ivDecoration, edName, edEmail, edPassword, btnRegister, btnToLogin)
            start()
        }
    }
}