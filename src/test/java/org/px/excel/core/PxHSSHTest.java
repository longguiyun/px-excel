package org.px.excel.core;/*
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

/**
 * <p>description</p>
 *
 * @author px
 * @version 1.0.0
 */
public class PxHSSHTest {

    String path = "C:\\Users\\Administrator\\Desktop";

    @Test
    public void createTest() throws IOException {
        String file = path + "\\test.xls";
        PxHSSH pxHssh = new PxHSSH();
        Sheet sheet = pxHssh.createSheet("xxx");

        PxSheet pxSheet = new PxSheet() {
            @Override
            public Row createRow(int rowNum) {
                return super.createRow(rowNum);
            }
        };

        pxSheet.setSheet(sheet);

        Row row = pxSheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(new Date());

        pxHssh.flush(file);
    }
}
