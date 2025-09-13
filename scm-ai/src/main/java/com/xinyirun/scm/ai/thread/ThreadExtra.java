/**
 * 会话扩展信息类
 */
package com.xinyirun.scm.ai.thread;

import java.io.Serializable;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
// @NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ThreadExtra implements Serializable {

    private static final long serialVersionUID = 1L;
    
}