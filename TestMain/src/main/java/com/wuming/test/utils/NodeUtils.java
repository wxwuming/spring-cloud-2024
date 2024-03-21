package com.wuming.test.utils;

import com.wuming.test.pojo.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @Author: wuming
 * @DateTime: 2024/3/18 16:15
 **/
public class NodeUtils {
    public static List<Node> createNode(int[] data) {
        List<Node> list = new ArrayList<>();

        // 将数组所有元素转化为节点
        for (int datum : data) {
            Node node = new Node(datum);
            list.add(node);
        }

        // 处理节点
        for (int i = 0; i < list.size() / 2 - 1; i++) {
            list.get(i).setLeft(list.get(i * 2 + 1));
            list.get(i).setRight(list.get(i * 2 + 2));
        }

        // 处理最后一个节点
        int index = list.size() / 2 - 1;
        list.get(index).setLeft(list.get(index * 2 + 1));
        // 处理最后一个节点的右节点
        if (list.size() % 2 == 1) {
            list.get(index).setRight(list.get(index * 2 + 2));
        }

        return list;
    }

    // 递归前序遍历
    public void preTraversal(Node node) {
        if (node == null) //很重要，必须加上 当遇到叶子节点用来停止向下遍历
            return;
        System.out.print(node.getValue() + " ");
        preTraversal(node.getLeft());
        preTraversal(node.getRight());
    }

    // 中序遍历
    public void MidTraversal(Node node) {
        if (node == null)
            return;
        MidTraversal(node.getLeft());
        System.out.print(node.getValue() + " ");
        MidTraversal(node.getRight());
    }

    // 后序遍历
    public void postTraversal(Node node) {
        if (node == null)
            return;
        postTraversal(node.getLeft());
        postTraversal(node.getRight());
        System.out.print(node.getValue() + " ");
    }

    // 非递归前序遍历
    public void preOrderTraversalbyLoop(Node node) {
        Stack<Node> stack = new Stack<>();
        Node p = node;
        while (p != null || !stack.isEmpty()) {
            while (p != null) {
                //当p不为空时，就读取p的值，并不断更新p为其左子节点，即不断读取左子节点
                System.out.print(p.getValue() + " ");
                stack.push(p); //将p入栈
                p = p.getLeft();
            }
            if (!stack.isEmpty()) {
                p = stack.pop();
                p = p.getRight();
            }
        }
    }

    // 非递归中序遍历
    public void inOrderTraversalbyLoop(Node node) {
        Stack<Node> stack = new Stack<>();
        Node p = node;
        while (p != null || !stack.isEmpty()) {
            while (p != null) {
                stack.push(p);
                p = p.getLeft();
            }
            if (!stack.isEmpty()) {
                p = stack.pop();
                System.out.print(p.getValue() + " ");
                p = p.getRight();
            }
        }
    }

    // 非递归后序遍历
    public void postOrderTraversalbyLoop(Node node) {
        Stack<Node> stack = new Stack<>();
        Node p = node, prev = node;
        while (p != null || !stack.isEmpty()) {
            while (p != null) {
                stack.push(p);
                p = p.getLeft();
            }
            if (!stack.isEmpty()) {
                Node temp = stack.peek().getRight();
                //只是拿出来栈顶这个值，并没有进行删除
                if (temp == null || temp == prev) {
                    //节点没有右子节点或者到达根节点【考虑到最后一种情况】
                    p = stack.pop();
                    System.out.print(p.getValue() + " ");
                    prev = p;
                    p = null;
                } else {
                    p = temp;
                }
            }
        }
    }

    // 广度优先遍历
    public void bfs(Node root) {
        if (root == null) return;
        LinkedList<Node> queue = new LinkedList<>();
        queue.offer(root); //首先将根节点存入队列
        //当队列里有值时，每次取出队首的node打印，打印之后判断node是否有子节点，若有，则将子节点加入队列
        while (queue.size() > 0) {
            Node node = queue.peek();
            queue.poll(); //取出队首元素并打印
            System.out.print(node.getValue() + " ");
            if (node.getLeft() != null) { //如果有左子节点，则将其存入队列
                queue.offer(node.getLeft());
            }
            if (node.getRight() != null) { //如果有右子节点，则将其存入队列
                queue.offer(node.getRight());
            }
        }
    }

    // 深度优先遍历
    public void dfs(Node node, List<List<Integer>> rst, List<Integer> list) {
        if (node == null) return;
        if (node.getLeft() == null && node.getRight() == null) {
            list.add(node.getValue());
            /* 这里将list存入rst中时，不能直接将list存入，而是通过新建一个list来实现，
             * 因为如果直接用list的话，后面remove的时候也会将其最后一个存的节点删掉
             * */
            rst.add(new ArrayList<>(list));
            list.remove(list.size() - 1);
        }
        list.add(node.getValue());
        dfs(node.getLeft(), rst, list);
        dfs(node.getRight(), rst, list);
        list.remove(list.size() - 1);
    }
}
