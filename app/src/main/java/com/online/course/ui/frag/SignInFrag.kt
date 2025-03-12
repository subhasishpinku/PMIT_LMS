package com.online.course.ui.frag

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.online.course.R
import com.online.course.databinding.FragSignInBinding
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.manager.db.AppDb
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.ApiService
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.CommonApiPresenterImpl
import com.online.course.presenterImpl.SignInPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.UserAuthFrag
import com.online.course.ui.widget.LoadingDialog
import org.json.JSONException


class SignInFrag : UserAuthFrag() {

    private lateinit var mPresenter: Presenter.SignInPresenter
    private lateinit var mBinding: FragSignInBinding

    private val mInputTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableLoginBtn()
        }
    }

    companion object {
        private const val TAG = "SignInFrag"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSignInBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        mGoogleBtn = mBinding.signInGoogleLayout
        mFacebookBtn = mBinding.signInFacebookLayout

        if (arguments != null) {
            mSignInRequest =
                requireArguments().getBoolean(App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP, false)
        }

        mBinding.signInLoginBtn.setOnClickListener(this)
        mBinding.signInSignUpBtn.setOnClickListener(this)
        mBinding.signInForgotPasswordBtn.setOnClickListener(this)
        mBinding.signInEmailPhoneEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.signInPasswordEdtx.addTextChangedListener(mInputTextWatcher)
        mPresenter = SignInPresenterImpl(this)
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v?.id) {
            R.id.signInLoginBtn -> {
                mLoadingDialog = LoadingDialog.instance
                mLoadingDialog!!.show(childFragmentManager, null)
                val username = mBinding.signInEmailPhoneEdtx.text.toString()
                val password = mBinding.signInPasswordEdtx.text.toString()

                val loginObj = Login()
                loginObj.username = username
                loginObj.password = password

                mPresenter.login(loginObj)
            }

            R.id.signInForgotPasswordBtn -> {
                parentFragmentManager.beginTransaction()
                    .replace(android.R.id.content, ForgetPasswordFrag())
                    .addToBackStack(null)
                    .commit()
            }

            R.id.signInSignUpBtn -> {
                val bundle = Bundle()
                bundle.putBoolean(App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP, mSignInRequest)

                val signUpFrag = SignUpFrag()
                signUpFrag.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(android.R.id.content, signUpFrag)
                    .commit()
            }
        }
    }

//    fun onCodeSent(response: Response, email: String?, mobile: String?) {
//        if (response.isSuccessful) {
//            val frag = VerifyCodeFrag.getInstance()
//            val bundle = Bundle()
//            bundle.putString(App.TOKEN, response.token)
//            bundle.putInt(App.CODE_VAL_TIME, response.codeValidationTime)
//            if (email != null)
//                bundle.putString(App.EMAIL, email)
//
//            if (mobile != null)
//                bundle.putString(App.MOBILE, mobile)
//
//            frag.arguments = bundle
//            parentFragmentManager.beginTransaction().replace(android.R.id.content, frag).commit()
//
//        } else {
//            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {

            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.

            val login = ThirdPartyLogin()
            login.email = account.email!!
            login.id = account.id!!
            login.name = account.displayName!!

            mLoadingDialog = LoadingDialog.instance
            mLoadingDialog?.show(childFragmentManager, null)
            mPresenter.googleSignInUp(login)

        } catch (e: ApiException) {
            mLoadingDialog?.dismiss()

            val rsp = BaseResponse()
            rsp.message = getString(R.string.sign_in_failed)
            onErrorOccured(rsp)

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            if (BuildVars.LOGS_ENABLED)
                Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    override fun getFacebookSignInCallback(): FacebookCallback<LoginResult> {
        return object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result != null) {
                    val request = GraphRequest.newMeRequest(
                        result.accessToken
                    ) { _, response ->
                        if (response != null) {
                            try {
                                val email = response.jsonObject.getString("email")
                                val name = response.jsonObject.getString("name")

                                if (email.isNullOrEmpty()) {
                                    val rsp = BaseResponse()
                                    rsp.message =
                                        getString(R.string.no_email_associated_with_this_account)
                                    onErrorOccured(rsp)
                                    return@newMeRequest
                                }

                                val login = ThirdPartyLogin()
                                login.email = email
                                login.id = result.accessToken.userId
                                login.name = name

                                mLoadingDialog?.dismiss()
                                mLoadingDialog = LoadingDialog.instance
                                mLoadingDialog?.show(childFragmentManager, null)
                                mPresenter.facebookSignInUp(login)

                            } catch (ex: JSONException) {
                                val rsp = BaseResponse()
                                rsp.message = getString(R.string.sign_in_failed)
                                onErrorOccured(rsp)
                            }
                        }
                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email")
                    request.parameters = parameters
                    request.executeAsync()
                }
            }

            override fun onCancel() {
                val rsp = BaseResponse()
                rsp.message = getString(R.string.sign_in_canceled)
                onErrorOccured(rsp)
            }

            override fun onError(error: FacebookException?) {
                val rsp = BaseResponse()
                rsp.message = getString(R.string.sign_in_failed)
                onErrorOccured(rsp)
            }
        }
    }


    fun onSuccessfulLogin(response: Data<Response>) {
        if (response.isSuccessful) {
//            App.setCustomer(customer)
//            context?.let { AppKotlin.saveTokenAndEmaileOrMobile(it, customer) }
            val token = response.data!!.token
            App.saveToLocal(token, requireContext(), AppDb.DataType.TOKEN)
            ApiService.createAuthorizedApiService(token)

            val commonPresenter = CommonApiPresenterImpl.getInstance()
            commonPresenter.getUserInfo(response.data!!.userId, object : ItemCallback<UserProfile> {
                override fun onItem(item: UserProfile, vararg args: Any) {
                    App.saveToLocal(
                        Gson().toJson(item),
                        requireContext(),
                        AppDb.DataType.USER
                    )
                    App.loggedInUser = item

                    if (mSignInRequest) {
                        val intent = Intent()
                        intent.putExtra(
                            App.SHOULD_REGISTER,
                            !response.data!!.profileState.isNullOrEmpty()
                        )
                        activity?.setResult(Activity.RESULT_OK, intent)
                    } else {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.putExtra(
                            App.SHOULD_REGISTER,
                            !response.data!!.profileState.isNullOrEmpty()
                        )
                        startActivity(intent)
                    }

                    activity?.finish()
                }

            })
        } else {
            mLoadingDialog?.dismiss()
            onErrorOccured(response)
        }
    }


    private fun enableDisableLoginBtn() {
        val username = mBinding.signInEmailPhoneEdtx.text.toString()
        val password = mBinding.signInPasswordEdtx.text.toString()
        val loginBtn = mBinding.signInLoginBtn

        if (username.isNotEmpty() && password.isNotEmpty()) {
            var checkForEmail = false

            for ((index, char) in username.withIndex()) {
                if (index == 0 && char == '+')
                    continue

                if (!char.isDigit()) {
                    checkForEmail = true
                    break
                }
            }

            if (checkForEmail) {
                val isValidEmail = username.length >= 3 && username.contains("@")
                        && username.contains(".")
                if (isValidEmail)
                    loginBtn.isEnabled = true
            } else {
                loginBtn.isEnabled = true
            }

        } else if (loginBtn.isEnabled) {
            loginBtn.isEnabled = false
        }
    }
}