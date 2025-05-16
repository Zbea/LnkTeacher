package com.bll.lnkteacher.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.manager.WallpaperDaoManager
import com.bll.lnkteacher.mvp.model.WallpaperBean
import com.bll.lnkteacher.ui.adapter.WallpaperMyAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.tv_custom_1
import java.io.File

class WallpaperMyActivity:BaseAppCompatActivity(){

    private var items= mutableListOf<WallpaperBean>()
    private var mAdapter: WallpaperMyAdapter?=null
    private var leftPath=""
    private var rightPath=""

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=6
    }
    override fun initView() {
        setPageTitle("我的壁纸")
        setPageSetting("设为壁纸")

        tv_custom_1.setOnClickListener {
            if (leftPath.isEmpty()&&rightPath.isEmpty()){
                showToast("设置失败")
                return@setOnClickListener
            }
            if(File(leftPath).exists()){
                android.os.SystemProperties.set("xsys.eink.standby",leftPath)
//                android.os.SystemProperties.set("xsys.eink.poweroff",leftPath)
            }
            if(File(rightPath).exists()){
                android.os.SystemProperties.set("xsys.eink.standby1",rightPath)
//                android.os.SystemProperties.set("xsys.eink.poweroff1",rightPath)
            }
            showToast("设置成功")
        }
        initRecycleView()
        fetchData()
    }

    private fun initRecycleView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,30f), DP2PX.dip2px(this,60f),
            DP2PX.dip2px(this,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 2)//创建布局管理
        mAdapter = WallpaperMyAdapter(R.layout.item_wallpaper_my, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco(2,90))
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(this@WallpaperMyActivity,items[position].paths).builder()
            }
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id == R.id.cb_check) {
                    for (item in items) {
                        item.isCheck = false
                    }
                    val item = items[position]
                    item.isCheck = true
                    leftPath = item.paths[0]
                    rightPath = item.paths[1]
                    mAdapter?.notifyDataSetChanged()
                }
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                delete(position)
                true
            }
        }
    }

    private fun delete(position:Int){
        CommonDialog(this).setContent("确定删除？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val item=items[position]
                val path= FileAddress().getPathImage("wallpaper" ,item.contentId.toString())
                FileUtils.deleteFile(File(path))
                WallpaperDaoManager.getInstance().deleteBean(item)
                mAdapter?.remove(position)
            }
        })
    }


    override fun fetchData() {
        val count=WallpaperDaoManager.getInstance().queryList().size
        items=WallpaperDaoManager.getInstance().queryList(pageSize,pageIndex)
        setPageNumber(count)
        mAdapter?.setNewData(items)
    }

}