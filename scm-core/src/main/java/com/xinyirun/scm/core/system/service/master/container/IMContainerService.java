package com.xinyirun.scm.core.system.service.master.container;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.container.MContainerEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.excel.container.MContainerExcelVo;
import com.xinyirun.scm.bean.system.vo.master.container.MContainerVo;

import java.util.List;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/5/30 16:09
 */


public interface IMContainerService extends IService<MContainerEntity> {

    /**
     * 查询列表下拉框
     * @return
     */
    List<MContainerVo> selectList();

    /**
     * 分页查询
     * @param vo
     * @return
     */
    IPage<MContainerVo> selectPageList(MContainerVo vo);


    /**
     * 新增
     * @param vo
     * @return
     */
    InsertResultAo<MContainerVo> insert(MContainerVo vo);

    /**
     * 更新
     * @param vo
     * @return
     */
    UpdateResultAo<MContainerVo> save(MContainerVo vo);

    /**
     * 删除
     * @param vo
     */
    void delete(MContainerVo vo);

    /**
     * 导出查询
     * @param vo
     * @return
     */
    List<MContainerExcelVo> selectExportList(MContainerVo vo);
}
