package com.xinyirun.scm.core.system.serviceimpl.master.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerExportVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.customer.MOwnerMapper;
import com.xinyirun.scm.core.system.service.master.customer.IMOwnerService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 */
@Service
public class IMOwnerServiceImpl extends BaseServiceImpl<MOwnerMapper, MOwnerEntity> implements IMOwnerService {

    @Autowired
    private MOwnerMapper mapper;

    @Override
    public IPage<MOwnerVo> selectPage(MOwnerVo searchCondition) {
        // 分页条件
        Page<MOwnerEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<MOwnerVo> selectList(MOwnerVo searchCondition) {
        return mapper.selectList(searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MOwnerVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(), CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MOwnerEntity entity = (MOwnerEntity) BeanUtilsSupport.copyProperties(vo, MOwnerEntity.class);

        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称简拼
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setShort_name_pinyin(str.toString());

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(MOwnerVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        MOwnerEntity entity = (MOwnerEntity) BeanUtilsSupport.copyProperties(vo, MOwnerEntity.class);

        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称简拼
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setShort_name_pinyin(str.toString());

        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public List<MOwnerEntity> selectByName(String name) {
        // 查询 数据
        return mapper.selectByName(name);
    }

    @Override
    public List<MOwnerEntity> selectByCode(String code) {
        // 查询 数据
        return mapper.selectByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MOwnerVo> searchCondition) {
        List<MOwnerEntity> list = mapper.selectIdsIn(searchCondition);
        for(MOwnerEntity entity : list) {
            entity.setEnable(Boolean.TRUE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disSabledByIdsIn(List<MOwnerVo> searchCondition) {
        List<MOwnerEntity> list = mapper.selectIdsIn(searchCondition);
        for(MOwnerEntity entity : list) {
            entity.setEnable(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MOwnerVo> searchCondition) {
        List<MOwnerEntity> list = mapper.selectIdsIn(searchCondition);
        for(MOwnerEntity entity : list) {
            entity.setEnable(!entity.getEnable());
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MOwnerVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 导出
     *
     * @param searchCondition 导出参数
     * @return List<MOwnerExportVo>
     */
    @Override
    public List<MOwnerExportVo> export(MOwnerVo searchCondition) {
        return mapper.exportList(searchCondition);
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(String name, String code, String moduleType) {
        List<MOwnerEntity> selectByName = selectByName(name);
        List<MOwnerEntity> selectByKey = selectByCode(code);

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", name);
                }
                if (selectByKey.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", code);
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", name);
                }
                if (selectByKey.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", code);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }
}
