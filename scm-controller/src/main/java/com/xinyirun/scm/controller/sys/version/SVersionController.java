package com.xinyirun.scm.controller.sys.version;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
@RestController
@RequestMapping("/api/v1")
public class SVersionController {

    @Value("${project.version}")
    private String version;

    @Value("${project.description}")
    private String description;

    @GetMapping("/get/version")
    public ResponseEntity<JsonResultAo<ProjectInfo>> get() {
        ProjectInfo info = new ProjectInfo(version, description);
        return ResponseEntity.ok().body(ResultUtil.OK(info));
    }

    @Data
    class ProjectInfo {
        private final String version;
        private final String description;

        public ProjectInfo(String version, String description) {
            this.version = version;
            this.description = description;
        }
    }
}
