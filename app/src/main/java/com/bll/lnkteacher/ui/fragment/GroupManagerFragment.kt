package com.bll.lnkteacher.ui.fragment

import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.ui.fragment.group.ClassGroupFragment
import com.bll.lnkteacher.ui.fragment.group.GroupFragment
import com.bll.lnkteacher.utils.NetworkUtil
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*

class GroupManagerFragment:BaseFragment() {

    private var classGroupFragment: ClassGroupFragment? = null
    private var groupFragment: GroupFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null


    override fun getLayoutId(): Int {
        return R.layout.fragment_group_manager
    }

    override fun initView() {
        setTitle(R.string.main_classgroup_fragment_title)
        showView(iv_create)

        classGroupFragment = ClassGroupFragment()
        groupFragment = GroupFragment()

        switchFragment(lastFragment, classGroupFragment)

        iv_create.setOnClickListener {
            when (lastPosition) {
                0 -> {
                    classGroupFragment?.createClassGroup()
                }
                1 -> {
                    groupFragment?.createGroup()
                }
            }
        }

        iv_add.setOnClickListener {
            groupFragment?.addGroup()
        }
        initTab()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkAvailable(requireActivity()))
            fetchCommonData()
    }

    /**
     * 设置tab
     */
    private fun initTab() {
        val strs=DataBeanManager.groupStrs
        for (i in strs.indices){
            rg_group.addView(getRadioButton(i,strs[i],strs.size-1))
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                0 -> {
                    disMissView(iv_add)
                    switchFragment(lastFragment, classGroupFragment)
                }
                1 -> {
                    showView(iv_add)
                    switchFragment(lastFragment, groupFragment)
                }
            }
            lastPosition = i
        }

    }

    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = activity?.supportFragmentManager
            val ft = fm?.beginTransaction()

            if (!to?.isAdded!!) {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.add(R.id.fl_content, to)?.commit()
            } else {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.show(to)?.commit()
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        lazyLoad()
    }

}