package org.px.excel.core;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * @author lgy
 * @date 2022-05-26 10:01
 */
@Data
public class HeadTree {

    private boolean root;
    private String code;

    private String name;

    private int maxHigh;

    private int maxWidth;

    private HeadTree parent;

    private Field field;

    private List<HeadTree> child;

    public HeadTree(String code, String name) {
        this.root = false;
        this.code = code;
        this.name = name;
        this.maxHigh = 1;
        this.maxWidth = 1;
        this.parent = null;
        this.child = null;
    }

    /**
     * calculate node max high, sum of all child node size
     */
    private void resizeWidth() {

        Queue<HeadTree> queue = new ArrayDeque<>();
        queue.add(this.parent);
        while (!queue.isEmpty()) {
            HeadTree node = queue.poll();

            List<HeadTree> child = node.getChild();
            if (Objects.isNull(child)) {
                continue;
            }

            int sumWidth = 0;
            for (HeadTree tree : child) {
                sumWidth += tree.getMaxWidth();
            }

            node.maxWidth = sumWidth;

            if (Objects.nonNull(node.getParent())) {
                queue.add(node.getParent());
            }
        }
    }

    /**
     * calculate node max high
     */
    private void resizeHigh() {
        Queue<HeadTree> queue = new ArrayDeque<>();
        queue.add(this);
        while (!queue.isEmpty()) {
            HeadTree node = queue.poll();
            HeadTree parent = node.getParent();
            if (Objects.isNull(parent)) {
                continue;
            }

            if (node.maxHigh >= parent.maxHigh) {
                parent.maxHigh = node.maxHigh + 1;
            }

            queue.add(parent);
        }
    }

    public void addNode(HeadTree node) {
        if (Objects.isNull(node)) {
            throw new NullPointerException("can not allowed null");
        }

        if (Objects.isNull(node.getCode()) || "".equals(node.getCode())) {
            throw new RuntimeException("please set node code");
        }

        if (Objects.nonNull(this.getCode()) && this.code.equals(node.getCode())) {
            throw new RuntimeException("can not allowed same code in different node");
        }
        node.setParent(this);

        List<HeadTree> child = this.getChild();
        if (Objects.isNull(child)) {
            child = new ArrayList<>();
        }
        child.add(node);
        this.child = child;

        node.resizeHigh();
        node.resizeWidth();
    }

    public HeadTree findNode(String code) {
        if (Objects.isNull(code) || "".equals(code.trim())) {
            return null;
        }

        Queue<HeadTree> queue = new ArrayDeque<>();
        queue.offer(this);

        while (!queue.isEmpty()) {

            List<HeadTree> nodes = new ArrayList<>(queue.size());
            while (!queue.isEmpty()) {
                HeadTree node = queue.poll();

                if (Objects.nonNull(node.getCode()) && node.getCode().equals(code)) {
                    return node;
                }

                if (Objects.nonNull(node.getChild())) {
                    nodes.addAll(node.getChild());
                }
            }

            for (HeadTree node : nodes) {
                queue.offer(node);
            }
        }

        return null;
    }

    public List<HeadTree> leafNode(){

        List<HeadTree> headTrees = new ArrayList<>(this.maxWidth);

        Queue<HeadTree> queue = new ArrayDeque<>();
        queue.offer(this);

        while (!queue.isEmpty()) {

            List<HeadTree> nodes = new ArrayList<>(queue.size());
            while (!queue.isEmpty()) {
                HeadTree node = queue.poll();

                if (Objects.nonNull(node.getChild())) {
                    nodes.addAll(node.getChild());
                } else {
                    headTrees.add(node);
                }
            }

            for (HeadTree node : nodes) {
                queue.offer(node);
            }
        }

        return headTrees;
    }
}
