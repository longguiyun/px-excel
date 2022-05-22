package org.px.excel;
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
import org.px.excel.bean.PxBean;

import java.io.IOException;
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
public class SimpleHSSHExcelTest {

    String path = "C:\\Users\\Administrator\\Desktop";

    @Test
    public void fillDataTest() throws IOException {
        String file = path + "\\test.xls";
        PxBean b1 = new PxBean();
        b1.setName("xxx");
        b1.setCode("ddd");

        PxBean b2 = new PxBean();
        b2.setName("sss");
        b2.setCode("sss");
        b2.setXx("sss");

        List<PxBean> data = new ArrayList<>();
        data.add(b1);
        data.add(b2);

        SimpleHSSHExcel<PxBean> excel = new SimpleHSSHExcel<>(PxBean.class);
        excel.data(data);
        excel.flush(file);
    }
}
