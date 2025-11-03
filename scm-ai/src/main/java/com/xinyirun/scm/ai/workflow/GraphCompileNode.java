package com.xinyirun.scm.ai.workflow;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图编译节点(并行分支节点)
 *
 * @author zxh
 * @since 2025-10-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GraphCompileNode extends CompileNode {
    /**
     * 根节点
     */
    private CompileNode root;

    /**
     * 尾节点
     */
    private CompileNode tail;

    /**
     * 添加到叶子节点
     *
     * @param node 节点
     */
    public void appendToLeaf(CompileNode node) {
        if (tail == null) {
            root.getNextNodes().add(node);
            tail = node;
        } else {
            tail.getNextNodes().add(node);
            tail = node;
        }
    }
}
