package com.xinyirun.scm.core.system.serviceimpl.business.todo;

import com.xinyirun.scm.bean.system.vo.business.todo.BAlreadyDoVo;
import com.xinyirun.scm.bean.system.vo.business.todo.BTodoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.service.business.todo.IBAlreadyDoService;
import com.xinyirun.scm.core.system.service.business.todo.IBTodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 待办事项服务类
 * </p>
 *
 * @author wwl
 * @since 2021-11-20
 */
@Component
public class TodoService {

    @Autowired
    private IBTodoService ibTodoService;

    @Autowired
    private IBAlreadyDoService ibAlreadyDoService;

    public void insertTodo(Integer serial_id, String serial_type, String perms) {

        BTodoVo bTodoVo = new BTodoVo();
        bTodoVo.setSerial_id(serial_id);
        bTodoVo.setSerial_type(serial_type);
        bTodoVo.setPerms(perms);
        ibTodoService.insertTodo(bTodoVo);

    }

    public void insertAlreadyDo(Integer serial_id, String serial_type, String perms) {

        // 待办状态修改
        BTodoVo bTodoVoCondition = new BTodoVo();
        bTodoVoCondition.setSerial_id(serial_id);
        bTodoVoCondition.setSerial_type(serial_type);
        bTodoVoCondition.setStatus(DictConstant.DICT_B_TODO_STATUS_TODO);
        List<BTodoVo> todoVoList = ibTodoService.selectList(bTodoVoCondition);
        for (BTodoVo v : todoVoList) {
            v.setStatus(DictConstant.DICT_B_TODO_STATUS_ALREADY);
            ibTodoService.save(v);
        }

        BAlreadyDoVo alreadyDoVo = new BAlreadyDoVo();
        alreadyDoVo.setPerms(perms);

        alreadyDoVo.setSerial_type(serial_type);
        alreadyDoVo.setSerial_id(serial_id);

        ibAlreadyDoService.insertAlreadyDo(alreadyDoVo);
    }

    /**
     * 根据 详情 serial_ids 和 serial_type 删除数据
     * @param serial_ids serial_id 集合
     * @param serial_type 类型
     */
    public void deleteByIdsAndSerialType(List<Integer> serial_ids, String serial_type) {
        ibTodoService.deleteByIdsAndSerialType(serial_ids, serial_type);
    }
}
