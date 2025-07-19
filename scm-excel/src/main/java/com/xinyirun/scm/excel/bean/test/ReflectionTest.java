package com.xinyirun.scm.excel.bean.test;

import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutImportVo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;

@Slf4j
public class ReflectionTest {

    public static void main(String[] args) {
        try {
            Method onLoaded2 = SomeClass.class.getMethod("someMethod",  new Class[]{BOutImportVo.class, ArrayList.class}  );

            SomeClass someClass = new SomeClass();
            ArrayList list = new ArrayList();
            BOutImportVo vo = new BOutImportVo();
            onLoaded2.invoke(someClass, null , list); // List size : 3

        } catch (Exception e) {
            log.error("ReflectionTest error", e);
        }
    }

}

class AW{}

class SomeClass{

    public void someMethod(BOutImportVo vo,  ArrayList<BOutImportVo> list) {
        int size = (list != null) ? list.size() : 0;
        System.out.println("List size : " + size);
    }

}