package org.px.excel.builder;
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


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.px.excel.core.PxDynamicHeadExcel;
import org.px.excel.core.PxExcel;

import java.beans.IntrospectionException;

/**
 * <p>
 * description
 * </p>
 *
 * @author px
 * @version 1.0.0
 */
public class PxExcelBuilder {

    PxExcel pxExcel;

    Workbook wb;

    public static PxExcelBuilder builder() throws IntrospectionException {
        return new PxExcelBuilder();
    }

    public <T> PxDynamicHeadExcel<T> pxDynamic(Class clazz) throws IntrospectionException {

        PxDynamicHeadExcel<T> pxDynamicHeadExcel = new PxDynamicHeadExcel<>(clazz);
        pxDynamicHeadExcel.init();
        pxDynamicHeadExcel.setWb(this.wb);
        return pxDynamicHeadExcel;
    }

    public PxExcelBuilder buildHSSFWb() throws IntrospectionException {
        this.wb = new HSSFWorkbook();
        return this;
    }


}
