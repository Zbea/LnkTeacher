package com.bll.lnkteacher

import com.bll.lnkteacher.MyApplication.Companion.mContext
import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.mvp.model.catalog.CatalogChildBean
import com.bll.lnkteacher.mvp.model.catalog.CatalogParentBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.homework.ResultStandardItem
import com.chad.library.adapter.base.entity.MultiItemEntity
import java.util.*

object DataBeanManager {

    var classGroups = mutableListOf<ClassGroup>()

    var grades = mutableListOf<ItemList>()
    var typeGrades = mutableListOf<ItemList>()
    var courses = mutableListOf<ItemList>()
    var versions = mutableListOf<ItemList>()

    private val cloudListTitle = arrayOf("书架","教材","笔记","日记","截图")

    val textbookStoreType = arrayOf(
        mContext.getString(R.string.textbook_tab_text),
        mContext.getString(R.string.textbook_tab_course),
        mContext.getString(R.string.textbook_tab_homework),
        mContext.getString(R.string.textbook_tab_homework_other),
        mContext.getString(R.string.textbook_tab_teaching),
    )

    val textbookType = arrayOf(
        "课本教材","课辅教材","教学教育","专业期刊","我的文档"
    )

    var teachingStrs = arrayOf(
        mContext.getString(R.string.teaching_tab_homework_assign),
        mContext.getString(R.string.teaching_tab_homework_correct),
    )

    var testPaperStrs = arrayOf(
        mContext.getString(R.string.teaching_tab_testpaper_assign),
        mContext.getString(R.string.teaching_tab_testpaper_correct)
    )

    var resources = arrayOf("我的工具","锁屏壁纸","跳页日历")


    fun getClassGroups(grade: Int):MutableList<ClassGroup>{
        val items= mutableListOf<ClassGroup>()
        for (classGroup in classGroups){
            if (classGroup.grade==grade){
                classGroup.isCheck=false
                items.add(classGroup)
            }
        }
        return items
    }

    /**
     * 获取当年级主群
     */
    fun getClassGroupByMains(grade: Int):MutableList<ClassGroup>{
        val items= mutableListOf<ClassGroup>()
        for (classGroup in classGroups){
            if (classGroup.grade==grade&&classGroup.state==1){
                classGroup.isCheck=false
                items.add(classGroup)
            }
        }
        return items
    }

    fun getClassGroupPopsByGrade(grade: Int): MutableList<PopupBean> {
        val popClasss = mutableListOf<PopupBean>()
        for (item in classGroups) {
            if (item.grade == grade) {
                popClasss.add(PopupBean(item.classId, item.name, false))
            }
        }
        return popClasss
    }

    fun getClassGroupPopsByClassIds(classIds:MutableList<Int> ): MutableList<PopupBean> {
        val popClasss = mutableListOf<PopupBean>()
        for (item in classGroups) {
            if (classIds.contains(item.classId)) {
                popClasss.add(PopupBean(item.classId, item.name, false))
            }
        }
        return popClasss
    }

    /**
     * 获取第一个主群的年级
     */
    fun getClassGroupsGrade():Int{
        for (item in classGroups) {
            if (item.state==1) {
                return item.grade
            }
        }
        return 0
    }

    fun getClassGroupPopsOtherClassId(classId:Int): MutableList<PopupBean>
       {
            val popClasss = mutableListOf<PopupBean>()
            for (item in classGroups) {
                if (item.state==1&&item.classId!=classId)
                    popClasss.add(PopupBean(item.classId, item.name, false))
            }
            return popClasss
        }

    val popupGrades: MutableList<PopupBean>
        get() {
            val list = mutableListOf<PopupBean>()
            for (i in grades.indices) {
                list.add(PopupBean(grades[i].type, grades[i].desc, false))
            }
            return list
        }

    fun popupGrades(grade: Int): MutableList<PopupBean> {
        val list = mutableListOf<PopupBean>()
        for (item in grades) {
            list.add(PopupBean(item.type, item.desc, item.type == grade))
        }
        return list
    }

    fun getGradeStr(grade: Int): String {
        var cls=""
        for (item in grades) {
            if (item.type == grade){
                cls=item.desc
            }
        }
        return cls
    }

    fun getCourseId(course: String): Int {
        var courseId=0
        for (item in courses) {
            if (item.desc == course){
                courseId=item.type
            }
        }
        return courseId
    }

