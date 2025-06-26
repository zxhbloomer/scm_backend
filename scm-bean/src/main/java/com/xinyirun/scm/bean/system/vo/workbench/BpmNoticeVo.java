package com.xinyirun.scm.bean.system.vo.workbench;

import com.alibaba.druid.sql.dialect.blink.parser.BlinkStatementParser;
import com.xinyirun.scm.bean.system.vo.business.notice.BNoticeVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 事项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpmNoticeVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -6729826061135847896L;

    List<BNoticeVo> noticeListAll;
    List<BNoticeVo> noticeListSystem;
    List<BNoticeVo> noticeListPersonal;

    /** 用户code */
    private String staffCode;
}
