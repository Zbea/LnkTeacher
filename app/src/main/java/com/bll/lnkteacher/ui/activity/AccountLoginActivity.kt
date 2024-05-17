package com.bll.lnkteacher.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.mvp.presenter.LoginPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.utils.ActivityManager
import com.bll.lnkteacher.utils.MD5Utils
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_login_user.*

class AccountLoginActivity:BaseActivity(), IContractView.ILoginView {

    private val presenter=LoginPresenter(this,1)
    private var token=""

    override fun getLogin(user: User?) {
        token= user?.token.toString()
        SPUtil.putString("token",token)
        presenter.accounts()
    }

    override fun getAccount(user: User?) {
        user?.token=token
        SPUtil.putObj("user",user!!)

        val intent = Intent()
        intent.putExtra("token", token)
        intent.putExtra("userId", user.accountId)
        intent.action = Constants.LOGIN_BROADCAST_EVENT
        sendBroadcast(intent)

        MethodManager.getUser()

        val intent1=Intent(this,MainActivity::class.java)
        intent1.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
        intent1.flags=Intent.FLAG_ACTIVITY_TASK_ON_HOME
        startActivity(intent1)
        ActivityManager.getInstance().finishOthers(MainActivity::class.java)

        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_login_user
    }

    override fun initData() {
        initDialog(1)
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        val account=SPUtil.getString("account")
        val password=SPUtil.getString("password")

        ed_user.setText(account)
        ed_psw.setText(password)

        tv_register.setOnClickListener {
            startActivityForResult(Intent(this, AccountRegisterActivity::class.java).setFlags(0), 0)
        }

        tv_find_psd.setOnClickListener {
            startActivityForResult(Intent(this, AccountRegisterActivity::class.java).setFlags(1), 0)
        }

        btn_login.setOnClickListener {
            val account = ed_user.text.toString()
            val psdStr=ed_psw.text.toString()
            val password = MD5Utils.digest(psdStr)

            SPUtil.putString("account",account)
            SPUtil.putString("password",psdStr)

            val map=HashMap<String,Any>()
            map ["account"]=account
            map ["password"]=password
            map ["role"]= 1

            presenter.login(map)

        }

        val tokenStr=SPUtil.getString("token")

        if (tokenStr.isNotEmpty() && mUser!=null)
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        if (NetworkUtil.isNetworkAvailable(this)) {
            disMissView(tv_tips)
        } else {
            showView(tv_tips)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            ed_user.setText(data?.getStringExtra("user"))
            ed_psw.setText(data?.getStringExtra("psw"))
        }

    }


}