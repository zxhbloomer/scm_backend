package com.xinyirun.scm.core.system.serviceimpl.business.monitor;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.monitor.BContainerInfoEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.system.vo.business.monitor.BContainerInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.core.system.mapper.business.monitor.BContainerInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.monitor.IBContainerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-12
 */
@Service
public class BContainerInfoServiceImpl extends ServiceImpl<BContainerInfoMapper, BContainerInfoEntity> implements IBContainerInfoService {

    @Autowired
    private BContainerInfoMapper mapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Override
    public List<BContainerInfoVo> selectContainerInfos(int serial_id, String serial_type) {
        List<BContainerInfoVo> list = mapper.selectList(serial_id, serial_type);
        List<BContainerInfoVo> result = new ArrayList<>();
        for (BContainerInfoVo vo : list) {
            if (vo.getFile_one() != null) {
                vo.setFile_oneVo(getFileInfo(vo.getFile_one()));
            } else {
                vo.setFile_oneVo(new SFileInfoVo());
            }

            if (vo.getFile_two() != null) {
                vo.setFile_twoVo(getFileInfo(vo.getFile_two()));
            } else {
                vo.setFile_twoVo(new SFileInfoVo());
            }

            if (vo.getFile_three() != null) {
                vo.setFile_threeVo(getFileInfo(vo.getFile_three()));
            } else {
                vo.setFile_threeVo(new SFileInfoVo());
            }

            if (vo.getFile_four() != null) {
                vo.setFile_fourVo(getFileInfo(vo.getFile_four()));
            } else {
                vo.setFile_fourVo(new SFileInfoVo());
            }

            result.add(vo);
        }
        return result;
    }

    @Override
    public BContainerInfoEntity selectById(Integer id) {
        return mapper.selectById(id);
    }

    /**
     * 查询附件对象
     */
    private SFileInfoVo getFileInfo(Integer id) {
        SFileEntity file = fileMapper.selectById(id);
//        SFileInfoEntity fileInfo = fileInfoMapper.selectFIdEntity(file.getId());
//        SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);

        SFileInfoVo fileInfoVo = fileInfoMapper.selectFId(file.getId());
//        SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
        fileInfoVo.setFileName(fileInfoVo.getFile_name());
        return fileInfoVo;
    }
}
