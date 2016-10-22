package cn.edu.cqu.ngtl.service.taservice.impl;

import cn.edu.cqu.ngtl.dao.ut.UTClassDao;
import cn.edu.cqu.ngtl.dao.ut.UTClassInfoDao;
import cn.edu.cqu.ngtl.dataobject.ut.UTClass;
import cn.edu.cqu.ngtl.dataobject.view.UTClassInformation;
import cn.edu.cqu.ngtl.service.taservice.ITAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by tangjing on 16-10-19.
 */
@Service
public class TAServiceimpl implements ITAService {

    @Autowired
    private UTClassInfoDao classInfoDao;

    @Autowired
    private UTClassDao classDao;

    @Override
    public UTClassInformation getClassInfoById(Integer id) {
        return classInfoDao.getOneById(id);
    }

    @Override
    public UTClass applicationTable(Integer classId) {

        UTClass clazz = classDao.selectByClassId(classId);

        return clazz;

    }

    @Override
    public UTClass applicationAssitantTable(Integer classId) {

        UTClass clazz = classDao.selectByClassId(classId);

        return clazz;

    }
}
