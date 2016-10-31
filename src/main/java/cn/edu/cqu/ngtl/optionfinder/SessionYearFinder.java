package cn.edu.cqu.ngtl.optionfinder;

import cn.edu.cqu.ngtl.dao.ut.UTSessionDao;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tangjing on 16-10-30.
 */
public class SessionYearFinder extends KeyValuesBase {

    @Autowired
    private UTSessionDao sessionDao;

    @Override
    public List<KeyValue> getKeyValues() {
        Calendar cal = Calendar.getInstance();
        int cuYear = cal.get(Calendar.YEAR);

        List<KeyValue> keyValues = new ArrayList();
        for(int offset = -4 ; offset < 2; offset++) {
            String year = new Integer(cuYear + offset).toString();
            keyValues.add(new ConcreteKeyValue(year , year));
        }

        return keyValues;
    }

}
