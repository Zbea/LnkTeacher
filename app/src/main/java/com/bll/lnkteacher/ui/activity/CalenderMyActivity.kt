package com.bll.lnkteacher.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.manager.CalenderDaoManager
import com.bll.lnkteacher.mvp.model.CalenderItemBean
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.ui.adapter.CalenderListAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_list.rv_list
import org.greenrobot.eventbus.EventBus
import java.io.File

class CalenderMyActivity:BaseActivity(){

    private var items= mutableListOf<CalenderItemBean>()
    private var mAdapter:CalenderListAdapter?=null
    private var longBeans = mutableListOf<ItemList>()
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12

        longBeans.add(ItemList().apply {
            name="删除"
            resId=R.mipmap.icon_setting_delete
        })
        longBeans.add(ItemList().apply {
            name="设置"
            resId=R.mipmap.icon_setting_set
        })
    }
    override fun initView() {
        setPageTitle("我的日历")

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

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = CalenderListAdapter(R.layout.item_bookstore, 1,null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val item=items[position]
                val urls=item.previewUrl.split(",")
                ImageDialog(this@CalenderMyActivity,urls).builder()
            }
            setOnItemLongClickListener { adapter, view, position ->
                this@CalenderMyActivity.position=position
                onLongClick()
                true
            }
        }
        rv_list?.addItemDecoration(SpaceGridItemDeco(4,  DP2PX.dip2px(this, 60f)))
    }

    private fun onLongClick() {
        val item=items[position]
        LongClickManageDialog(this, item.title,longBeans).builder()
            .setOnDialogClickListener {
                if (it==0){
                    FileUtils.deleteFile(File(item.path))
                    CalenderDaoManager.getInstance().deleteBean(item)
                    mAdapter?.remove(position)
                }
                else{
                    CalenderDaoManager.getInstance().setSetFalse()
                    item.isSet=true
                    CalenderDaoManager.getInstance().insertOrReplace(item)
                    showToast("设置成功")
                }
                EventBus.getDefault().post(Constants.CALENDER_SET_EVENT)
            }
    }

    override fun fetchData() {
        val count=CalenderDaoManager.getInstance().queryList().size
        items=CalenderDaoManager.getInstance().queryList(pageIndex,pageSize)
        setPageNumber(count)
        mAdapter?.setNewData(items)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.CALENDER_EVENT){
            pageIndex=1
            fetchData()
        }
    }

}