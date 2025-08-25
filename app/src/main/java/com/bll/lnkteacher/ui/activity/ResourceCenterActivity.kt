package com.bll.lnkteacher.ui.activity

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.ui.fragment.resource.AppDownloadFragment
import com.bll.lnkteacher.ui.fragment.resource.CalenderDownloadFragment
import com.bll.lnkteacher.ui.fragment.resource.DictionaryDownloadFragment
import com.bll.lnkteacher.ui.fragment.resource.WallpaperDownloadFragment
import kotlinx.android.synthetic.main.common_title.tv_supply

class ResourceCenterActivity:BaseAppCompatActivity(){
    private var lastFragment: Fragment? = null
    private var toolFragment: AppDownloadFragment? = null
    private var wallpaperFragment: WallpaperDownloadFragment? = null
    private var calenderFragment: CalenderDownloadFragment? = null
    private var dictionaryDownloadFragment: DictionaryDownloadFragment?=null

    private var popSupplys= mutableListOf<PopupBean>()

    override fun layoutId(): Int {
        return  R.layout.ac_resource
    }

    override fun initData() {
        popSupplys=DataBeanManager.popupSupplys
    }

    override fun initView() {
        setPageTitle("资源中心")
        showView(tv_supply)

        toolFragment=AppDownloadFragment().newInstance(2)
        wallpaperFragment = WallpaperDownloadFragment()
        calenderFragment = CalenderDownloadFragment()
        dictionaryDownloadFragment=DictionaryDownloadFragment()

        switchFragment(lastFragment, toolFragment)
        initTab()

        if (popSupplys.size>0)
            tv_supply.text=popSupplys[0].name
        tv_supply.setOnClickListener {
            PopupRadioList(this,popSupplys,tv_supply,tv_supply.width,5).builder().setOnSelectListener {
                tv_supply.text = it.name
                toolFragment?.changeSupply(it.id)
                wallpaperFragment?.changeSupply(it.id)
                calenderFragment?.changeSupply(it.id)
            }
        }
    }

    private fun initTab(){
        for (i in DataBeanManager.resources.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=DataBeanManager.resources[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        when(position){
            0->{
                showView(tv_supply)
                switchFragment(lastFragment, toolFragment)
            }
            1->{
                disMissView(tv_supply)
                switchFragment(lastFragment, dictionaryDownloadFragment)
            }
            2->{
                showView(tv_supply)
                switchFragment(lastFragment, wallpaperFragment)
            }
            3->{
                showView(tv_supply)
                switchFragment(lastFragment, calenderFragment)
            }
        }
    }


    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.frame_layout, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

    override fun initChangeScreenData() {
        toolFragment?.initChangeScreenData()
        wallpaperFragment?.initChangeScreenData()
        calenderFragment?.initChangeScreenData()
        dictionaryDownloadFragment?.initChangeScreenData()
    }

}