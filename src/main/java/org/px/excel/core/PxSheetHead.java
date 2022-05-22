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


import java.util.*;

/**
 * <p>
 *
 * </p>
 *
 * @author px
 * @version 1.0.0
 */
public class PxSheetHead {

    private String name;

    /**
     * the tree root, it must be unique
     */
    private String code;

    private PxSheetHead parent;

    private PxSheetHead root;

    private List<PxSheetHead> child;

    /**
     * the tree maxHigh
     */
    private int maxHigh;

    /**
     * all leaf numbers
     */
    private int allLeafSize;

    private Object columnMeta;

    private List<PxSheetHead> allLeaf;

    public PxSheetHead(boolean isRoot) {
        this.name = "";
        this.code = "";
        this.parent = null;
        this.root = isRoot ? this : null;
        this.child = new ArrayList<>(0);
        this.allLeafSize = 1;
        this.maxHigh = 1;
        this.allLeaf = new ArrayList<>();
    }

    public PxSheetHead() {
        this.name = "";
        this.code = "";
        this.parent = null;
        this.root = null;
        this.child = new ArrayList<>(0);
        this.allLeafSize = 1;
        this.maxHigh = 1;
        this.allLeaf = new ArrayList<>();
    }

    public PxSheetHead(String name, String code) {
        this.name = name;
        this.code = code;
        this.parent = null;
        this.root = null;
        this.child = new ArrayList<>(0);
        this.allLeafSize = 1;
        this.maxHigh = 1;
        this.allLeaf = new ArrayList<>();
    }

    public void addNode(String parentCode, PxSheetHead node) {
        PxSheetHead parent = findNode(this.root, parentCode);

        if (Objects.isNull(parent)) {
            parent = this.root;
        }

        node.setParent(parent);
        node.setRoot(this.root);

        parent.getChild().add(node);

        node.resizeMaxHighWithAllLeaf();
    }

    private void resizeMaxHighWithAllLeaf() {

        PxSheetHead p = this.parent;

        Queue<PxSheetHead> queue = new ArrayDeque<>();
        queue.offer(p);

        while (!queue.isEmpty()) {
            PxSheetHead node = queue.poll();

            int tempMaxHigh = 0;
            int tempMaxLeafSize = 0;
            List<PxSheetHead> cd = node.getChild();
            for (int i = 0; i < cd.size(); i++) {
                PxSheetHead c = cd.get(i);
                tempMaxHigh = Math.max(tempMaxHigh, c.getMaxHigh());
                tempMaxLeafSize += c.getAllLeafSize();
            }

            node.allLeafSize = tempMaxLeafSize;
            node.maxHigh = 1 + tempMaxHigh;

            if (Objects.isNull(node.getParent())) {
                return;
            }

            queue.offer(node.getParent());
        }
    }

    public PxSheetHead findNode(PxSheetHead root, String code) {

        Queue<PxSheetHead> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {

            List<PxSheetHead> cd = new ArrayList<>();
            while (!queue.isEmpty()) {
                PxSheetHead node = queue.poll();
                if (node.getCode().equals(code)) {
                    return node;
                }

                cd.addAll(node.getChild());
            }

            for (PxSheetHead pxSheetHead : cd) {
                queue.offer(pxSheetHead);
            }
        }


        return null;
    }

    public void buildAllLeaf(){
        Queue<PxSheetHead> queue = new ArrayDeque<>();
        queue.offer(this.root);
        while (!queue.isEmpty()){

            PxSheetHead node = queue.poll();
            if(node.getChild().isEmpty()){
                this.allLeaf.add(node);
                continue;
            }

            List<PxSheetHead> cd = node.getChild();
            for (PxSheetHead pxSheetHead : cd) {
                queue.add(pxSheetHead);
            }
        }
    }

    public PxSheetHead findNode(String code) {
        return this.findNode(this,code);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PxSheetHead getParent() {
        return parent;
    }

    public void setParent(PxSheetHead parent) {
        this.parent = parent;
    }

    public PxSheetHead getRoot() {
        return root;
    }

    public void setRoot(PxSheetHead root) {
        this.root = root;
    }

    public List<PxSheetHead> getChild() {
        return child;
    }

    public void setChild(List<PxSheetHead> child) {
        this.child = child;
    }

    public int getMaxHigh() {
        return maxHigh;
    }

    public void setMaxHigh(int maxHigh) {
        this.maxHigh = maxHigh;
    }

    public int getAllLeafSize() {
        return allLeafSize;
    }

    public void setAllLeafSize(int allLeafSize) {
        this.allLeafSize = allLeafSize;
    }

    public Object getColumnMeta() {
        return columnMeta;
    }

    public void setColumnMeta(Object columnMeta) {
        this.columnMeta = columnMeta;
    }

    public List<PxSheetHead> getAllLeaf() {
        this.buildAllLeaf();
        return allLeaf;
    }

    public void setAllLeaf(List<PxSheetHead> allLeaf) {
        this.allLeaf = allLeaf;
    }
}