    fun getCourseStr(courseId: Int): String {
        var courseStr=""
        for (item in courses) {
            if (item.type == courseId){
                courseStr=item.desc
            }
        }
        return courseStr
    }

    val popupTypeGrades: MutableList<PopupBean>
        get() {
            val list = mutableListOf<PopupBean>()
            for (i in typeGrades.indices) {
                list.add(PopupBean(typeGrades[i].type, typeGrades[i].desc, i == getTypeGradePos()))
            }
            return list
        }

    /**
     * 获取位置
     */
    fun getTypeGradePos(): Int
    {
        val grade=getClassGroupsGrade()
        val type=if (grade<4){
            0
        }
        else if (grade in 4..6){
            1
        }
        else if (grade in 7..9){
            2
        }
        else{
            3
        }
        return type
    }

    val popupCourses: MutableList<PopupBean>
        get() {
            val list = mutableListOf<PopupBean>()
            for (i in courses.indices) {
                list.add(PopupBean(courses[i].type, courses[i].desc, i == 0))
            }
            return list
        }

    fun popupCourses(courseId: Int): MutableList<PopupBean>
        {
            val list = mutableListOf<PopupBean>()
            for (i in courses.indices) {
                list.add(PopupBean(courses[i].type, courses[i].desc, courses[i].type == courseId))
            }
            return list
        }


