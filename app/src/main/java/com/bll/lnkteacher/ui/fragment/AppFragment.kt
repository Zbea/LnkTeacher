package com.bll.lnkteacher.ui.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.AppBean
import com.bll.lnkteacher.ui.adapter.AppListAdapter
import com.bll.lnkteacher.utils.AppUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_app.*

class AppFragment:BaseMainFragment() {

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
        mAdapter = AppListAdapter(R.layout.item_app_list, 1,null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(5,70))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val packageName= apps[position].packageName
            AppUtils.startAPP(activity,packageName)
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            CommonDialog(requireActivity(),1).setContent("卸载应用？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    AppUtils.uninstallAPK(requireActivity(),apps[position].packageName)
                }
            })
            true
        }
    }

    private fun initData() {
        apps=AppUtils.scanLocalInstallAppList(activity)
        mAdapter?.setNewData(apps)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.APP_INSTALL_EVENT,Constants.APP_UNINSTALL_EVENT->{
                initData()
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        initData()
    }

}