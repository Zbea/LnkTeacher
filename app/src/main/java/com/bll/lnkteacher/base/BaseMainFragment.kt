package com.bll.lnkteacher.base

import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.presenter.CloudUploadPresenter
import com.bll.lnkteacher.mvp.presenter.QiniuPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import kotlinx.android.synthetic.main.common_fragment_title.*


abstract class BaseMainFragment : BaseFragment(), IContractView.ICloudUploadView,IContractView.IQiniuView {

    val mQiniuPresenter= QiniuPresenter(this)
    var mCloudUploadPresenter= CloudUploadPresenter(this)
    var grade=0
    var popGrades= mutableListOf<PopupBean>()

    override fun onToken(token: String) {
        onUpload(token)
    }

    override fun onSuccessCloudUpload(cloudIds: MutableList<Int>?) {
        uploadSuccess(cloudIds)
    }
    override fun onDeleteSuccess() {
    }

    override fun initView() {
        setGradeStr()

        tv_grade.setOnClickListener {
            PopupRadioList(requireActivity(), popGrades, tv_grade,tv_grade.width,  5).builder().setOnSelectListener { item ->
                tv_grade?.text=item.name
                grade=item.id
                onGradeEvent()
            }
        }
    }

    fun setGradeStr(){
        if (grade!=0){
            tv_grade.text= DataBeanManager.getGradeStr(grade)
            popGrades = DataBeanManager.popupGrades(grade)
        }
        else{
            popGrades = DataBeanManager.popupGrades
        }
    }

    open fun onGradeEvent(){

    }

    /**
     * 开始上传
     */
    open fun onUpload(token: String){

    }
    /**
     * 上传成功(书籍云id) 上传成功后删掉重复上传的数据
     */
    open fun uploadSuccess(cloudIds: MutableList<Int>?){
        if (!cloudIds.isNullOrEmpty())
        {
            mCloudUploadPresenter.deleteCloud(cloudIds)
        }
    }

}
