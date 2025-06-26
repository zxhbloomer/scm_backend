package com.xinyirun.scm.core.api.serviceimpl.business.v1.borroworder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.api.vo.business.borroworder.ApiBBorrowOrderVo;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseFilesEntity;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BWkReleaseOrderDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BWkReleaseOrderEntity;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.api.mapper.business.borroworder.ApiBBorrowOrderMapper;
import com.xinyirun.scm.core.api.mapper.business.releaseorder.ApiBReleaseFilesMapper;
import com.xinyirun.scm.core.api.service.business.v1.borroworder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wang Qianfeng
 * @date 2022/11/30 16:29
 */
@Service
public class ApiIBBorrowOrderServiceImpl extends ServiceImpl<ApiBBorrowOrderMapper, BReleaseOrderEntity> implements ApiIBBorrowOrderService {

    @Autowired
    private ApiIBWkBorrowOrderService wkService;

    @Autowired
    private ApiIBWkBorrowOrderDetailService wkDetailService;

    @Autowired
    private ApiIBBorrowOrderDetailService detailService;

    @Autowired
    private ApiIBBorrowFilesService filesService;

    @Autowired
    private ApiBReleaseFilesMapper apiBReleaseFilesMapper;

    /**
     * 同步数据
     *
     * @param list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sync(List<ApiBBorrowOrderVo> list) {
        // 锁表
        wkService.lockB_wk_release_order00();
        wkDetailService.lockB_wk_release_order_detail00();

        // 清空表
        wkService.remove(new QueryWrapper<>());
        wkDetailService.remove(new QueryWrapper<>());

        List<String> codes = new ArrayList<>();

        // wk 表添加数据
        list.forEach(item -> {
            List<BWkReleaseOrderDetailEntity> detailList = BeanUtilsSupport.copyProperties(item.getDetailList(), BWkReleaseOrderDetailEntity.class);
            BWkReleaseOrderEntity bWkReleaseOrderEntity = new BWkReleaseOrderEntity();
            BeanUtilsSupport.copyProperties(item, bWkReleaseOrderEntity);
            wkService.save(bWkReleaseOrderEntity);
            detailList.forEach(item1 -> item1.setRelease_order_code(bWkReleaseOrderEntity.getCode()));

            item.getFiles().stream().forEach(k->{
                k.setRelease_order_code(bWkReleaseOrderEntity.getCode());
                k.setTimestamp(item.getC_time());
            });

            wkDetailService.saveBatch(detailList);

            List<BReleaseFilesEntity> files = BeanUtilsSupport.copyProperties(item.getFiles(), BReleaseFilesEntity.class);

            filesService.deleteByReleaseOrderCode(item.getCode());
            codes.add(item.getCode());

            filesService.saveBatch(files);

            // 删除主表release_code
//            detailService.remove(new LambdaQueryWrapper<BReleaseOrderDetailEntity>().eq(BReleaseOrderDetailEntity :: getRelease_order_code, item.getCode()));
//            baseMapper.delete(new LambdaQueryWrapper<BReleaseOrderEntity>().eq(BReleaseOrderEntity :: getCode, item.getCode()));
        });

        // 更新主表
        detailService.updateB_release_order_detail30();
        baseMapper.updateB_release_order30();

        // 新增主表
        baseMapper.insertB_release_order30();
        detailService.insertB_release_order_detail30();

        // 更新文件的release_order_id
        apiBReleaseFilesMapper.updateB_release_order_file30(codes);

    }
}
