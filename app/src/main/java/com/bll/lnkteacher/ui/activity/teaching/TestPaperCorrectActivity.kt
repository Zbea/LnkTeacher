package com.bll.lnkteacher.ui.activity.teaching

import android.graphics.BitmapFactory
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.ExamScoreDetailsDialog
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.testpaper.*
import com.bll.lnkteacher.mvp.presenter.FileUploadPresenter
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.IFileUploadView
import com.bll.lnkteacher.ui.adapter.ExamScoreAdapter
import com.bll.lnkteacher.ui.adapter.TestPaperCorrectUserAdapter
import com.bll.lnkteacher.utils.*
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_testpaper_correct.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class TestPaperCorrectActivity:BaseDrawingActivity(),IContractView.ITestPaperCorrectDetailsView,IFileUploadView{

    private var id=0
    private val mUploadPresenter=FileUploadPresenter(this)
    private val mPresenter=TestPaperCorrectDetailsPresenter(this)
    private var mClassBean: CorrectClassBean?=null
    private var userItems= mutableListOf<TestPaperCorrectClass.UserBean>()

    private var mAdapter:TestPaperCorrectUserAdapter?=null
    private var url=""//上个学生提交地址
    private var posImage=0//当前图片下标
    private var posUser=0//当前学生下标
    private var currentImages:Array<String>?=null
    private val commitItems = mutableListOf<ItemList>()
    private var initScores=mutableListOf<ExamScoreItem>()//初始题目分数
    private var examScoreItems= mutableListOf<ExamScoreItem>()//已填题目分数

    private var mScoreAdapter:ExamScoreAdapter?=null
    private var userCorrect=0
    private var userSend=0

    override fun onToken(token: String) {
        val commitPaths = mutableListOf<String>()
        for (item in commitItems) {
            commitPaths.add(item.url)
        }
        FileImageUploadManager(token, commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    url=ToolUtils.getImagesStr(urls)
                    val map= HashMap<String, Any>()
                    map["studentTaskId"]=userItems[posUser].studentTaskId
                    map["score"]=tv_score.text.toString().toInt()
                    map["submitUrl"]=url
                    map["status"]=2
                    map["question"]=Gson().toJson(examScoreItems)
                    mPresenter.commitPaperStudent(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast("上传失败")
                }
            })
        }
    }

    override fun onImageList(list: MutableList<ContentListBean>?) {
    }
    override fun onClassPapers(bean: TestPaperCorrectClass?) {
        userItems=bean?.list!!
        userCorrect=bean.totalUpdate
        userSend=bean.totalSend
        if (userItems.size>0){
            userItems[posUser].isCheck=true
            setContentView()
        }
        tv_correct_number.text=userCorrect.toString()+"人"
        mAdapter?.setNewData(userItems)
    }
    override fun onGrade(list: MutableList<TestPaperGrade>?) {
    }
    override fun onCorrectSuccess() {
        showToast(userItems[posUser].name+getString(R.string.teaching_correct_success))
        userCorrect+=1
        tv_correct_number.text=userCorrect.toString()+"人"
        userItems[posUser].score=tv_score.text.toString().toInt()
        userItems[posUser].submitUrl=url
        userItems[posUser].status=2
        userItems[posUser].question=Gson().toJson(examScoreItems)
        mAdapter?.notifyItemChanged(posUser)
        //批改完成之后删除文件夹
        FileUtils.deleteFile(File(getPath()))
        elik?.setPWEnabled(false,false)
        EventBus.getDefault().post(Constants.EXAM_CORRECT_EVENT)
    }

    override fun onSendSuccess() {
        showToast(R.string.toast_send_success)
        mPresenter.getClassPapers(mClassBean?.examChangeId!!)
    }
    
    override fun layoutId(): Int {
        return R.layout.ac_testpaper_correct
    }

    override fun initData() {
        id=intent.flags
        mClassBean=intent.getBundleExtra("bundle")?.getSerializable("classBean") as CorrectClassBean

        mPresenter.getClassPapers(mClassBean?.examChangeId!!)
    }

    override fun initView() {
        elik?.setPWEnabled(false,false)

        setPageTitle("${getString(R.string.teaching_tab_testpaper_correct)}  ${mClassBean?.examName}  ${mClassBean?.name}")
        showView(tv_custom)
        tv_custom.text="发送批改"

        initRecyclerView()
        initRecyclerViewScore()

        tv_custom.setOnClickListener {
            if (userCorrect>userSend)
                mPresenter.sendClass(mClassBean?.examChangeId!!)
        }

        iv_up.setOnClickListener {
            if (posImage>0){
                posImage-=1
                setContentImage()
            }
        }
        iv_down.setOnClickListener {
            if (posImage< getImageSize()-1){
                posImage+=1
                setContentImage()
            }
        }

        tv_save.setOnClickListener {
            val item=userItems[posUser]
            if (item.status==1&&et_total.text.isNotEmpty()){
                tv_score.text=et_total.text.toString()
                for (ite in initScores){
                    if (!ite.score.isNullOrEmpty()){
                        examScoreItems.add(ite)
                    }
                }
                showLoading()
                commitPapers()
            }
            disMissView(ll_score_content)
            hideKeyboard()
        }

        iv_add.setOnClickListener {
            for (i in 0..1){
                initScores.add(ExamScoreItem().apply {
                    sort=initScores.size+1
                })
            }
            mScoreAdapter?.notifyItemRangeInserted(initScores.size-2,2)
            rv_list_score.scrollToPosition(initScores.size-1)
        }

        tv_score.setOnClickListener {
            val item=userItems[posUser]
            when(item.status){
                1->{
                    showView(ll_score_content)
                }
                2->{
                    if (!item.question.isNullOrEmpty()){
                        initScores=Gson().fromJson(item.question, object : TypeToken<List<ExamScoreItem>>() {}.type) as MutableList<ExamScoreItem>
                        ExamScoreDetailsDialog(this,item.score,initScores).builder()
                    }
                }
            }
        }

        initScoreData()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = TestPaperCorrectUserAdapter(R.layout.item_homework_correct_name, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, DP2PX.dip2px(this@TestPaperCorrectActivity,10f)))
            setOnItemClickListener { adapter, view, position ->
                if (position==posUser)
                    return@setOnItemClickListener

                userItems[posUser].isCheck=false
                posUser=position//设置当前学生下标
                val item=userItems[position]
                item.isCheck=true
                notifyDataSetChanged()
                posImage=0

                disMissView(ll_score_content)
                when(item.status){
                    1,3->{
                        initScoreData()
                        tv_score.text=""
                        et_total.setText("")
                    }
                    2->{
                        tv_score.text=item.score.toString()
                    }
                }
                setContentView()
            }
        }
    }

    /**
     * 初始化分数列表数据
     */
    private fun initScoreData(){
        initScores.clear()
        for (i in 1..10){
            initScores.add(ExamScoreItem().apply {
                sort=i
            })
        }
        mScoreAdapter?.setNewData(initScores)
    }

    private fun initRecyclerViewScore(){
        rv_list_score.layoutManager = GridLayoutManager(this,2,LinearLayoutManager.HORIZONTAL,false)
        mScoreAdapter = ExamScoreAdapter(R.layout.item_exam_score, null).apply {
            rv_list_score.adapter = this
            bindToRecyclerView(rv_list_score)
        }
    }

    /**
     * 提交学生考卷
     */
    private fun commitPapers(){
        commitItems.clear()
        //手写,图片合图
        for (i in currentImages?.indices!!){
            val index=i+1
            val masterImage="${getPath()}/${index}.png"//原图
            val drawPath = getPathDrawStr(index).replace("tch","png")
            val mergePath = getPath()//合并后的路径
            var mergePathStr = "${getPath()}/merge${index}.png"//合并后图片地址
            Thread {
                val oldBitmap = BitmapFactory.decodeFile(masterImage)
                val drawBitmap = BitmapFactory.decodeFile(drawPath)
                if (drawBitmap!=null){
                    val mergeBitmap = BitmapUtils.mergeBitmap(oldBitmap, drawBitmap)
                    BitmapUtils.saveBmpGallery(this, mergeBitmap, mergePath, "merge${index}")
                }
                else{
                    mergePathStr=masterImage
                }
                commitItems.add(ItemList().apply {
                    id = i
                    url = mergePathStr
                })
                if (commitItems.size==currentImages?.size){
                    commitItems.sort()
                    mUploadPresenter.getToken()
                }
            }.start()

        }
    }

    /**
     * 设置切换内容展示
     */
    private fun setContentView(){
        val userItem=userItems[posUser]
        when(userItem.status){
            1->{
                currentImages=userItem.studentUrl.split(",").toTypedArray()
                loadPapers()
            }
            2->{
                currentImages=userItem.submitUrl.split(",").toTypedArray()
                tv_score.text = userItem.score.toString()
                elik?.setPWEnabled(false,false)
                setContentImage()
            }
            3->{
                elik?.setPWEnabled(false,false)
                tv_page.text=""
                v_content.setImageResource(0)
            }
        }
    }

    /**
     * 设置学生提交图片展示
     */
    private fun setContentImage(){
        //批改成功后删掉原来，加载提交后的图片
        if (userItems[posUser].status==2){
            GlideUtils.setImageRoundUrl(this, currentImages?.get(posImage) ,v_content,10)
        }
        else{
            elik?.setPWEnabled(true,true)
            val masterImage="${getPath()}/${posImage+1}.png"//原图
            GlideUtils.setImageFile(this,File(masterImage),v_content)
        }
        tv_page.text="${posImage+1}/${getImageSize()}"

        val drawPath = getPathDrawStr(posImage+1)
        elik?.setLoadFilePath(drawPath, true)
    }

    override fun onElikSave() {
        elik?.saveBitmap(true) {}
    }

    /**
     * 下载学生提交试卷
     */
    private fun loadPapers(){
        showLoading()
        val savePaths= mutableListOf<String>()
        for (i in currentImages?.indices!!){
            savePaths.add(getPath()+"/${i+1}.png")
        }
        val files = FileUtils.getFiles(getPath())
        if (files.isNullOrEmpty()) {
            FileMultitaskDownManager.with(this).create(currentImages?.toMutableList()).setPath(savePaths).startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        hideLoading()
                        setContentImage()
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        hideLoading()
                    }
                })
        } else {
            hideLoading()
            setContentImage()
        }
    }

    /**
     * 每份多少张考卷
     */
    private fun getImageSize():Int{
        if (currentImages.isNullOrEmpty())
            return 0
        return currentImages!!.size
    }

    /**
     * 文件路径
     */
    private fun getPath():String{
        return FileAddress().getPathTestPaper(id,mClassBean?.classId, userItems[posUser].userId)
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int):String{
        return getPath()+"/draw${index}.tch"//手绘地址
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

}