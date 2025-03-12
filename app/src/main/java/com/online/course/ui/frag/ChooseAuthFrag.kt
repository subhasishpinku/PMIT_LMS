package com.online.course.ui.frag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.ChooseAuthFragBinding
import com.online.course.manager.App
import com.online.course.manager.PrefManager
import com.online.course.ui.MainActivity

class ChooseAuthFrag : Fragment(), View.OnClickListener {

    private lateinit var mBinding: ChooseAuthFragBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = ChooseAuthFragBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.signInBtn.setOnClickListener(this)
        mBinding.signUpBtn.setOnClickListener(this)
        mBinding.skipLoginBtn.setOnClickListener(this)

        val signInRequest = arguments?.getBoolean(App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP, false)
        if (signInRequest != null && signInRequest)
            signIn(true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signInBtn -> {
                signIn()
            }

            R.id.signUpBtn -> {
                signUp()
            }

            R.id.skipLoginBtn -> {
                val prefManager = PrefManager(requireContext())
                prefManager.skipLogin = true

                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra(App.IS_LOGIN, true)

                startActivity(intent)
                activity?.finish()
            }
        }
    }

    private fun signUp() {
        val instance = SignUpFrag()
        parentFragmentManager.beginTransaction().addToBackStack(null)
            .replace(android.R.id.content, instance).commit()
    }

    private fun signIn(signInRequest: Boolean = false) {
        val bundle = Bundle()
        bundle.putBoolean(App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP, signInRequest)

        val instance = SignInFrag()
        instance.arguments = bundle

        var replace = parentFragmentManager.beginTransaction()
            .replace(android.R.id.content, instance)

        if (!signInRequest)
            replace = replace.addToBackStack(null)

        replace.commit()
    }

}