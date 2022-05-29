package org.px.excel.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.px.excel.HSSFWorkbookExcel;
import org.px.excel.annotation.Column;
import org.px.excel.annotation.Dynamic;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @author lgy
 * @date 2022-05-27 16:35
 */
public class PxDynamicHeadExcel<T> extends HSSFWorkbookExcel {

    private Map<String, Method> getMethodMap;

    private List<Field> attrList;

    private List<Field> dynamicList;

    private HeadTree root;

    private Class<T> clazz;

    public PxDynamicHeadExcel(Class clazz) {
        this.getMethodMap = new HashMap<>();
        this.attrList = new ArrayList<>();
        this.dynamicList = new ArrayList<>();
        this.root = new HeadTree(null, null);
        this.clazz = clazz;
    }

    public void init() throws IntrospectionException {
        this.createWb();
        this.resolve(clazz);
    }

    private void resolve(Class clazz) throws IntrospectionException {

        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);

        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            String name = pd.getName();
            Method readMethod = pd.getReadMethod();
            getMethodMap.put(name, readMethod);
        }

        Field[] fields = clazz.getDeclaredFields();

        List<Field> fieldList = new ArrayList<>();
        List<Field> dynamicFieldList = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getAnnotation(Column.class) != null) {
                fieldList.add(fields[i]);
            } else if (fields[i].getAnnotation(Dynamic.class) != null) {
                dynamicFieldList.add(fields[i]);
            }
        }

        fieldList.sort(Comparator.comparing(o -> o.getAnnotation(Column.class).sort()));
        this.attrList.addAll(fieldList);

        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            Column column = field.getAnnotation(Column.class);

            String[] columnNames = column.columnName().split(",");
            HeadTree p = this.root;
            for (int k = 0; k < columnNames.length; k++) {
                String code = columnNames[k];
                HeadTree exists = this.root.findNode(code);
                if (Objects.nonNull(exists)) {
                    p = exists;
                    continue;
                }

                HeadTree node = new HeadTree(code, code);
                node.setField(field);
                p.addNode(node);
                p = node;
            }
        }

        this.dynamicList.addAll(dynamicFieldList);
        this.dynamicList.sort(Comparator.comparing(o -> o.getAnnotation(Dynamic.class).sort()));
    }

    public void addDynamicHead(String parentCode, String node, String name) {
        HeadTree n = new HeadTree(node, name);
        this.addDynamicHead(parentCode, n);
    }

    public void addDynamicHead(String parentCode, HeadTree node) {
        if (Objects.isNull(parentCode) || "".equals(parentCode)) {
            this.root.addNode(node);
            return;
        }

        HeadTree parent = this.root.findNode(parentCode);
        if (Objects.isNull(parent)) {
            this.root.addNode(node);
            return;
        }

        parent.addNode(node);
    }

    public void flush(OutputStream outputStream) throws IOException {
        this.wb.write(outputStream);
    }

    public void export(String sheetName, List<T> data) throws InvocationTargetException, IllegalAccessException, IntrospectionException {

        int data_rows = this.MAX_ROWS - this.root.getMaxHigh();

        int loopCount = data.size() / data_rows + (data.size() % data_rows) > 0 ? 1 : 0;

        for (int l = 0; l < loopCount; l++) {
            if (l > 0) {
                sheetName = sheetName + "_" + l;
            }

            Sheet sheet = this.createSheet(sheetName);

            Queue<HeadTree> queue = new ArrayDeque<>();
            queue.offer(this.root);

            int rowNum = 0;

            // write sheet head
            while (!queue.isEmpty()) {

                int columnNum = 0;
                Row row = sheet.createRow(rowNum);

                List<HeadTree> nodes = new ArrayList<>(queue.size());
                while (!queue.isEmpty()) {
                    HeadTree node = queue.poll();

                    if (Objects.nonNull(node.getChild())) {
                        nodes.addAll(node.getChild());
                    }

                    if (Objects.isNull(node.getCode()) || "".equals(node.getCode())) {
                        rowNum--;
                        continue;
                    }

                    for (int i = 0; i < node.getMaxWidth(); i++) {
                        Cell cell = row.createCell(columnNum);
                        cell.setCellValue(node.getName());
                        columnNum++;
                    }

                    if (node.getMaxWidth() > 1) {
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, columnNum - node.getMaxWidth(), columnNum - 1));
                    }


                }

                rowNum++;

                for (HeadTree node : nodes) {
                    queue.offer(node);
                }
            }

            // write data
            for (int i = 0; i < data.size(); i++) {
                Object o = data.get(i);

                Row row = sheet.createRow(rowNum);
                int columnNum = 0;
                for (Field field : attrList) {
                    String attr = field.getName();
                    Method getMethod = getMethodMap.get(attr);
                    if (Objects.isNull(getMethod)) {
                        continue;
                    }

                    Object value = getMethod.invoke(o);
                    Cell cell = row.createCell(columnNum++);
                    cell.setCellValue(value.toString());
                }

                // resolve dynamic columns
                for (Field field : dynamicList) {
                    String attr = field.getName();
                    Method getMethod = getMethodMap.get(attr);
                    if (Objects.isNull(getMethod)) {
                        continue;
                    }

                    Object value = getMethod.invoke(o);
                    if (Objects.isNull(value)) {
                        continue;
                    }

                    if (!(value instanceof List)) {
                        continue;
                    }

                    List tempList = (List) value;
                    Map<String, Method> tempGetMethods = new HashMap<>();
                    Object t = tempList.get(0);

                    Field[] tempFields = t.getClass().getDeclaredFields();
                    List<Field> fieldList = new ArrayList<>(tempFields.length);
                    fieldList.addAll(Arrays.asList(tempFields));

                    fieldList = fieldList.stream().filter(f -> Objects.nonNull(f.getAnnotation(Column.class)))
                            .sorted(Comparator.comparing(o2 -> Integer.valueOf(o2.getAnnotation(Column.class).sort())))
                            .collect(Collectors.toList());

                    BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass());
                    PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
                    for (int h = 0; h < pds.length; h++) {
                        PropertyDescriptor pd = pds[h];
                        tempGetMethods.put(pd.getName(), pd.getReadMethod());
                    }

                    for (Object o1 : tempList) {

                        for (Field tf : fieldList) {
                            Column column = tf.getAnnotation(Column.class);
                            if (Objects.isNull(column)) {
                                continue;
                            }

                            String attrText = column.attr();
                            Method tempGetMethod = tempGetMethods.get(tf.getName());
                            if (Objects.isNull(tempGetMethod)) {
                                continue;
                            }

                            Object o2 = tempGetMethod.invoke(o1);

                            Cell cell = row.createCell(columnNum++);
                            cell.setCellValue(o2.toString());
                        }
                    }
                }

                rowNum++;
            }

        }

    }
}
