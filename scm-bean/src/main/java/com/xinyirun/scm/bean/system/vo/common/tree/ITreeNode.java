package com.xinyirun.scm.bean.system.vo.common.tree;

import java.util.List;

/**
 * 树结点接口
 *
 * @author zxh
 * @date 2019年 10月04日 10:16:58
 */
public interface ITreeNode<T extends ITreeNode> {

    /**
     * 获取结点ID
     *
     * @return
     */
    Long getId();

    /**
     * 获取结点名称
     *
     * @return
     */
    String getName();

//    /**
//     * 获取父结点
//     *
//     * @return
//     */
//    T getParent();
//    void setParent(T parent);

    /**
     * 获取子结点
     *
     * @return
     */
    List<T> getChildren();
    void setChildren(List<T> children);

    /**
     * 获取排序序号
     * @return
     */
    int getSort();

    /**
     * 获取深度
     * @return
     */
    int getLevel();

    /**
     * 获取父id
     * @return
     */
    Long getParent_id();

    void setLeaf(boolean leaf);
}
