package com.xinyirun.scm.bean.system.vo.common.component;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.tree.ITreeNode;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 树数据的基类
 *
 * @author zxh
 * @date 2019年 10月03日 11:55:24
 */
@Data
// @ApiModel(value = "树数据的基类", description = "树数据的基类")
@EqualsAndHashCode(callSuper=false)
public class TreeNode extends BaseVo implements ITreeNode<TreeNode>, Comparable<TreeNode> , Serializable {

    private static final long serialVersionUID = 320909448609241562L;

    /** 级联value */
    private Long id;
    /** 级联label */
    private String label;
    /** 结点名称 */
    private String name;
    private Long parent_id;
    /** 父结点 */
//    private TreeNode parent;
    /** 子结点 */
    private List<TreeNode> children;
    private int level;
    /** 是否叶子结点（无子结点） */
    private boolean leaf = true;
    /** 序号 */
    private int sort;
    /**
     * 编号，00010001..
     */
    private String code;


    public TreeNode() {}

    /**
     * 构建时无限层递归构建父子结点
     * 
     * @param entity
     */
//    public TreeNode(ITreeNode entity) {
//        init(entity, Integer.MAX_VALUE);
//    }

    /**
     * 构建时按指定层数递归构建父子结点
     * 
     * @param entity
     */
//    public TreeNode(ITreeNode entity, int maxLevel) {
//        init(entity, maxLevel);
//    }

//    private void init(ITreeNode node, int maxLevel) {
//        this.id = node.getId();
//        this.name = node.getName();
//        this.sort = node.getSort();
//        generateParent(this, node, maxLevel, 1);
//        generateChildren(this, node, maxLevel, 1);
//    }

//    /**
//     * 递归父结点
//     *
//     * @param node
//     * @param entity
//     */
//    private void generateParent(TreeNode node, ITreeNode entity, int maxLevel, int currentLevel) {
//        if (currentLevel > maxLevel) {
//            return;
//        }
//        if (entity.getParent() != null) {
//            TreeNode parentNode = new TreeNode();
//            parentNode.id = entity.getParent().getId();
//            parentNode.name = entity.getParent().getName();
//            parentNode.sort = entity.getParent().getSort();
//            node.setParent(parentNode);
//            generateParent(parentNode, entity.getParent(), maxLevel, ++currentLevel);
//        }
//    }

    /**
     * 递归子结点
     * 
     * @param node
     * @param entity
     */
    private void generateChildren(TreeNode node, ITreeNode entity, int maxLevel, int currentLevel) {
        if (entity.getChildren() != null && entity.getChildren().size() > 0) {
            node.setLeaf(false);
            if (currentLevel > maxLevel) {
                return;
            }
            node.children = new ArrayList<>();
            for (ITreeNode childEntity : (Collection<ITreeNode>)entity.getChildren()) {
                TreeNode childNode = new TreeNode();
                childNode.id = childEntity.getId();
                childNode.name = childEntity.getName();
                childNode.sort = childEntity.getSort();
                node.children.add(childNode);
                Collections.sort(node.children);
                generateChildren(childNode, childEntity, maxLevel, ++currentLevel);
            }
        }
    }

    @Override
    public int compareTo(TreeNode o) {
        return sort - o.sort;
    }
}
