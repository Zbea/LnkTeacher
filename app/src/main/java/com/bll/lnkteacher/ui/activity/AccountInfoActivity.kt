package com.bll.lnkteacher.ui.activity

import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.*
import com.bll.lnkteacher.mvp.model.FriendList
import com.bll.lnkteacher.mvp.model.SchoolBean
import com.bll.lnkteacher.mvp.presenter.AccountInfoPresenter
import com.bll.lnkteacher.mvp.presenter.SchoolPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.ISchoolView
import com.bll.lnkteacher.ui.adapter.AccountFriendAdapter
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_info.*

class AccountInfoActivity:BaseActivity(), IContractView.IAccountInfoView,ISchoolView {

    private lateinit var mSchoolPresenter:SchoolPresenter
    private lateinit var presenter:AccountInfoPresenter
    private var nickname=""
    private var school=0
    private var schoolBean: SchoolBean?=null
    private var schools= mutableListOf<SchoolBean>()
    private var schoolSelectDialog:SchoolSelectDialog?=null
    private var friends= mutableListOf<FriendList.FriendBean>()
    private var mAdapterFriend: AccountFriendAdapter?=null
    private var position=0

    override fun onEditNameSuccess() {
        showToast("修改姓名成功")
        mUser?.nickname=nickname
        tv_name.text = nickname
    }

    override fun onEditSchool() {
        mUser?.schoolId = schoolBean?.id
        mUser?.schoolProvince=schoolBean?.province
        mUser?.schoolCity=schoolBean?.city
        mUser?.schoolArea=schoolBean?.area
        mUser?.schoolName=schoolBean?.schoolName
        tv_province_str.text = schoolBean?.province
        tv_city.text = schoolBean?.city
        tv_school.text = schoolBean?.schoolName
        tv_area.text = schoolBean?.area
    }

    override fun onBind() {
        presenter.getFriends()
    }

    override fun onUnbind() {
        mAdapterFriend?.remove(position)
        DataBeanManager.friends=friends
    }

    override fun onListFriend(list: FriendList) {
        friends=list.list
        DataBeanManager.friends=friends
        mAdapterFriend?.setNewData(friends)
    }

    override fun onListSchools(list: MutableList<SchoolBean>) {
        schools=list
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        initChangeScreenData()
        mSchoolPresenter.getSchool()
        presenter.getFriends()
    }

    override fun initChangeScreenData() {
        mSchoolPresenter=SchoolPresenter(this,getCurrentScreenPos())
        presenter=AccountInfoPresenter(this,getCurrentScreenPos())
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        setPageTitle("我的账户")

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text =  telNumber.substring(0,3)+"****"+telNumber.substring(7,11)
            tv_course_str.text=subjectName
            tv_province_str.text = schoolProvince
            tv_city.text = schoolCity
            tv_school.text = schoolName
            tv_area.text = schoolArea
        }

        btn_edit_name.setOnClickListener {
            editName()
        }

        btn_edit_school.setOnClickListener {
            editSchool()
        }

        btn_add_friend.setOnClickListener {
            InputContentDialog(this,"输入好友账号").builder()
                .setOnDialogClickListener { string ->
                    presenter.onBindFriend(string)
                }
        }

        btn_logout.setOnClickListener {
            CommonDialog(this).setContent("确认退出登录？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    MethodManager.logout(this@AccountInfoActivity)
                }
            })
        }

        initRecyclerViewFriend()
    }

    private fun initRecyclerViewFriend(){
        rv_list_friend.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapterFriend = AccountFriendAdapter(R.layout.item_account_friend,null)
        rv_list_friend.adapter = mAdapterFriend
        mAdapterFriend?.bindToRecyclerView(rv_list_friend)
        mAdapterFriend?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id==R.id.tv_friend_cancel){
                CommonDialog(this).setContent("取消好友关联?").builder().setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        presenter.unbindFriend(friends[position].id)
                    }
                })
            }
        }
    }


    /**
     * 修改名称
     */
    private fun editName(){
        InputContentDialog(this,tv_name.text.toString()).builder()
            .setOnDialogClickListener { string ->
                nickname = string
                presenter.editName(nickname)
            }
    }

    /**
     * 修改学校
     */
    private fun editSchool() {
        if (schoolSelectDialog==null){
            schoolSelectDialog=SchoolSelectDialog(this,schools).builder()
            schoolSelectDialog?.setOnDialogClickListener {
                school=it.id
                if (school==mUser?.schoolId)
                    return@setOnDialogClickListener
                presenter.editSchool(it.id)
                for (item in schools){
                    if (item.id==school)
                        schoolBean=item
                }
            }
        }
        else{
            schoolSelectDialog?.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mUser?.let { SPUtil.putObj("user", it) }
    }

}