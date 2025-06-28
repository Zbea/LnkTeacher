package com.bll.lnkteacher.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_class_schedule.iv_image
import kotlinx.android.synthetic.main.common_title.tv_custom

class ClassScheduleActivity:BaseAppCompatActivity() {

    private var classGroupId = 0

    override fun layoutId(): Int {
        return R.layout.ac_class_schedule
    }

    override fun initData() {
        for (classGroup in DataBeanManager.getClassGroupMains()) {
            if (classGroup.userId == mUserId) {
                classGroupId = classGroup.classGroupId
                break
            }
        }
        if (classGroupId > 0) {
            showView(tv_custom)
        } else {
            disMissView(tv_custom)
        }
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setPageTitle("课程表")

        tv_custom.text = "课程表编辑"

        tv_custom.setOnClickListener {
            val intent=Intent(this, ClassScheduleEditActivity::class.java)
                .setFlags(2)
                .putExtra("classGroupId",classGroupId)
            activityResultLauncher.launch(intent)
        }

        if (DataBeanManager.getClassGroupMains().isNotEmpty())
            GlideUtils.setImageUrl(this,DataBeanManager.getClassGroupMains()[0].imageUrl,iv_image)

        initTab()
    }

    private fun initTab() {
        for (item in DataBeanManager.getClassGroupMains()){
            itemTabTypes.add(ItemTypeBean().apply {
                title=item.name
                typeId=item.classGroupId
                path=item.imageUrl
                isCheck=DataBeanManager.getClassGroupMains().indexOf(item)==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        GlideUtils.setImageUrl(this,itemTabTypes[position].path,iv_image)
    }

    /**
     * 开始通知回调
     */
    private val activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode== Activity.RESULT_OK){
            val data=it.data
            val path=data?.getStringExtra("path")
            for (item in itemTabTypes){
                if (item.typeId==classGroupId){
                    item.path=path
                    if (item.isCheck){
                        GlideUtils.setImageUrl(this,path,iv_image)
                    }
                }
            }
        }
    }
}