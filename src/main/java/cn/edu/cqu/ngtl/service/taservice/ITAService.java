package cn.edu.cqu.ngtl.service.taservice;

import cn.edu.cqu.ngtl.dataobject.view.UTClassInformation;

/**
 * Created by tangjing on 16-10-19.
 */
public interface ITAService {

    UTClassInformation getClassInfoById(Integer id);

}