package cn.edu.cqu.ngtl.dataobject.cm;

import cn.edu.cqu.ngtl.dataobject.ut.UTCourse;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.kuali.rice.krad.bo.DataObjectBase;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by tangjing on 16-10-12.
 */
@Entity
@Table(name = "CM_PROGRAM_COURSE")
public class CMProgramCourse extends DataObjectBase implements Serializable {

    private static final long serialVersionUID = -8218284296080572819L;

    /** Properties and Foreign Properties **/

    @Id
    @Column(name="UNIQUEID")
    private Integer id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="PROGRAM_ID", insertable=false, updatable=false)
    private CMProgram program;

    @Column(name="PROGRAM_ID")
    private Integer programId;

    @Column(name="COURSE_ID")
    private Integer courseId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="COURSE_ID", updatable=false,insertable=false)
    private UTCourse course;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="CLASSIFICATION_ID", insertable=false, updatable=false)
    private CMCourseClassification classification;

    @Column(name="CLASSIFICATION_ID")
    private Integer classificationId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="SUBCLASSIFICATION_ID", insertable=false, updatable=false)
    private CMCourseSubclassification subclassification;

    @Column(name="SUBCLASSIFICATION_ID")
    private Integer subclassificationId;

    @Column(name="REQUIRED")
    private Short required;

    @Column(name="RCMD_TEACHING_SEMESTER")
    private String semester;

    /** Start Setter and Getter **/

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public UTCourse getCourse() {
        return course;
    }

    public void setCourse(UTCourse course) {
        this.course = course;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CMProgram getProgram() {
        return program;
    }

    public void setProgram(CMProgram program) {
        this.program = program;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public CMCourseClassification getClassification() {
        return classification;
    }

    public void setClassification(CMCourseClassification classification) {
        this.classification = classification;
    }

    public Integer getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(Integer classificationId) {
        this.classificationId = classificationId;
    }

    public CMCourseSubclassification getSubclassification() {
        return subclassification;
    }

    public void setSubclassification(CMCourseSubclassification subclassification) {
        this.subclassification = subclassification;
    }

    public Integer getSubclassificationId() {
        return subclassificationId;
    }

    public void setSubclassificationId(Integer subclassificationId) {
        this.subclassificationId = subclassificationId;
    }

    public Short getRequired() {
        return required;
    }

    public void setRequired(Short required) {
        this.required = required;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    /** End of Setter and Getter **/

}
