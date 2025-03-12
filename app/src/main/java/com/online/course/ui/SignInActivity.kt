package com.online.course.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.online.course.R
import com.online.course.databinding.ActivitySignInBinding
import com.online.course.manager.App
import com.online.course.manager.PrefManager
import com.online.course.ui.frag.ChooseAuthFrag
import com.online.course.ui.frag.SignInFrag
import com.online.course.ui.frag.SignUpFrag

class SignInActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = Bundle()
        intent?.getBooleanExtra(App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP, false)?.let {
            bundle.putBoolean(
                App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP,
                it
            )
        }

        val frag = ChooseAuthFrag()
        frag.arguments = bundle

        supportFragmentManager.beginTransaction().replace(android.R.id.content, frag).commit()
    }
}