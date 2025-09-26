package com.xinyirun.scm.ai.core.mapper.chat;

import com.xinyirun.scm.ai.bean.dto.request.AiModelSourceCreateNameDTO;
import com.xinyirun.scm.ai.bean.dto.request.AiModelSourceRequest;
import com.xinyirun.scm.ai.bean.dto.sdk.OptionDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtAiModelSourceMapper {

    List<AiModelSourceCreateNameDTO> list(@Param("request") AiModelSourceRequest aiModelSourceRequest);

    List<OptionDTO> enableSourceNameList(@Param("userId") String userId);

    List<OptionDTO> enablePersonalSourceNameList(@Param("userId") String userId);

}