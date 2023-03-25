package com.bll.lnkteacher.ui.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.AppBean
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

        initData()

        initRecycler()

    }

    fun initData() {
        apps.addAll(AppUtils.scanLocalInstallAppList(activity))
    }

    override fun lazyLoad() {
    }


    private fun initRecycler(){

        rv_list.layoutManager = GridLayoutManager(activity,5)//创建布局管理
        mAdapter = AppListAdapter(R.layout.item_app_list, apps)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(5,70))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.iv_image){
                val packageName= apps[position].packageName
                AppUtils.startAPP(activity,packageName)
            }
        }

    }


}