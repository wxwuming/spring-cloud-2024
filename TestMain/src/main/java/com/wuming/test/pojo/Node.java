package com.wuming.test.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: wuming
 * @DateTime: 2024/3/18 16:12
 **/
@Getter
@Setter
public class Node {
    private int value;
    private Node node;
    private Node left;
    private Node right;

    public Node(int value) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return this.value + "";
    }
}
