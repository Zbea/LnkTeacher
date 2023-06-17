package com.bll.lnkteacher.mvp.model;
import com.bll.lnkteacher.utils.SPUtil;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 书籍
 */
@Entity
public class Book {

    @Id(autoincrement = true)
    @Unique
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    @Unique
    public int bookId;
    public String imageUrl;
    public int subjectName;//课目
    public String createdAt;
    public String bookDesc;//描述
    public int semester;//学期
    public String area;//地区
    public String bookName;//书名
    public int price;//书的价格
    public int grade; //年级
    public String bookVersion;//版本
    public String version;//版本
    public String supply ;  //官方
    public int category;//0教材1古籍2自然科学3社会科学4思维科学5运动才艺
    public int type;
    public String textBookType;  //教材类别
    public int status;
    @SerializedName("bodyUrl")
    public String downloadUrl;//书籍下载url
    public String bookPath;  //book书的路径
    public Long time;//观看时间
    public int pageIndex=0;//当前页
    public String pageUrl;//当前页路径
    @Transient
    public int loadSate=0;//0未下载 1正下载 2已下载
    @Transient
    public int buyStatus;
    @Generated(hash = 2101682676)
    public Book(Long id, long userId, int bookId, String imageUrl, int subjectName,
            String createdAt, String bookDesc, int semester, String area,
            String bookName, int price, int grade, String bookVersion,
            String version, String supply, int category, int type,
            String textBookType, int status, String downloadUrl, String bookPath,
            Long time, int pageIndex, String pageUrl) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.imageUrl = imageUrl;
        this.subjectName = subjectName;
        this.createdAt = createdAt;
        this.bookDesc = bookDesc;
        this.semester = semester;
        this.area = area;
        this.bookName = bookName;
        this.price = price;
        this.grade = grade;
        this.bookVersion = bookVersion;
        this.version = version;
        this.supply = supply;
        this.category = category;
        this.type = type;
        this.textBookType = textBookType;
        this.status = status;
        this.downloadUrl = downloadUrl;
        this.bookPath = bookPath;
        this.time = time;
        this.pageIndex = pageIndex;
        this.pageUrl = pageUrl;
    }
    @Generated(hash = 1839243756)
    public Book() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public int getBookId() {
        return this.bookId;
    }
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public int getSubjectName() {
        return this.subjectName;
    }
    public void setSubjectName(int subjectName) {
        this.subjectName = subjectName;
    }
    public String getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getBookDesc() {
        return this.bookDesc;
    }
    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }
    public int getSemester() {
        return this.semester;
    }
    public void setSemester(int semester) {
        this.semester = semester;
    }
    public String getArea() {
        return this.area;
    }
    public void setArea(String area) {
        this.area = area;
    }
    public String getBookName() {
        return this.bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public int getPrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public String getBookVersion() {
        return this.bookVersion;
    }
    public void setBookVersion(String bookVersion) {
        this.bookVersion = bookVersion;
    }
    public String getVersion() {
        return this.version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getSupply() {
        return this.supply;
    }
    public void setSupply(String supply) {
        this.supply = supply;
    }
    public int getCategory() {
        return this.category;
    }
    public void setCategory(int category) {
        this.category = category;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getTextBookType() {
        return this.textBookType;
    }
    public void setTextBookType(String textBookType) {
        this.textBookType = textBookType;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public String getBookPath() {
        return this.bookPath;
    }
    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }
    public Long getTime() {
        return this.time;
    }
    public void setTime(Long time) {
        this.time = time;
    }
    public int getPageIndex() {
        return this.pageIndex;
    }
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
    public String getPageUrl() {
        return this.pageUrl;
    }
    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

  

}
