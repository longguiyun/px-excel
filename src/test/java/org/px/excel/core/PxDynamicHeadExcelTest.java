package org.px.excel.core;
/*
 * Copyright 2022-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.junit.jupiter.api.Test;
import org.px.excel.bean.BeanA;
import org.px.excel.bean.BeanB;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author px
 * @version 1.0.0
 */
public class PxDynamicHeadExcelTest {



    @Test
    public void test1() throws IntrospectionException, InvocationTargetException, IllegalAccessException, IOException {

        File file = new File("");
        String path = file.getCanonicalPath() + "\\files\\";

        BeanB b1 = new BeanB();
        b1.setCode("yuwen");
        b1.setScore(100);

        BeanB b2 = new BeanB();
        b2.setCode("shuxue");
        b2.setScore(95);

        List<BeanB> bbs = new ArrayList<>();
        bbs.add(b1);
        bbs.add(b2);

        BeanA a = new BeanA();
        a.setName("a");
        a.setNickname("a_a");
        a.setBbs(bbs);

        List<BeanA> list = new ArrayList<>();
        list.add(a);

        PxDynamicHeadExcel<BeanA> px = new PxDynamicHeadExcel<>(BeanA.class);
        px.init();

        px.addDynamicHead(null,"score","成绩");
        px.addDynamicHead("score","yuwen","语文");
        px.addDynamicHead("score","shuxue","数学");

        px.export("xxx",list);

        FileOutputStream fos = new FileOutputStream(path + "test.xls");
        px.flush(fos);


    }
}
