package com.bll.lnkteacher.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.manager.WallpaperDaoManager
import com.bll.lnkteacher.mvp.model.WallpaperBean
import com.bll.lnkteacher.ui.adapter.MyWallpaperAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_title.*
import java.io.File

class WallpaperMyActivity:BaseActivity(){

    private var items= mutableListOf<WallpaperBean>()
    private var mAdapter: MyWallpaperAdapter?=null
    private var position=0
    private var leftPath=""
    private var rightPath=""

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
    }
    override fun initView() {
        setPageTitle("我的壁纸")

        setPageSetting("设为壁纸")

        tv_custom_1.setOnClickListener {
            if (leftPath.isEmpty()&&rightPath.isEmpty())
                return@setOnClickListener
            if(File(leftPath).exists()){
                android.os.SystemProperties.set("xsys.eink.standby",leftPath)
//                android.os.SystemProperties.set("xsys.eink.poweroff",leftPath)
            }
            if(File(rightPath).exists()){
                android.os.SystemProperties.set("xsys.eink.standby1",rightPath)
//                android.os.SystemProperties.set("xsys.eink.poweroff1",rightPath)
            }
        }

        initRecycleView()
        fetchData()
    }

    private fun initRecycleView(){

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,20f), DP2PX.dip2px(this,50f),
            DP2PX.dip2px(this,20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = MyWallpaperAdapter(R.layout.item_wallpaper_my, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(this@WallpaperMyActivity,19f),0))
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(this@WallpaperMyActivity,items[position].bodyUrl.split(",")).builder()
            }
            setOnItemChildClickListener { adapter, view, position ->
                //用来确定翻页后选中的位置
                val wallpaperItem=items[position]
                if (view.id==R.id.cb_left){
                    if(wallpaperItem.isLeft){
                        wallpaperItem.isLeft=false
                        leftPath=""
                    }
                    else{
                        for (item in items){
                            item.isLeft=false
                        }
                        wallpaperItem.isLeft=true
                        leftPath=wallpaperItem.path
                    }
                }
                if (view.id==R.id.cb_right){
                    if (wallpaperItem.isRight){
                        wallpaperItem.isRight=false
                        rightPath=""
                    }
                    else{
                        for (item in items){
                            item.isRight=false
                        }
                        wallpaperItem.isRight=true
                        rightPath=wallpaperItem.path
                    }
                }
                notifyDataSetChanged()
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@WallpaperMyActivity.position = position
                delete()
                true
            }
        }
    }

    private fun delete(){
        CommonDialog(this).setContent("确定删除？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val item=items[position]
                FileUtils.deleteFile(File(item.path))
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