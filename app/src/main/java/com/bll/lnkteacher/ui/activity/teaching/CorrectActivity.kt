package com.bll.lnkteacher.ui.activity.teaching

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.model.testpaper.CorrectClassBean
import com.bll.lnkteacher.mvp.model.testpaper.ExamScoreItem
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.presenter.FileUploadPresenter
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.IFileUploadView
import com.bll.lnkteacher.ui.adapter.TestPaperCorrectUserAdapter
import com.bll.lnkteacher.ui.adapter.TopicMultiScoreAdapter
import com.bll.lnkteacher.ui.adapter.TopicScoreAdapter
import com.bll.lnkteacher.utils.*
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_testpaper_correct.*
import kotlinx.android.synthetic.main.common_correct_drawing.*
import kotlinx.android.synthetic.main.common_correct_topic_module.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class CorrectActivity:BaseDrawingActivity(),IContractView.ITestPaperCorrectDetailsView,IFileUploadView{

    private var mId=0
    private var type=0 //1作业 2 测卷
    private var subType = 0
    private val mUploadPresenter=FileUploadPresenter(this,3)
    private val mPresenter=TestPaperCorrectDetailsPresenter(this,3)
    private var mClassBean: CorrectClassBean?=null
    private var userItems= mutableListOf<TestPaperClassUserList.UserBean>()

    private var mAdapter:TestPaperCorrectUserAdapter?=null
    private var url=""//上个学生提交地址
    private var posImage=0//当前图片下标
    private var posUser=0//当前学生下标
    private var currentImages:Array<String>?=null
    private val commitItems = mutableListOf<ItemList>()
    private var initScores=mutableListOf<ExamScoreItem>()//初始题目分数
    private var recordPath = ""
    private var mediaPlayer: MediaPlayer? = null

    private var mModuleTopicAdapter:TopicScoreAdapter?=null
    private var mModuleMultiAdapter:TopicMultiScoreAdapter?=null

    private var userCorrect=0
    private var userSend=0
    private var positionModule=0 //模板指示器


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
                    map["score"]=tv_score_num.text.toString().toInt()
                    map["submitUrl"]=url
                    map["status"]=2
                    map["question"]=toJson(currentScores)
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
    override fun onClassPapers(bean: TestPaperClassUserList) {
        userItems=bean.list
        userCorrect=bean.totalUpdate
        userSend=bean.totalSend
        if (userItems.size>0){
            userItems[posUser].isCheck=true
            setContentView()
        }
        mAdapter?.setNewData(userItems)
    }
    override fun onCorrectSuccess() {
        showToast(userItems[posUser].name+getString(R.string.teaching_correct_success))
        userCorrect+=1
        userItems[posUser].score=tv_score_num.text.toString().toInt()
        userItems[posUser].submitUrl=url
        userItems[posUser].status=2
        userItems[posUser].question=toJson(currentScores)
        mAdapter?.notifyItemChanged(posUser)
        disMissView(tv_save)
        //批改完成之后删除文件夹
        FileUtils.deleteFile(File(getPath()))
        elik_a?.setPWEnabled(false,false)
        elik_b?.setPWEnabled(false,false)
        EventBus.getDefault().post(if (type==1)Constants.HOMEWORK_CORRECT_EVENT else Constants.EXAM_CORRECT_EVENT)
    }

    override fun onSendSuccess() {
        showToast(R.string.toast_send_success)
        mPresenter.getClassPapers(mClassBean?.examChangeId!!)
    }

    override fun setModuleSuccess() {
        showToast("上传模板成功")
        disMissView(ll_module_content)
        setContentView()
        EventBus.getDefault().post(if (type==1)Constants.HOMEWORK_CORRECT_EVENT else Constants.EXAM_CORRECT_EVENT)
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_correct
    }

    override fun initData() {
        type=intent.flags
        mId=intent.getIntExtra("id",0)
        correctModule=intent.getIntExtra("module",-1)
        mClassBean=intent.getBundleExtra("bundle")?.getSerializable("classBean") as CorrectClassBean
        subType = intent.getIntExtra("subType", 0)
        mPresenter.getClassPapers(mClassBean?.examChangeId!!)
    }

    override fun initView() {
        val title=if (type==1) "作业批改" else "试卷批改"
        setPageTitle("$title  ${mClassBean?.examName}  ${mClassBean?.name}")

        elik_b?.setPWEnabled(false,false)
        disMissView(iv_tool,iv_catalog,iv_btn)

        when(correctModule){
            -1->{
                showView(ll_module_content)
            }
            0->{
                disMissView(ll_module_content,rv_list_number)
            }
            else->{
                disMissView(ll_module_content)
                initRecyclerViewScore()
            }
        }

        //如果是朗读本
        if (subType == 3) {
            showView(ll_record)
            disMissView(ll_score,ll_topic,ll_content)
        } else {
            if (correctModule!=-1){
                disMissView(ll_record)
                showView(tv_custom,ll_content)
                tv_custom.text="发送批改"
            }
        }

        tv_custom.setOnClickListener {
            if (userCorrect>userSend)
                mPresenter.sendClass(mClassBean?.examChangeId!!)
        }

        tv_save.setOnClickListener {
            val item=userItems[posUser]
            if (item.status==1){
                showLoading()
                commitPapers()
            }
            hideKeyboard()
        }

        tv_score_num.setOnClickListener {
            val item=userItems[posUser]
            if (item.status==1){
                InputContentDialog(this,3,"请输入分数(整数)").builder().setOnDialogClickListener{
                    if (TextUtils.isDigitsOnly(it)) {
                        tv_score_num.text=it
                    }
                }
            }
        }

        iv_play.setOnClickListener {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(recordPath)
                mediaPlayer?.setOnCompletionListener {
                    changeMediaView(false)
                }
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                changeMediaView(true)
            }
            else{
                if (mediaPlayer?.isPlaying == true){
                    mediaPlayer?.pause()
                    changeMediaView(false)
                }
                else{
                    mediaPlayer?.start()
                    changeMediaView(true)
                }
            }
        }

        iv_1.setOnClickListener {
            setModuleView(1)
        }
        iv_2.setOnClickListener {
            setModuleView(2)

        }
        iv_3.setOnClickListener {
            setModuleView(3)
        }
        iv_4.setOnClickListener {
            setModuleView(4)
        }
        iv_close.setOnClickListener {
            CommonDialog(this).setContent("确定不使用模板？").builder().onDialogClickListener= object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    correctModule=0
                    val map=HashMap<String,Any>()
                    map["id"]=mId
                    map["questionType"]=correctModule
                    map["question"]=""
                    mPresenter.setModule(map)
                }
            }
        }
        tv_save_module.setOnClickListener {
            val map=HashMap<String,Any>()
            map["id"]=mId
            map["questionType"]=correctModule
            map["question"]=toJson(initScores)
            mPresenter.setModule(map)
        }

        initRecyclerView()
    }

    private fun setModuleView(type:Int){
        correctModule=type
        initRecyclerViewModule()
        initRecyclerViewScore()
        showView(tv_save_module,ll_topic)
        if (correctModule<3){
            disMissView(iv_close,ll_module)
            ll_topic_child.visibility= View.INVISIBLE
        }
        else{
            disMissView(iv_close,ll_module)
        }
        initModuleData()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,7)//创建布局管理
        mAdapter = TestPaperCorrectUserAdapter(R.layout.item_homework_correct_name, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(7, 0, 20))
            setOnItemClickListener { adapter, view, position ->
                if (position==posUser)
                    return@setOnItemClickListener
                userItems[posUser].isCheck=false
                posUser=position//设置当前学生下标
                val item=userItems[position]
                item.isCheck=true
                notifyDataSetChanged()
                posImage=0
                setContentView()
            }
        }
    }

    private fun initRecyclerViewModule(){
        iv_topic_reduce.setOnClickListener {
            if (initScores.size==0){
                return@setOnClickListener
            }
            if (correctModule<3){
                mModuleTopicAdapter?.remove(initScores.size-1)
            }
            else{
                mModuleMultiAdapter?.remove(initScores.size-1)
                if (positionModule>initScores.size-1){
                    positionModule=initScores.size-1
                    initScores.last().isCheck=true
                    mModuleMultiAdapter?.setNewData(initScores)
                }
            }
        }

        iv_topic_add.setOnClickListener {
            if (correctModule<3){
                initScores.add(ExamScoreItem().apply {
                    sort=initScores.size+1
                })
                mModuleTopicAdapter?.setNewData(initScores)
            }
            else{
                var num=0
                if (correctModule==4){
                    //以创建题目总数
                    for (item in initScores){
                        num+=item.childScores.size
                    }
                }
                initScores.add(ExamScoreItem().apply {
                    sort=initScores.size+1
                    for (i in 1..6){
                        childScores.add(ExamScoreItem().apply {
                            sort=num+i
                        })
                    }
                })
                for (item in initScores){
                    item.isCheck=false
                }
                positionModule=initScores.size-1
                initScores.last().isCheck=true
                mModuleMultiAdapter?.setNewData(initScores)
            }
        }

        iv_topic_reduce_child.setOnClickListener {
            val item=initScores[positionModule]
            if (item.childScores.size>1){
                item.childScores.removeLast()
                if (correctModule==4)
                    changeModuleData()
                mModuleMultiAdapter?.setNewData(initScores)
            }
        }

        iv_topic_add_child.setOnClickListener {
            val item=initScores[positionModule]
            item.childScores.add(ExamScoreItem().apply {
                sort=item.childScores.size+1
            })
            if (correctModule==4)
                changeModuleData()
            mModuleMultiAdapter?.setNewData(initScores)
        }

        if (correctModule<3){
            rv_list_module.layoutManager = GridLayoutManager(this,4)
            mModuleTopicAdapter = TopicScoreAdapter(R.layout.item_topic_score,null).apply {
                rv_list_module.adapter = this
                bindToRecyclerView(rv_list_module)
            }
        }
        else{
            rv_list_module.layoutManager = LinearLayoutManager(this)
            mModuleMultiAdapter = TopicMultiScoreAdapter(R.layout.item_topic_multi_score,null).apply {
                rv_list_module.adapter = this
                bindToRecyclerView(rv_list_module)
                setOnItemClickListener  { adapter, view, position ->
                    if (position!=positionModule){
                        val item=initScores[position]
                        positionModule=position
                        for (ite in initScores){
                            ite.isCheck=false
                        }
                        item.isCheck=true
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    /**
     * 重新排模板子序
     */
    private fun changeModuleData(){
        var num=1
        for (item in initScores){
            for (itm in item.childScores){
                itm.sort=num
                num+=1
            }
        }
    }

    /**
     * 初始化题目模板列表数据
     */
    private fun initModuleData(){
        if (correctModule<3){
            for (i in 1..10){
                initScores.add(ExamScoreItem().apply {
                    sort=i
                })
            }
            mModuleTopicAdapter?.setNewData(initScores)
            mModuleTopicAdapter?.setChangeModule(correctModule)
        }
        else{
            for (i in 1..3){
                val item =ExamScoreItem()
                item.sort=i
                item.isCheck=i==1
                for (j in 1..6){
                    item.childScores.add(ExamScoreItem().apply {
                        sort=if (correctModule==3) j else (i-1)*6+j
                    })
                }
                initScores.add(item)
            }
            mModuleMultiAdapter?.setNewData(initScores)
        }
    }


    override fun onChangeExpandContent() {
        if (getImageSize()==1)
            return
        if (posImage==getImageSize()-1&&getImageSize()>1)
            posImage=getImageSize()-2
        changeErasure()
        isExpand=!isExpand
        onChangeExpandView()
        setContentImage()
    }

    override fun onPageUp() {
        if (isExpand){
            if (posImage>1){
                posImage-=2
            }
            else if (posImage==1){
                posImage=0
            }
        }
        else{
            if (posImage > 0) {
                posImage -= 1
            }
        }
        setContentImage()
    }

    override fun onPageDown() {
        if (isExpand){
            if (posImage<getImageSize()-2){
                posImage+=2
            }
            else if (posImage==getImageSize()-2){
                posImage=getImageSize()-1
            }
        }
        else{
            if (posImage < getImageSize() - 1) {
                posImage += 1
            }
        }
        setContentImage()
    }

    /**
     * 设置切换内容展示
     */
    private fun setContentView(){
        if (isExpand){
            isExpand=false
            onChangeExpandView()
        }
        val userItem=userItems[posUser]

        if (subType==3){
            if (!userItem.studentUrl.isNullOrEmpty()) {
                recordPath = userItem.studentUrl.split(",")[0]
                showView(iv_file, ll_play)
            } else {
                disMissView(iv_file, ll_play)
            }
            return
        }

        when(userItem.status){
            1->{
                tv_score_num.text = ""
                currentImages=userItem.studentUrl.split(",").toTypedArray()
                showView(ll_score,rv_list_number,rv_list_score,tv_save)
                loadPapers()
            }
            2->{
                currentImages=userItem.submitUrl.split(",").toTypedArray()
                tv_score_num.text = userItem.score.toString()
                showView(ll_score,rv_list_score)
                disMissView(tv_save,rv_list_number)
                setContentImage()
            }
            3->{
                currentImages= arrayOf()
                disMissView(ll_score,rv_list_score,rv_list_number)
                v_content_a.setImageResource(0)
                v_content_b.setImageResource(0)
                elik_a?.setPWEnabled(false,false)
                elik_b?.setPWEnabled(false,false)
            }
        }

        if (correctModule>0){
            if (userItem.question?.isNotEmpty() == true&&correctModule>0){
                initScores= jsonToList(userItem.question) as MutableList<ExamScoreItem>
            }
            currentScores=initScores
            if (correctModule<3){
                mTopicScoreAdapter?.setNewData(currentScores)
                mTopicScoreAdapter?.setChangeModule(correctModule)
            }
            else{
                mTopicMultiAdapter?.setNewData(currentScores)
            }
        }
        else{
            if (userItem.status!=3){
                disMissView(rv_list_score,rv_list_number)
                showView(ll_total_score)
            }
        }
    }

    /**
     * 设置学生提交图片展示
     */
    private fun setContentImage(){
        if (isExpand){
            when(userItems[posUser].status){
                1->{
                    elik_a?.setPWEnabled(true,true)
                    elik_b?.setPWEnabled(true,true)

                    val masterImage="${getPath()}/${posImage+1}.png"//原图
                    GlideUtils.setImageFile(this,File(masterImage),v_content_a)
                    val drawPath = getPathDrawStr(posImage+1)
                    elik_a?.setLoadFilePath(drawPath, true)

                    if (posImage+1<getImageSize()){
                        val masterImage_b="${getPath()}/${posImage+1+1}.png"//原图
                        GlideUtils.setImageFile(this,File(masterImage_b),v_content_b)
                        val drawPath_b = getPathDrawStr(posImage+1+1)
                        elik_b?.setLoadFilePath(drawPath_b, true)
                    }
                    else{
                        elik_b?.setPWEnabled(false,false)
                        v_content_b.setImageResource(0)
                    }
                }
                2->{
                    elik_a?.setPWEnabled(false,false)
                    elik_b?.setPWEnabled(false,false)

                    GlideUtils.setImageUrl(this, currentImages?.get(posImage) ,v_content_a)
                    if (posImage+1<getImageSize()){
                        GlideUtils.setImageUrl(this, currentImages?.get(posImage+1) ,v_content_b)
                    }
                    else{
                        v_content_b.setImageResource(0)
                    }
                }
            }
            tv_page.text="${posImage+1}/${getImageSize()}"
            tv_page_a.text=if (posImage+1<getImageSize()) "${posImage+1+1}/${getImageSize()}" else ""
        }
        else{
            when(userItems[posUser].status){
                1->{
                    elik_b?.setPWEnabled(true,true)
                    val masterImage="${getPath()}/${posImage+1}.png"//原图
                    GlideUtils.setImageFile(this,File(masterImage),v_content_b)
                    val drawPath = getPathDrawStr(posImage+1)
                    elik_b?.setLoadFilePath(drawPath, true)
                }
                2->{
                    elik_b?.setPWEnabled(false,false)
                    GlideUtils.setImageUrl(this, currentImages?.get(posImage) ,v_content_b)
                }
            }
            tv_page.text="${posImage+1}/${getImageSize()}"
        }
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
     * 文件路径
     */
    private fun getPath():String{
        val path=if (type==1){
            FileAddress().getPathHomework(mId,mClassBean?.classId, userItems[posUser].userId)
        }
        else{
            FileAddress().getPathTestPaperDrawing(mId,mClassBean?.classId, userItems[posUser].userId)
        }
        return path
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int):String{
        return getPath()+"/draw${index}.tch"//手绘地址
    }

    /**
     * 更改播放view状态
     */
    private fun changeMediaView(boolean: Boolean){
        if (boolean){
            iv_play.setImageResource(R.mipmap.icon_record_pause)
            tv_play.setText(R.string.pause)
        }
        else{
            iv_play.setImageResource(R.mipmap.icon_record_play)
            tv_play.setText(R.string.play)
        }
    }


    private fun release(){
        if (mediaPlayer!=null){
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
        release()
    }

}