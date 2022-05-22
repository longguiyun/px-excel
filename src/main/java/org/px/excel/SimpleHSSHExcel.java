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

package org.px.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.px.excel.annotation.Column;
import org.px.excel.core.PxHSSH;
import org.px.excel.core.PxSheet;
import org.px.excel.core.PxSheetHead;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>description</p>
 *
 * @author px
 * @version 1.0.0
 */
public class SimpleHSSHExcel<T> extends PxHSSH {

    /**
     * the sheet max rows
     */
    public int MAX_ROWS_LIMIT = 65535;

    private PxSheet pxSheet;

    private Class clazz;

    private BeanInfo beanInfo;

    private Field[] fields;

    private Set<String> fieldSet;

    private PropertyDescriptor[] propertyDescriptors;

    private Map<String,Method> readMethodMap;

    private PxSheetHead head;

    private int rowNum = 0;

    public SimpleHSSHExcel(Class clazz) {
        this.clazz = clazz;
        this.head = new PxSheetHead(true);
        this.pxSheet = new SimplePxSheet();
        this.fieldSet = new HashSet<>();
        this.readMethodMap = new HashMap<>();
        init();
    }

    public SimpleHSSHExcel(int rowsLimit, Class clazz) {
        this.MAX_ROWS_LIMIT = rowsLimit;
        this.clazz = clazz;
        this.head = new PxSheetHead(true);
        this.pxSheet = new SimplePxSheet();
        this.fieldSet = new HashSet<>();
        this.readMethodMap = new HashMap<>();
        init();
    }

    private void init() {
        getBeanInfo();
        getSheetHead();
        getFieldSet();
        fillHead();

    }

    private void getBeanInfo() {
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }

        this.propertyDescriptors = beanInfo.getPropertyDescriptors();
    }

    private void getSheetHead() {
        this.fields = clazz.getDeclaredFields();
        List<Field> fieldList = Arrays.asList(fields);
        fieldList.sort(Comparator.comparing(o -> o.getAnnotation(Column.class).sort()));

        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            String fieldName = field.getName();
            fieldSet.add(fieldName);

            Column column = field.getAnnotation(Column.class);
            String[] codes = column.name().split(",");

            String pCode = null;
            for (int j = 0; j < codes.length; j++) {
                String code = codes[j];
                if (j > 0) {
                    pCode = codes[j - 1];
                }

                PxSheetHead parent = head.findNode(code);
                if (Objects.nonNull(parent)) {
                    continue;
                }

                PxSheetHead node = new PxSheetHead(fieldName, code);
                node.setColumnMeta(column);
                this.head.addNode(pCode, node);
            }
        }
    }

    private void getFieldSet(){
        for (int i = 0; i < this.propertyDescriptors.length; i++) {
            PropertyDescriptor pd = this.propertyDescriptors[i];
            String pName = pd.getName();

            if(!fieldSet.contains(pName)) {
                continue;
            }

            Method readMethod = pd.getReadMethod();
            readMethodMap.put(pName,readMethod);
        }
    }

    private void fillHead() {
        Sheet sheet = super.createSheet("xxx");
        this.pxSheet.setSheet(sheet);

        Queue<PxSheetHead> queue = new ArrayDeque<>();
        queue.offer(head);
        while (!queue.isEmpty()) {

            List<PxSheetHead> cd = new ArrayList<>();

            int columnNum = 0;
            Row row = pxSheet.createRow(rowNum);

            while (!queue.isEmpty()) {
                PxSheetHead node = queue.poll();
                cd.addAll(node.getChild());

                // skip root node
                if (Objects.isNull(node.getParent())) {
                    rowNum--;
                    continue;
                }

                for (int i = 0; i < node.getAllLeafSize(); i++) {
                    Cell cell = pxSheet.createCell(row, columnNum++);
                    cell.setCellValue(node.getCode());
                }

                //merge
                if (node.getAllLeafSize() > 1) {
                    pxSheet.mergeColumn(rowNum, rowNum, columnNum - node.getAllLeafSize(), columnNum - 1);
                }
            }

            rowNum++;

            for (int i = 0; i < cd.size(); i++) {
                queue.offer(cd.get(i));
            }
        }

    }


    public void data(List<T> data) {
        List<PxSheetHead> leafs = head.getAllLeaf();

        for (int i = 0; i < data.size(); i++) {
            Object d = data.get(i);

            int column = 0;
            Row row = pxSheet.createRow(rowNum++);

            for (PxSheetHead leaf : leafs) {
                String propertyName = leaf.getName();
                if(!readMethodMap.containsKey(propertyName)){
                    continue;
                }

                Method readMethod = readMethodMap.get(propertyName);
                Class returnClass = readMethod.getReturnType();

                Object value = null;
                try {
                    value = readMethod.invoke(d);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                Cell cell = pxSheet.createCell(row, column++);

                if(Objects.isNull(value)){

                } else if(returnClass == String.class){
                    cell.setCellValue((String)value);
                } else if(returnClass == Integer.class || returnClass == int.class){
                    cell.setCellValue((int)value);
                } else if(returnClass == Double.class || returnClass == double.class){
                    cell.setCellValue((double)value);
                } else if(returnClass == Float.class || returnClass == float.class){
                    cell.setCellValue((float)value);
                } else if(returnClass == Short.class || returnClass == short.class){
                    cell.setCellValue((short)value);
                } else if(returnClass == Boolean.class || returnClass == boolean.class){
                    cell.setCellValue((boolean)value);
                } else if(returnClass == Date.class ){
                    cell.setCellValue((Date)value);
                } else if(returnClass == BigDecimal.class ){
                    BigDecimal bg = (BigDecimal) value;
                    cell.setCellValue(bg.toPlainString());
                } else {

                }

            }
        }
    }

    private class SimplePxSheet extends PxSheet {

        public void mergeColumn(int startRow, int endRow, int startColumn, int columnEnd) {
            getSheet().addMergedRegion(new CellRangeAddress(
                    startRow, //first row (0-based)
                    endRow, //last row  (0-based)
                    startColumn, //first column (0-based)
                    columnEnd  //last column  (0-based)
            ));
        }
    }


}
