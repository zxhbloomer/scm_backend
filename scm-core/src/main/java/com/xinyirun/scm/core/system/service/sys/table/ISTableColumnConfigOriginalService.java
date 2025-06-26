package com.xinyirun.scm.core.system.service.sys.table;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigOriginalEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigOriginalVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
public interface ISTableColumnConfigOriginalService extends IService<STableColumnConfigOriginalEntity> {

    /**
     * 查询页面表格配置信息
     * @param vo
     * @return
     */
    List<STableColumnConfigOriginalVo> list(STableColumnConfigOriginalVo vo);

    /**
     * 分页查询
     * @param param
     * @return
     */
    IPage<STableColumnConfigOriginalVo> selectPageList(STableColumnConfigOriginalVo param);

    /**
     * 新增
     * @param param
     */
    void insert(STableColumnConfigOriginalVo param);

    /**
     * 排序
     * @param param
     */
    void sort(STableColumnConfigOriginalVo param);
}
