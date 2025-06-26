package com.xinyirun.scm.core.system.serviceimpl.sys.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigOriginalEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigOriginalVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.sys.table.STableColumnConfigOriginalMapper;
import com.xinyirun.scm.core.system.service.sys.table.ISTableColumnConfigOriginalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
@Service
public class STableColumnConfigOriginalServiceImpl extends ServiceImpl<STableColumnConfigOriginalMapper, STableColumnConfigOriginalEntity> implements ISTableColumnConfigOriginalService {

    @Autowired
    private STableColumnConfigOriginalMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<STableColumnConfigOriginalVo> list(STableColumnConfigOriginalVo vo) {
        vo.setStaff_id(SecurityUtil.getStaff_id().intValue());
        return mapper.originallist(vo);
    }

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    @Override
    public IPage<STableColumnConfigOriginalVo> selectPageList(STableColumnConfigOriginalVo param) {
        // 封装分页参数
        Page page = new Page(param.getPageCondition().getCurrent(),  param.getPageCondition().getSize());
        String page_code = param.getPage_code();
        // 封装模糊查询参数和排序参数
        LambdaQueryWrapper<STableColumnConfigOriginalEntity> wrapper = new LambdaQueryWrapper<STableColumnConfigOriginalEntity>()
                .like(StringUtils.isNotEmpty(page_code), STableColumnConfigOriginalEntity::getPage_code, page_code)
                .orderByDesc(STableColumnConfigOriginalEntity::getPage_code)
                .orderByAsc(STableColumnConfigOriginalEntity::getSort);
        return mapper.selectPage(page, wrapper);
    }


    /**
     * 新增
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(STableColumnConfigOriginalVo param) {
        // 先删除所有的列表
        mapper.delete(new LambdaQueryWrapper<STableColumnConfigOriginalEntity>()
                .eq(STableColumnConfigOriginalEntity :: getPage_code, param.getPage_code()));
        // 新增
        List<STableColumnConfigOriginalEntity> itemList = param.getItemList();
        // 排序， 添加值
        itemList.stream().forEach(item -> {
            item.setPage_code(param.getPage_code());
            item.setTable_code(param.getPage_code());
            item.setIs_delete(false);
        });
        this.saveBatch(itemList);
    }

    /**
     * 排序
     *
     * @param param
     */
    @Override
    public void sort(STableColumnConfigOriginalVo param) {
        LambdaQueryWrapper<STableColumnConfigOriginalEntity> queryWrapper = new LambdaQueryWrapper<STableColumnConfigOriginalEntity>()
                .eq(STableColumnConfigOriginalEntity::getPage_code, param.getPage_code())
                .orderByAsc(STableColumnConfigOriginalEntity :: getSort);
        // 根据 page_Code 查询所有
        List<STableColumnConfigOriginalEntity> entityList = mapper.selectList(queryWrapper);
        List<Integer> collect = entityList.stream().map(STableColumnConfigOriginalEntity::getId).collect(Collectors.toList());
        // 获取当前元素下标
//        int source_index = Collections.binarySearch(collect, param.getId());
        int source_index = collect.indexOf(param.getId());
        // 获取目标元素下标
        int target_index = 0;
        if ("up".equals(param.getSort_type())) {
            // 升序
          if (source_index != 0) {
              target_index = source_index -1;
          }
        } else if ("down".equals(param.getSort_type())) {
            if (source_index != entityList.size() - 1) {
                target_index = source_index + 1;
            } else {
                target_index = source_index;
            }
        }
        // 根据下标获取实例
        STableColumnConfigOriginalEntity sourceEntity = entityList.get(source_index);
        STableColumnConfigOriginalEntity targetEntity = entityList.get(target_index);
        // 更新顺序
        Integer targetSort = targetEntity.getSort();
        Integer sourceSort = sourceEntity.getSort();
        sourceEntity.setSort(targetSort);
        targetEntity.setSort(sourceSort);
        mapper.updateById(sourceEntity);
        mapper.updateById(targetEntity);
    }

}
