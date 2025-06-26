package com.xinyirun.scm.controller.common;

import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.web.WebUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import fr.opensagres.xdocreport.core.io.IOUtils;
import io.mola.galimatias.GalimatiasParseException;
import jodd.io.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

@Slf4j
// @Api(tags = "共通下载")
@RestController
@RequestMapping(value = "/api/v1/file")
public class FileDownLoadController extends SystemBaseController {

    @SysLogAnnotion("共通下载")
    // @ApiOperation(value = "共通下载")
    @GetMapping("/cors/download")
    @ResponseBody
    @RepeatSubmitAnnotion
    public void getCorsFile(@RequestParam String urlPath, HttpServletResponse response) {
        log.debug("下载跨域文件url：{}", urlPath);
        try {
            URL url = WebUtil.normalizedURL(urlPath);
            byte[] bytes = NetUtil.downloadBytes(url.toString());
            IOUtils.write(bytes, response.getOutputStream());
        } catch (IOException | GalimatiasParseException e) {
            log.error("下载跨域文件异常，url：{}", urlPath, e);
        }
    }

}
