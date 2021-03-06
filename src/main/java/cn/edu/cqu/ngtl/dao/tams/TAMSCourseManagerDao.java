package cn.edu.cqu.ngtl.dao.tams;

import cn.edu.cqu.ngtl.dataobject.tams.TAMSCourseManager;

import java.util.List;
import java.util.Map;
/**
 * Created by awake on 2016-10-26.
 */
public interface TAMSCourseManagerDao {


     List<TAMSCourseManager> getAllCourseManager();

     TAMSCourseManager getCourseManagerByInstructorId(String instructorId);

     boolean saveCourseManager(TAMSCourseManager tamsCourseManager);

     void deleteCourseManager(TAMSCourseManager tamsCourseManager);

     List<TAMSCourseManager> selectCourseManagerByCondition(Map<String, String> conditions);

     List<TAMSCourseManager> selectCourseManagerByConditionWithDeptId(Map<String, String> conditions,String departmentId);

     TAMSCourseManager getCourseManagerByCourseId(String courseId);

     List<TAMSCourseManager> getCourseManagerByDeptId(String departmentId);

     List<Object> getCouseIdsByManagerId(String managerId);

}
