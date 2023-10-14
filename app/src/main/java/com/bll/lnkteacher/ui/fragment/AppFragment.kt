package com.bll.lnkteacher.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.AppBean
import com.bll.lnkteacher.ui.activity.AppCenterActivity
import com.bll.lnkteacher.ui.activity.AppToolActivity
import com.bll.lnkteacher.ui.activity.WallpaperListActivity
import com.bll.lnkteacher.ui.activity.book.BookStoreTypeActivity
import com.bll.lnkteacher.ui.adapter.AppListAdapter
import com.bll.lnkteacher.utils.AppUtils
import com.bll.lnkteacher.utils.BitmapUtils
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
        mAdapter = AppListAdapter(R.layout.item_app_list, 1,null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(5,70))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            when (position) {
                0 -> {
                    startActivity(Intent(requireActivity(),AppCenterActivity::class.java))
                }
                1 -> {
                    startActivity(Intent(requireActivity(),BookStoreTypeActivity::class.java))
                }
                2 -> {
                    startActivity(Intent(requireActivity(),AppToolActivity::class.java))
                }
                3 -> {
                    startActivity(Intent(requireActivity(),WallpaperListActivity::class.java))
                }
                else -> {
                    val packageName= apps[position].packageName
                    AppUtils.startAPP(activity,packageName)
                }
            }
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            if (position>3){
                CommonDialog(requireActivity()).setContent("卸载应用？").builder().setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        AppUtils.uninstallAPK(requireActivity(),apps[position].packageName)
                    }
                })
            }
            true
        }
    }

    private fun initData() {
        apps.clear()
        apps.add(AppBean().apply {
            appName = "应用中心"
            imageByte = BitmapUtils.drawableToByte(requireActivity().getDrawable(R.mipmap.icon_app_center))
        })
        apps.add(AppBean().apply {
            appName = "书库"
            imageByte = BitmapUtils.drawableToByte(requireActivity().getDrawable(R.mipmap.icon_app_center))
        })
        apps.add(AppBean().apply {
            appName = "工具"
            imageByte = BitmapUtils.drawableToByte(requireActivity().getDrawable(R.mipmap.icon_app_center))
        })
        apps.add(AppBean().apply {
            appName = "壁纸"
            imageByte = BitmapUtils.drawableToByte(requireActivity().getDrawable(R.mipmap.icon_app_center))
        })
        apps.addAll(AppUtils.scanLocalInstallAppList(activity))
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