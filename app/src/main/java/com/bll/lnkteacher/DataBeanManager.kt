package com.bll.lnkteacher

import com.bll.lnkteacher.MyApplication.Companion.mContext
import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import java.util.*

object DataBeanManager {

    var classGroups = mutableListOf<ClassGroup>()

    var grades = mutableListOf<Grade>()
    var typeGrades = mutableListOf<Grade>()
    var courses = mutableListOf<ItemList>()
    var provinces = mutableListOf<AreaBean>()
    var versions = mutableListOf<ItemList>()

    private val cloudListTitle = arrayOf("书架","教材","笔记","日记","截图")

    val textbookType = arrayOf(
        mContext.getString(R.string.textbook_tab_text),
        mContext.getString(R.string.textbook_tab_course),
        mContext.getString(R.string.textbook_tab_homework),
        mContext.getString(R.string.textbook_tab_homework_other),
        mContext.getString(R.string.textbook_tab_teaching),
        "我的讲义"
    )

    var teachingStrs = arrayOf(
        mContext.getString(R.string.teaching_tab_homework_assign),
        mContext.getString(R.string.teaching_tab_homework_correct),
    )

    var testPaperStrs = arrayOf(
        mContext.getString(R.string.teaching_tab_testpaper_assign),
        mContext.getString(R.string.teaching_tab_testpaper_correct)
    )

    var resources = arrayOf("新闻报刊","书籍阅读","期刊杂志","实用工具","锁屏壁纸","跳页日历")

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
            for (i in classGroups.indices) {
                val item = classGroups[i]
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
                list.add(PopupBean(typeGrades[i].type, typeGrades[i].desc, i == 0))
            }
            return list
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
                resId = R.drawable.bg_gray_stroke_10dp_corner
                resContentId = 0
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_freenote_bg_1
            })
            return list
        }

    //日记内容选择
    val noteModuleDiary: MutableList<ModuleBean>
        get() {
            val list = mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_diary_details_bg_1
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_fgb)
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_diary_details_bg_2
            })
            return list
        }

    //笔记本内容选择
    val noteModuleBook: MutableList<ModuleBean>
        get() {
            val list = mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_kbb)
                resId = R.drawable.bg_gray_stroke_10dp_corner
                resContentId = 0
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_note_details_bg_1
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_fgb)
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_note_details_bg_2
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_yyb)
                resId = R.mipmap.icon_note_module_bg_3
                resContentId = R.mipmap.icon_note_details_bg_3
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_tzb)
                resId = R.mipmap.icon_note_module_bg_4
                resContentId = R.mipmap.icon_note_details_bg_4
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_wxp)
                resId = R.mipmap.icon_note_module_bg_5
                resContentId = R.mipmap.icon_note_details_bg_5
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
}