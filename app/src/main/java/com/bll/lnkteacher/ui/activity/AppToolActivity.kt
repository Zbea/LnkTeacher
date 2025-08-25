package com.bll.lnkteacher.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.manager.AppDaoManager
import com.bll.lnkteacher.mvp.model.AppBean
import com.bll.lnkteacher.ui.adapter.AppListAdapter
import com.bll.lnkteacher.utils.AppUtils
import com.bll.lnkteacher.utils.BitmapUtils
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_app_tool.rv_list
import kotlinx.android.synthetic.main.ac_app_tool.rv_list_tool
import kotlinx.android.synthetic.main.ac_app_tool.tv_out
import kotlinx.android.synthetic.main.common_title.tv_btn_1

class AppToolActivity:BaseAppCompatActivity() {

    private var apps= mutableListOf<AppBean>()
    private var menuApps= mutableListOf<AppBean>()
    private var mAdapter: AppListAdapter?=null
    private var mAdapterTool: AppListAdapter?=null
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_app_tool
    }

    override fun initData() {
        if (AppDaoManager.getInstance().queryBeanByPackageName(Constants.PACKAGE_GEOMETRY)==null){
            AppDaoManager.getInstance().insertOrReplace(AppBean().apply {
                appName="几何绘图"
                imageByte = BitmapUtils.drawableToByte(getDrawable(R.mipmap.icon_app_geometry))
                packageName=Constants.PACKAGE_GEOMETRY
                time=System.currentTimeMillis()
                isTool=false
                type=1
            })
        }
    }

    override fun initView() {
        setPageTitle("我的工具")
        showView(tv_btn_1)
        tv_btn_1.text="添加"

        initRecyclerView()
        initRecyclerTool()

        tv_btn_1.setOnClickListener {
            for (item in apps){
                if (item.isCheck){
                    item.isTool=true
                    AppDaoManager.getInstance().insertOrReplace(item)
                }
            }
            fetchData()
        }
        tv_out.setOnClickListener {
            for (item in menuApps){
                if (item.isCheck){
                    item.isTool=false
                    AppDaoManager.getInstance().insertOrReplace(item)
                }
            }
            fetchData()
        }

        fetchData()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mAdapter = AppListAdapter(R.layout.item_app_list, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(6,50))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val item=apps[position]
            val packageName= item.packageName
            if (packageName!=Constants.PACKAGE_GEOMETRY){
                if (item.type==2){
                    MethodManager.gotoDictionaryDetails(this,item.bookId,getCurrentScreenPos())
                }
                else{
                    AppUtils.startAPP(this,packageName)
                }
            }
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item=apps[position]
            when(view.id){
                R.id.ll_name->{
                    item.isCheck=!item.isCheck
                    mAdapter?.notifyItemChanged(position)
                }
            }
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            this.position=position
            val item=apps[position]
            val packageName= item.packageName
            if (packageName!=Constants.PACKAGE_GEOMETRY){
                CommonDialog(this).setContent("确认删除工具？").builder().setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun ok() {
                        if (item.type==2){
                            AppDaoManager.getInstance().deleteBean(item)
                            FileUtils.delete(item.path)
                            fetchData()
                        }
                        else{
                            AppUtils.uninstallAPK(this@AppToolActivity,packageName)
                        }
                    }
                })
            }
            true
        }

    }

    private fun initRecyclerTool(){
        rv_list_tool.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mAdapterTool = AppListAdapter(R.layout.item_app_list, null)
        rv_list_tool.adapter = mAdapterTool
        mAdapterTool?.bindToRecyclerView(rv_list_tool)
        rv_list_tool.addItemDecoration(SpaceGridItemDeco(6,30))
        mAdapterTool?.setOnItemChildClickListener { adapter, view, position ->
            val item=menuApps[position]
            when(view.id){
                R.id.ll_name->{
                    item.isCheck=!item.isCheck
                    mAdapterTool?.notifyItemChanged(position)
                }
            }
        }

    }

    override fun fetchData() {
        setData()
        setMenuData()
    }

    private fun setData(){
        apps=MethodManager.getAppTools(this,0)
        mAdapter?.setNewData(apps)
    }

    private fun setMenuData(){
        menuApps=MethodManager.getAppTools(this,1)
        mAdapterTool?.setNewData(menuApps)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.APP_INSTALL_INSERT_EVENT->{
                setData()
            }
            Constants.APP_UNINSTALL_EVENT->{
                AppDaoManager.getInstance().deleteBean(apps[position])
                fetchData()
            }
        }
    }

}