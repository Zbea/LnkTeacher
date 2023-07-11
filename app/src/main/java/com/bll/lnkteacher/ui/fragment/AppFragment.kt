package com.bll.lnkteacher.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.AppBean
import com.bll.lnkteacher.ui.activity.AppCenterActivity
import com.bll.lnkteacher.ui.adapter.AppListAdapter
import com.bll.lnkteacher.utils.AppUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_app.*

class AppFragment:BaseFragment() {

    private var apps= mutableListOf<AppBean>()
    private var mAdapter: AppListAdapter?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_app
    }

    override fun initView() {
        setTitle(R.string.main_app_title)
        initRecycler()
        initData()
    }

    override fun lazyLoad() {
    }


    private fun initRecycler(){

        rv_list.layoutManager = GridLayoutManager(activity,5)//创建布局管理
        mAdapter = AppListAdapter(R.layout.item_app_list, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(5,70))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            if (position==0){
                startActivity(Intent(requireActivity(),AppCenterActivity::class.java))
            }
            else{
                val packageName= apps[position].packageName
                AppUtils.startAPP(activity,packageName)
            }
        }
    }

    private fun initData() {
        apps.clear()
        apps.add(AppBean().apply {
            appId = 0
            appName = "应用中心"
            image = activity?.getDrawable(R.mipmap.icon_app_center)
            isBase = true
        })
        apps.addAll(AppUtils.scanLocalInstallAppList(activity))
        mAdapter?.setNewData(apps)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.APP_EVENT->{
                initData()
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        initData()
    }

}