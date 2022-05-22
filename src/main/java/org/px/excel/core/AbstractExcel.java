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

package org.px.excel.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 *
 * </p>
 *
 * @author px
 * @version 1.0.0
 */
public abstract class AbstractExcel {

    private Workbook workbook;

    public AbstractExcel(Workbook wb) {
        this.workbook = wb;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    /**
     * create sheet
     * @param name sheet name
     * @return
     */
    public abstract Sheet createSheet(String name);

    public abstract Row createRow(int rowNum);

    public abstract Cell createCell(Row row, int columnNum);

    public void flush(String path) throws IOException {
       this.flush(new File(path));
    }

    public void flush(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        this.workbook.write(fos);
    }


    public void flush(OutputStream outputStream) throws IOException {
        this.workbook.write(outputStream);
    }
}
