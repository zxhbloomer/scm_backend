package com.xinyirun.scm.ai.kbase;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.ai.kbase.request.KbaseRequest;
import com.xinyirun.scm.ai.kbase.response.KbaseResponse;
import com.xinyirun.scm.common.bpm.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/kbase")
public class KbaseController {

    @Autowired
    private KbaseRestService kbaseService;

    /**
     * 创建知识库
     */
    @PostMapping
    public ResponseEntity<Result<KbaseResponse>> create(@RequestBody KbaseRequest request) {
        try {
            KbaseResponse response = kbaseService.create(request);
            return ResponseEntity.ok(Result.<KbaseResponse>OK("创建成功", response));
        } catch (Exception e) {
            log.error("创建知识库失败", e);
            Result<KbaseResponse> result = new Result<>();
            result.setSuccess(false);
            result.setMessage("创建失败: " + e.getMessage());
            result.setCode(500);
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 更新知识库
     */
    @PutMapping("/{uid}")
    public ResponseEntity<Result<KbaseResponse>> update(@PathVariable String uid, 
                                                          @RequestBody KbaseRequest request) {
        try {
            request.setUid(uid);
            KbaseResponse response = kbaseService.update(request);
            return ResponseEntity.ok(Result.<KbaseResponse>OK("更新成功", response));
        } catch (Exception e) {
            log.error("更新知识库失败: {}", uid, e);
            Result<KbaseResponse> result = new Result<>();
            result.setSuccess(false);
            result.setMessage("更新失败: " + e.getMessage());
            result.setCode(500);
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 根据UID查询知识库
     */
    @GetMapping("/{uid}")
    public ResponseEntity<Result<KbaseResponse>> findByUid(@PathVariable String uid) {
        try {
            KbaseResponse response = kbaseService.findByUid(uid);
            if (response == null) {
                Result<KbaseResponse> result = new Result<>();
                result.setSuccess(false);
                result.setMessage("知识库不存在");
                result.setCode(404);
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.ok(Result.<KbaseResponse>OK(response));
        } catch (Exception e) {
            log.error("查询知识库失败: {}", uid, e);
            Result<KbaseResponse> result = new Result<>();
            result.setSuccess(false);
            result.setMessage("查询失败: " + e.getMessage());
            result.setCode(500);
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 分页查询知识库
     */
    @GetMapping
    public ResponseEntity<Result<IPage<KbaseResponse>>> findByPage(KbaseRequest request) {
        try {
            IPage<KbaseResponse> page = kbaseService.findByPage(request);
            return ResponseEntity.ok(Result.<IPage<KbaseResponse>>OK(page));
        } catch (Exception e) {
            log.error("分页查询知识库失败", e);
            Result<IPage<KbaseResponse>> result = new Result<>();
            result.setSuccess(false);
            result.setMessage("查询失败: " + e.getMessage());
            result.setCode(500);
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 根据类型查询知识库
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Result<List<KbaseResponse>>> findByType(@PathVariable String type) {
        try {
            List<KbaseResponse> responses = kbaseService.findByType(type);
            return ResponseEntity.ok(Result.<List<KbaseResponse>>OK(responses));
        } catch (Exception e) {
            log.error("根据类型查询知识库失败: {}", type, e);
            Result<List<KbaseResponse>> result = new Result<>();
            result.setSuccess(false);
            result.setMessage("查询失败: " + e.getMessage());
            result.setCode(500);
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 删除知识库
     */
    @DeleteMapping("/{uid}")
    public ResponseEntity<Result<Void>> deleteByUid(@PathVariable String uid) {
        try {
            kbaseService.deleteByUid(uid);
            Result<Void> result = Result.<Void>OK();
            result.setMessage("删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("删除知识库失败: {}", uid, e);
            Result<Void> result = new Result<>();
            result.setSuccess(false);
            result.setMessage("删除失败: " + e.getMessage());
            result.setCode(500);
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Result<KbaseResponse>> getStatistics() {
        try {
            KbaseResponse response = kbaseService.getStatistics();
            return ResponseEntity.ok(Result.<KbaseResponse>OK(response));
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            Result<KbaseResponse> result = new Result<>();
            result.setSuccess(false);
            result.setMessage("获取统计信息失败: " + e.getMessage());
            result.setCode(500);
            return ResponseEntity.ok(result);
        }
    }
}