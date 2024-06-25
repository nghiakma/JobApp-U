package com.example.jobapp_u.auth.fragments


import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.jobapp_u.R
import com.example.jobapp_u.auth.viewmodel.AuthViewModel
import com.example.jobapp_u.databinding.FragmentLoginBinding
import com.example.jobapp_u.home.activity.HomeActivity
import com.example.jobapp_u.user_detail.UserDetailActivity
import com.example.jobapp_u.util.*
import com.example.jobapp_u.util.Constants.Companion.ROLE_TYPE_STUDENT
import com.example.jobapp_u.util.InputValidation
import com.example.jobapp_u.util.LoadingDialog
import com.example.jobapp_u.util.Status
import com.example.jobapp_u.util.showToast


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val authViewModel by viewModels<AuthViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            tvSignup.text = createSignupText()

            tvSignup.setOnClickListener {
                navigateToSignup()
            }

            tvForgetPassword.setOnClickListener {
                navigateToForgotPassword()
            }

            etEmailContainer.addTextWatcher()
            etPasswordContainer.addTextWatcher()

            btnLogin.setOnClickListener {
                val email = etEmail.getInputValue()
                val password = etPassword.getInputValue()
                if (detailVerification(email, password)) {
                    authViewModel.login(email, password)
                    clearField()
                }
            }
        }
    }

    private fun setupObserver() {
        authViewModel.loginStatus.observe(viewLifecycleOwner) { loginState ->
            when (loginState.status) {
                Status.LOADING -> {
                    loadingDialog.show()
                }
                Status.SUCCESS -> {
                    val currentUser = loginState.data!!
                    if (currentUser.roleType == ROLE_TYPE_STUDENT) {
                        if (currentUser.userInfoExist) {
                            navigateToHomeActivity()
                        } else {
                            navigateToUserDetail(
                                username = currentUser.username,
                                email = currentUser.email
                            )
                        }
                        showToast(requireContext(), getString(R.string.auth_pass))
                    } else {
                        showToast(requireContext(), "Account doesn't exist")
                    }
                    loadingDialog.dismiss()
                }
                Status.ERROR -> {
                    showToast(requireContext(), loginState.message.toString())
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun createSignupText(): SpannableString {
        val signupText = SpannableString(getString(R.string.sign_up_prompt))
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val signupColor = ForegroundColorSpan(color)
        signupText.setSpan(UnderlineSpan(), 31, signupText.length, 0)
        signupText.setSpan(signupColor, 31, signupText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return signupText
    }

    private fun clearField() {
        binding.etEmail.clearText()
        binding.etPassword.clearText()
    }


    private fun navigateToHomeActivity() {
        val homeActivity = Intent(requireContext(), HomeActivity::class.java)
        startActivity(homeActivity)
        requireActivity().finish()
    }

    private fun navigateToUserDetail(username: String, email: String) {
        val userDetailActivity = Intent(requireContext(), UserDetailActivity::class.java)
        userDetailActivity.putExtra("USERNAME", username)
        userDetailActivity.putExtra("EMAIL", email)
        requireActivity().startActivity(userDetailActivity)
        requireActivity().finish()
    }

    private fun navigateToForgotPassword() {
        findNavController().navigate(R.id.action_loginFragment_to_forgotPassFragment)
    }

    private fun navigateToSignup() {
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }


    private fun detailVerification(
        email: String,
        password: String
    ): Boolean {
        binding.apply {
            val (isEmailValid, emailError) = InputValidation.isEmailValid(email)
            if (isEmailValid.not()) {
                etEmailContainer.error = emailError
                return isEmailValid
            }

            val (isPasswordValid, passwordError) = InputValidation.isPasswordValid(password)
            if (isPasswordValid.not()) {
                etPasswordContainer.error = passwordError
                return isPasswordValid
            }
            return true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}