package com.xinyirun.scm.core.system.service.business.todo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.entity.busniess.todo.BTodoEntity;
import com.xinyirun.scm.bean.system.vo.business.todo.BTodoVo;
import com.xinyirun.scm.bean.system.vo.business.todo.TodoCountVo;

import java.util.List;

/**
 * <p>
 * 待办事项服务类
 * </p>
 *
 * @author wwl
 * @since 2021-11-20
 */
public interface IBTodoService extends IService<BTodoEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BTodoVo> selectPage(BTodoVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    List<BTodoVo> selectList(BTodoVo searchCondition) ;

    /**
     * 根据类圆形查询待办id
     */
    List<Integer> selectTodoIdList(String serial_type, Long[] position_id) ;

    /**
     * 获取列表，页面查询
     */
    BTodoEntity selectById(int id) ;

    /**
     * 插入一条记录
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BTodoVo vo);

    /**
     * 修改一条记录
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> save(BTodoVo vo);


    /**
     * 查询待办条数
     */
    TodoCountVo selectTodoCount(String serial_type);

    /**
     * 生成待办事项
     * @param vo 待办事项
     */
    public void insertTodo(BTodoVo vo);

    /**
     * 根据 详情 serial_ids 和 serial_type 删除数据
     * @param serial_ids serial_id 集合
     * @param serial_type 类型
     */
    void deleteByIdsAndSerialType(List<Integer> serial_ids, String serial_type);
}
