package com.xinyirun.scm.core.system.serviceimpl.sys.file;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.file.BackupFileVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SBackupLogVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileVo;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 附件信息 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class SFileServiceImpl extends BaseServiceImpl<SFileMapper, SFileEntity> implements ISFileService {

    @Autowired
    private SFileMapper mapper;

    @Autowired
    private SFileInfoMapper sFileInfoMapper;

    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    @Override
    public List<SFileVo> selectList(SFileVo searchCondition) {
        return mapper.selectList( searchCondition);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(SFileVo vo) {

        // 插入逻辑保存
        SFileEntity entity = (SFileEntity) BeanUtilsSupport.copyProperties(vo, SFileEntity.class);
        int rtn = mapper.insert(entity);
        SFileInfoEntity fileInfoEntity = (SFileInfoEntity) BeanUtilsSupport.copyProperties(vo, SFileInfoEntity.class);
        fileInfoEntity.setF_id(entity.getId());
        sFileInfoMapper.insert(fileInfoEntity);
        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    @Override
    public SFileVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 批量删除复原
     *
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DeleteResultAo<Integer> realDeleteByIdsIn(List<SFileVo> searchCondition) {
        List<Integer> idList = new ArrayList<>();
        searchCondition.forEach(bean -> {
            idList.add(bean.getId());
        });
        int result=mapper.deleteBatchIds(idList);
        return DeleteResultUtil.OK(result);
    }


    /**
     * 获取附件信息
     *
     * @param id
     */
    @Override
    public List<SFileInfoVo> selectFileInfo(Integer id) {
        if (id == null) {
            return null;
        }

        List<SFileInfoVo> sFileInfoVos = new ArrayList<>();
        SFileEntity file = mapper.selectById(id);
        List<SFileInfoEntity> fileInfos = sFileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", file.getId()));
        for (SFileInfoEntity fileInfo : fileInfos) {
            SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
            fileInfoVo.setFileName(fileInfoVo.getFile_name());
            sFileInfoVos.add(fileInfoVo);
        }
        return sFileInfoVos;
    }

    /**
     * 根据业务类型和业务ID查询文件信息
     *
     * @param serialType 业务类型（如 "ai_knowledge_base_item"）
     * @param serialId 业务ID（如 知识项ID）
     * @return 文件信息列表
     */
    @Override
    public List<SFileInfoVo> selectFileInfoBySerialTypeAndId(String serialType, Integer serialId) {
        // 1. 使用SQL方法根据serial_type和serial_id查询s_file表
        SFileVo sFileVo = mapper.selectBySerial(serialType, serialId);

        // 2. 如果没有找到s_file记录，返回空列表
        if (sFileVo == null) {
            return new ArrayList<>();
        }

        // 3. 调用现有方法查询s_file_info
        return selectFileInfo(sFileVo.getId());
    }

}