    fun getTextbookFragment(): MutableList<ItemList> {
        val list = mutableListOf<ItemList>()
        list.add(ItemList().apply {
            id=0
            desc= textbookType[0]
        })
        list.add(ItemList().apply {
            id=1
            desc= textbookType[1]
        })
        list.add(ItemList().apply {
            id=2
            desc= textbookType[2]
        })
        list.add(ItemList().apply {
            id=3
            desc= textbookType[3]
        })
        return list
    }

    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    fun getMainCloud(): MutableList<ItemList> {
        val list = mutableListOf<ItemList>()
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_bookcase)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_bookcase_check)
            isCheck = true
            name = cloudListTitle[0]
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_teaching)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_teaching_check)
            name = cloudListTitle[1]
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_note)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_note_check)
            name = cloudListTitle[2]
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_diary)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_diary_check)
            name = cloudListTitle[3]
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_screenshot)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_screenshot_check)
            name = cloudListTitle[4]
        })
        return list
    }


    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    fun getIndexLeftData(): MutableList<ItemList> {
        val list = mutableListOf<ItemList>()
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_home)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_home_check)
            isCheck = true
            name = "首页"
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_bookcase)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_bookcase_check)
            name = "书架"
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_teaching)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_teaching_check)
            name = "教学"
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_learn_condition)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_learn_condition_check)
            name = "教情"
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_app)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_app_check)
            name = "应用"
        })
        return list
    }


    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    fun getIndexRightData(): MutableList<ItemList> {
        val list = mutableListOf<ItemList>()
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_home)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_home_check)
            isCheck = true
            name = "首页"
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_homework)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_homework_check)
            name = "作业"
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_testpaper)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_testpaper_check)
            name = "测卷"
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_paper)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_paper_check)
            name = "考卷"
        })
        list.add(ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_note)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_note_check)
            name = "笔记"
        })
        return list
    }

    val freenoteModules: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_kbb)
                resId = R.drawable.bg_black_stroke_10dp_corner
                resContentId = 0
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_hg_11
                resContentId = R.mipmap.icon_freenote_bg_1
            })
            return list
        }

    //日记内容选择
    val diaryModules: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_hgb_11)
                resId = R.mipmap.icon_diary_module_bg_1
                resContentId = R.mipmap.icon_diary_details_bg_1
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_hgb_9)
                resId = R.mipmap.icon_diary_module_bg_3
                resContentId = R.mipmap.icon_diary_details_bg_3
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_fgb_10)
                resId = R.mipmap.icon_diary_module_bg_2
                resContentId = R.mipmap.icon_diary_details_bg_2
            })
            list.add(ModuleBean().apply {
                name =mContext.getString(R.string.diary_type_fgb_8_5)
                resId = R.mipmap.icon_diary_module_bg_4
                resContentId = R.mipmap.icon_diary_details_bg_4
            })
            return list
        }

    //笔记本内容选择
    val noteModuleBook: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_hgb_11)
                resId = R.mipmap.icon_note_module_hg_11
                resContentId = R.mipmap.icon_note_content_hg_11
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_hgb_9)
                resId = R.mipmap.icon_note_module_hg_9
                resContentId = R.mipmap.icon_note_content_hg_9
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_hgb_7)
                resId = R.mipmap.icon_note_module_hg_7
                resContentId = R.mipmap.icon_note_content_hg_7
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_fgb_10)
                resId = R.mipmap.icon_note_module_fg_10
                resContentId = R.mipmap.icon_note_content_fg_10
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_fgb_8_5)
                resId = R.mipmap.icon_note_module_fg_8_5
                resContentId = R.mipmap.icon_note_content_fg_8_5
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_fgb_7)
                resId = R.mipmap.icon_note_module_fg_7
                resContentId = R.mipmap.icon_note_content_fg_7
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_yyb_3_5)
                resId = R.mipmap.icon_note_module_yy_3_5
                resContentId = R.mipmap.icon_note_content_yy_3_5
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_yyb_3)
                resId = R.mipmap.icon_note_module_yy_3
                resContentId = R.mipmap.icon_note_content_yy_3
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_wxp)
                resId = R.mipmap.icon_note_module_wxp
                resContentId = R.mipmap.icon_note_content_wxp
            })
            return list
        }


    //学期选择
    val popupSemesters: MutableList<PopupBean>
        get() {
            val list = mutableListOf<PopupBean>()
            list.add(PopupBean(1, mContext.getString(R.string.semester_last), true))
            list.add(PopupBean(2, mContext.getString(R.string.semester_next), false))
            return list
        }

    fun popupSemesters(semester:Int):MutableList<PopupBean>{
        val list= mutableListOf<PopupBean>()
        for (item in popupSemesters){
            list.add(PopupBean(item.id, item.name, item.id == semester))
        }
        return list
    }

    val popupSupplys: MutableList<PopupBean>
        get() {
            val list = mutableListOf<PopupBean>()
            list.add(PopupBean(1, "官方", true))
            list.add(PopupBean(2, "第三方", false))
            return list
        }

    val bookStoreTypes: MutableList<ItemList>
        get() {
            val list = mutableListOf<ItemList>()
            list.add(ItemList(1, mContext.getString(R.string.book_tab_gj)))
            list.add(ItemList(2, mContext.getString(R.string.book_tab_zrkx)))
            list.add(ItemList(3, mContext.getString(R.string.book_tab_shkx)))
            list.add(ItemList(4, mContext.getString(R.string.book_tab_sxkx)))
            list.add(ItemList(5, mContext.getString(R.string.book_tab_yscn)))
            list.add(ItemList(6, mContext.getString(R.string.book_tab_ydjk)))
            return list
        }

    fun getBookVersionStr(version:String):String{
        var versionStr=""
        for (item in versions){
            if (item.type.toString()==version)
                versionStr=item.desc
        }
        return versionStr
    }

    private fun getResultChildItems():MutableList<ResultStandardItem.ResultChildItem>{
        val items= mutableListOf<ResultStandardItem.ResultChildItem>()
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=1
            sortStr="A"
            score=92.5
            isCheck=false
        })
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=2
            sortStr="B"
            score=77.5
            isCheck=false
        })
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=3
            sortStr="C"
            score=62.5
            isCheck=false
        })
        return items
    }

    private fun getResultChildHighItems():MutableList<ResultStandardItem.ResultChildItem>{
        val items= mutableListOf<ResultStandardItem.ResultChildItem>()
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=1
            sortStr="A+"
            score=97.5
            isCheck=false
        })
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=2
            sortStr="A "
            score=92.5
            isCheck=false
        })
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=3
            sortStr="A-"
            score=87.5
            isCheck=false
        })
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=4
            sortStr="B+"
            score=82.5
            isCheck=false
        })
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=5
            sortStr="B "
            score=77.5
            isCheck=false
        })
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=6
            sortStr="B-"
            score=72.5
            isCheck=false
        })
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=7
            sortStr="C+"
            score=67.5
            isCheck=false
        })
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=8
            sortStr="C "
            score=62.5
            isCheck=false
        })
        items.add(ResultStandardItem.ResultChildItem().apply {
            sort=9
            sortStr="C-"
            score=57.5
            isCheck=false
        })
        return items
    }

    /**
     * 练字评分
     */
    private fun getResultStandardItem6s(correctModule:Int):MutableList<ResultStandardItem>{
        val items= mutableListOf<ResultStandardItem>()
        items.add(ResultStandardItem().apply {
            title="比例匀称"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        items.add(ResultStandardItem().apply {
            title="字迹工整"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        items.add(ResultStandardItem().apply {
            title="卷面整洁"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        return items
    }

    /**
     * 朗读评分
     */
    private fun getResultStandardItem3s(correctModule:Int):MutableList<ResultStandardItem>{
        val items= mutableListOf<ResultStandardItem>()
        items.add(ResultStandardItem().apply {
            title="语言标准"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        items.add(ResultStandardItem().apply {
            title="词汇语法"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        items.add(ResultStandardItem().apply {
            title="流畅程度"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        return items
    }

    /**
     * 阅读评分
     */
    private fun getResultStandardItem8s(correctModule:Int):MutableList<ResultStandardItem>{
        val items= mutableListOf<ResultStandardItem>()
        items.add(ResultStandardItem().apply {
            title="词句摘抄"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        items.add(ResultStandardItem().apply {
            title="阅读感想"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        items.add(ResultStandardItem().apply {
            title="卷面整洁"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        return items
    }

    /**
     * 作文评分
     */
    private fun getResultStandardItem2s(correctModule:Int):MutableList<ResultStandardItem>{
        val items= mutableListOf<ResultStandardItem>()
        items.add(ResultStandardItem().apply {
            title="思想内容"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        items.add(ResultStandardItem().apply {
            title="语言文字"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        items.add(ResultStandardItem().apply {
            title="层次结构"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        items.add(ResultStandardItem().apply {
            title="卷面书写"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        return items
    }

    /**
     * 手写
     */
    private fun getResultStandardItem0s(correctModule:Int):MutableList<ResultStandardItem>{
        val items= mutableListOf<ResultStandardItem>()
        items.add(ResultStandardItem().apply {
            title="标准评分"
            list= if (correctModule==2) getResultChildHighItems() else getResultChildItems()
        })
        return items
    }

    /**
     * 返回标准评分
     * @param score
     * @return
     */
    fun getResultStandardStr(score: Double,questionType:Int): String {
        if (questionType==2){
            return when (score) {
                1.0 -> {
                    "A+"
                }
                2.0 -> {
                    "A "
                }
                3.0 -> {
                    "A-"
                }
                4.0 -> {
                    "B+"
                }
                5.0 -> {
                    "B "
                }
                6.0 -> {
                    "B-"
                }
                7.0 -> {
                    "C+"
                }
                8.0 -> {
                    "C "
                }
                else -> {
                    "C-"
                }
            }
        }
        else{
            return when (score) {
                1.0 -> {
                    "A"
                }
                2.0 -> {
                    "B"
                }
                else -> {
                    "C"
                }
            }
        }
    }


    /**
     * 获取评分列表
     */
    fun getResultStandardItems(state:Int, name: String, correctModule:Int):MutableList<ResultStandardItem>{
        return when(state){
            3->{
                getResultStandardItem3s(correctModule)
            }
            6->{
                getResultStandardItem6s(correctModule)
            }
            8->{
                getResultStandardItem8s(correctModule)
            }
            else->{
                if (name=="作文作业本"){
                    getResultStandardItem2s(correctModule)
                }
                else{
                    getResultStandardItem0s(correctModule)
                }
            }
        }
    }


    fun operatingGuideInfo():List<MultiItemEntity>{
        val list= mutableListOf<MultiItemEntity>()
        val types= mutableListOf("一、主页面","二、管理中心","三、作业书籍","四、教学工具")
        val mainStrs= mutableListOf("注册账号","按键/接口","状态栏按钮","账户/班群","窗口功能","书架/作业","教学/测卷","教情/考卷","应用/笔记")
        val managerStrs= mutableListOf("管理中心","云书库","书城","资源")
        val bookStrs= mutableListOf("课本/文档","书籍/规划","日记本/随笔","作业布置","批改界面","作业统计数据","试卷统计数据","层群创建/成绩排序")
        val toolStrs= mutableListOf("排课表","教学计划","我的日历","我的工具","截屏","几何绘图")
        val childTypes= mutableListOf(mainStrs,managerStrs,bookStrs,toolStrs)
        for (type in types){
            val index=types.indexOf(type)
            val catalogParent = CatalogParentBean()
            catalogParent.title=type
            for (childType in childTypes[index]){
                val catalogChild = CatalogChildBean()
                catalogChild.title = childType
                catalogChild.parentPosition=index
                catalogChild.pageNumber = childTypes[index].indexOf(childType)+1
                catalogParent.addSubItem(catalogChild)
            }
            list.add(catalogParent)
        }
        return list
    }
}