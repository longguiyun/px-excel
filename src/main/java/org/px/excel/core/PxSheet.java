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

/**
 * <p>description</p>
 *
 * @author px
 * @version 1.0.0
 */
public abstract class PxSheet implements PxRow {

    private Sheet sheet;

    @Override
    public Row createRow(int rowNum) {
        return this.sheet.createRow(rowNum);
    }

    @Override
    public Cell createCell(Row row, int column) {
        return row.createCell(column);
    }

    public void mergeColumn(int startRow, int endRow, int startColumn, int columnEnd){

    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }
}
