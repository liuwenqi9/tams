package cn.edu.cqu.ngtl.viewobject.tainfo;

/**
 * Created by tangjing on 16-11-1.
 */
public class TaInfoViewObject {

    //id用于和后台进行交互，不要把id用于前端显示
    private String id;

    private String classid;

    private String taId;

    private boolean checkBox;

    private String courseName;

    private String courseCode;

    private String classNumber;

    private String instructorName;

    private String taName;

    private String taIDNumber;

    private String taGender;

    private String taBachelorMajorName;

    private String taMasterMajorName;

    private String advisorName;

    private String contactPhone;

    private String vitality;

    private String teacherAppraise;

    private String stuAppraise;

    private String status;

    private String applicationReason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCheckBox() {
        return checkBox;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getTaName() {
        return taName;
    }

    public void setTaName(String taName) {
        this.taName = taName;
    }

    public String getTaIDNumber() {
        return taIDNumber;
    }

    public void setTaIDNumber(String taIDNumber) {
        this.taIDNumber = taIDNumber;
    }

    public String getTaGender() {
        return taGender;
    }

    public void setTaGender(String taGender) {
        this.taGender = taGender;
    }

    public String getTaBachelorMajorName() {
        return taBachelorMajorName;
    }

    public void setTaBachelorMajorName(String taBachelorMajorName) {
        this.taBachelorMajorName = taBachelorMajorName;
    }

    public String getTaMasterMajorName() {
        return taMasterMajorName;
    }

    public void setTaMasterMajorName(String taMasterMajorName) {
        this.taMasterMajorName = taMasterMajorName;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getVitality() {
        return vitality;
    }

    public void setVitality(String vitality) {
        this.vitality = vitality;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getTaId() {
        return taId;
    }

    public void setTaId(String taId) {
        this.taId = taId;
    }

    public String getApplicationReason() {
        return applicationReason;
    }

    public void setApplicationReason(String applicationReason) {
        this.applicationReason = applicationReason;
    }

    public String getTeacherAppraise() {
        return teacherAppraise;
    }

    public void setTeacherAppraise(String teacherAppraise) {
        this.teacherAppraise = teacherAppraise;
    }

    public String getStuAppraise() {
        return stuAppraise;
    }

    public void setStuAppraise(String stuAppraise) {
        this.stuAppraise = stuAppraise;
    }
}
