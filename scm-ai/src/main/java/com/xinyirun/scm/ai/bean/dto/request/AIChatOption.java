package com.xinyirun.scm.ai.bean.dto.request;

import com.xinyirun.scm.ai.bean.dto.request.AiModelSourceDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Data
public class AIChatOption implements Serializable {

    @Serial
    private static final long serialVersionUID = -9157522931636771829L;

    private String conversationId;

    private AiModelSourceDTO module;

    private String prompt;

    private String system;

    private String tenant;

    public AIChatOption withPrompt(@NotBlank String prompt) {
        this.prompt = prompt;
        return this;
    }
}