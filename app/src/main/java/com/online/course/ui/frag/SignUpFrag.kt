package com.online.course.ui.frag

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
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
import com.online.course.R
import com.online.course.databinding.FragSignUpBinding
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.manager.ResponseStatus
import com.online.course.manager.ToastMaker
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.SignUpPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.UserAuthFrag
import com.online.course.ui.widget.LoadingDialog
import org.json.JSONException


class SignUpFrag : UserAuthFrag(), SelectionDialog.ItemSelection<Country>{

    private lateinit var mPresenter: Presenter.SignUpPresenter
    private lateinit var mBinding: FragSignUpBinding

    private var mCountry: Country? = null
    private var mIsEmail = false

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
        mBinding = FragSignUpBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        mGoogleBtn = mBinding.signUpGoogleLayout
        mFacebookBtn = mBinding.signUpFacebookLayout

        mBinding.signUpCountryImg.setOnClickListener(this)
        mBinding.signUpCreateAccountBtn.setOnClickListener(this)
        mBinding.signUpSignInBtn.setOnClickListener(this)
        mBinding.signUpEmailPhoneEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.signUpPasswordEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.signUpRetypePasswordEdtx.addTextChangedListener(mInputTextWatcher)
        mPresenter = SignUpPresenterImpl(this)

        if (App.appConfig.registrationMethod == App.Companion.Registration.EMAIL.value()) {
            mIsEmail = true
            mBinding.signUpCountryImg.visibility = View.GONE
            mBinding.signUpCountryImg.visibility = View.GONE
            mBinding.signUpEmailPhoneEdtx.hint = getString(R.string.email)
            mBinding.signUpEmailPhoneEdtx.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v?.id) {
            R.id.signUpCreateAccountBtn -> {
                if (!arePasswordFieldsEqual())
                    return

                val password = mBinding.signUpPasswordEdtx.text.toString()
                val passwordConfirmation = mBinding.signUpRetypePasswordEdtx.text.toString()

                if (mIsEmail) {
                    val email = mBinding.signUpEmailPhoneEdtx.text.toString()
                    val signUp = EmailSignUp()
                    signUp.password = password
                    signUp.passwordConfirmation = passwordConfirmation
                    signUp.email = email
                    mPresenter.signUp(signUp)
                } else {
                    val mobile = mBinding.signUpEmailPhoneEdtx.text.toString()
                    val signUp = MobileSignUp()
                    signUp.password = password
                    signUp.passwordConfirmation = passwordConfirmation
                    signUp.countryCode = mCountry?.callingCode!!
                    signUp.mobile = mobile
                    mPresenter.signUp(signUp)
                }
            }

            R.id.signUpCountryImg -> {
                val bundle = Bundle()
                bundle.putSerializable(App.SELECTION_TYPE, SelectionDialog.Selection.Country)

                val instance = SelectionDialog.getInstance<Country>()
                instance.setOnItemSelected(this)
                instance.arguments = bundle
                instance.show(childFragmentManager, null)
            }


            R.id.signUpSignInBtn -> {
                val frag = SignInFrag()
                parentFragmentManager.beginTransaction().replace(android.R.id.content, frag)
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

//    fun onCustomerSaved(response: retrofit2.Response<Customer>) {
//        val customer = response.body()
//        if (customer != null) {
//    if (customer.via == App.RegistrationProvider.GOOGLE.value())
//    {
//        mGoogleSignInClient.signOut()
//    } else if (customer.via == App.RegistrationProvider.FACEBOOK.value())
//    {
//        LoginManager.getInstance().logOut()
//    }
//
//            App.setCustomer(customer)
//            ApiService.createAuthorizedApiService(requireContext(), customer.token)
//            context?.let { AppKotlin.saveTokenAndEmaileOrMobile(it, customer) }
//
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            intent.putExtra(App.IS_LOGIN, true)
//            intent.putExtra(App.SHOULD_REGISTER, customer.shouldRegister())
//            intent.putExtra(App.TOKEN, customer.token)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//            activity?.finish()
//        } else {
//            mLoadingDialog.dismiss()
//            val rsp = Gson().fromJson<BaseResponse>(
//                response.errorBody()?.string(),
//                BaseResponse::class.java
//            )
//            Toast.makeText(requireContext(), rsp.message, Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun enableDisableLoginBtn() {
        val username = mBinding.signUpEmailPhoneEdtx.text.toString()
        val password = mBinding.signUpPasswordEdtx.text.toString()
        val passwordRetype = mBinding.signUpRetypePasswordEdtx.text.toString()
        val loginBtn = mBinding.signUpCreateAccountBtn
        loginBtn.isEnabled = false

        if (username.isNotEmpty() && password.isNotEmpty() && passwordRetype.isNotEmpty()) {

            if (mIsEmail) {
                val isValidEmail = username.length >= 3 && username.contains("@")
                        && username.contains(".")
                if (isValidEmail) {
                    loginBtn.isEnabled = true
                }

            } else {
                var enable = true

                if (mCountry != null) {
                    for ((index, char) in username.withIndex()) {
                        if (index == 0 && char == '+') {
                            continue
                        }

                        if (!char.isDigit()) {
                            enable = false
                            break
                        }
                    }
                } else {
                    enable = false
                }

                loginBtn.isEnabled = enable
            }

        } else if (loginBtn.isEnabled) {
            loginBtn.isEnabled = false
        }
    }


    private fun arePasswordFieldsEqual(): Boolean {
        val password = mBinding.signUpPasswordEdtx.text.toString()
        val passwordRetype = mBinding.signUpRetypePasswordEdtx.text.toString()
        if (password.isNotEmpty() && passwordRetype.isNotEmpty() && password != passwordRetype) {
            ToastMaker.show(
                requireContext(),
                getString(R.string.password),
                getString(R.string.make_sure_passwords_are_the_same),
                ToastMaker.Type.ERROR
            )

            return false
        }

        return true
    }


    override fun onItemSelected(country: Country) {
        mCountry = country
        country.img?.let { mBinding.signUpCountryImg.setImageResource(it) }
        enableDisableLoginBtn()
    }

    fun onUserBasicsSaved(
        data: Data<User>,
        emailSignUp: EmailSignUp? = null,
        mobileSignUp: MobileSignUp? = null
    ) {
        if (data.isSuccessful || data.status == ResponseStatus.AUTH_GO_TO_STEP2.value()) {
            val frag = VerifyAccountFrag()
            val bundle = Bundle()
            bundle.putInt(App.USER_ID, data.data!!.userId)
            if (emailSignUp != null) {
                bundle.putParcelable(App.SIGN_UP, emailSignUp)
            } else {
                bundle.putParcelable(App.SIGN_UP, mobileSignUp)
            }
            frag.arguments = bundle
            parentFragmentManager.beginTransaction().replace(android.R.id.content, frag).commit()
        } else if (data.status == ResponseStatus.AUTH_GO_TO_STEP3.value()) {

            activity?.finish()

            if (mSignInRequest) {
                val intent = Intent()
                intent.putExtra(App.SHOULD_REGISTER, true)
                intent.putExtra(App.USER_ID, data.data!!.userId)
                activity?.setResult(Activity.RESULT_OK, intent)
            } else {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra(App.SHOULD_REGISTER, true)
                intent.putExtra(App.USER_ID, data.data!!.userId)
                startActivity(intent)
            }
        } else {
            onErrorOccured(data)
        }
    }
}