package com.xinyirun.scm.ai.kbase.article;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import com.xinyirun.scm.common.bpm.Result;
import com.xinyirun.scm.ai.kbase.article.request.ArticleRequest;
import com.xinyirun.scm.ai.kbase.article.response.ArticleResponse;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;

@Tag(name = "文章管理", description = "文章管理相关接口")
@RestController
@RequestMapping("/api/v1/article")
@Description("Article Management Controller - Knowledge base article content management APIs")
public class ArticleController {

    private final ArticleRestService articleRestService;

    public ArticleController(ArticleRestService articleRestService) {
        this.articleRestService = articleRestService;
    }

    @Operation(summary = "查询组织下的文章", description = "根据组织ID查询文章列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    public ResponseEntity<?> queryByOrg(ArticleRequest request) {

        Page<ArticleResponse> page = articleRestService.queryByOrg(request);

        return ResponseEntity.ok(Result.OK(page));
    }

    @Operation(summary = "查询用户下的文章", description = "根据用户ID查询文章列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    public ResponseEntity<?> queryByUser(ArticleRequest request) {
        
        Page<ArticleResponse> page = articleRestService.queryByUser(request);

        return ResponseEntity.ok(Result.OK(page));
    }

    @Operation(summary = "查询指定文章", description = "根据UID查询文章详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    public ResponseEntity<?> queryByUid(ArticleRequest request) {
        
        ArticleResponse article = articleRestService.queryByUid(request);

        if (article == null) {
            return ResponseEntity.ok(Result.error("not found"));
        }

        return ResponseEntity.ok(Result.OK(article));
    }

    @Operation(summary = "创建文章", description = "创建新的文章")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    public ResponseEntity<?> create(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleRestService.create(request);

        return ResponseEntity.ok(Result.OK(article));
    }

    @Operation(summary = "更新文章", description = "更新文章信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    public ResponseEntity<?> update(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleRestService.update(request);

        return ResponseEntity.ok(Result.OK(article));
    }

    @Operation(summary = "删除文章", description = "删除指定的文章")
    @ApiResponse(responseCode = "200", description = "删除成功")
    public ResponseEntity<?> delete(@RequestBody ArticleRequest request) {

        articleRestService.delete(request);

        return ResponseEntity.ok(Result.OK("delete success", request.getUid()));
    }

    @Operation(summary = "导出文章", description = "导出文章数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    public Object export(ArticleRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            articleRestService,
            ArticleExcel.class,
            "文章",
            "Article"
        );
    }

    @Operation(summary = "更新文章索引", description = "更新文章的Elasticsearch索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody ArticleRequest request) {

        articleRestService.updateIndex(request);

        return ResponseEntity.ok(Result.OK("update index success", request.getUid()));
    }

    @Operation(summary = "更新文章向量索引", description = "更新文章的向量索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody ArticleRequest request) {

        articleRestService.updateVectorIndex(request);
        return ResponseEntity.ok(Result.OK("update vector index success", request.getUid()));
    }

    @Operation(summary = "更新所有文章索引", description = "更新所有文章的Elasticsearch索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody ArticleRequest request) {

        articleRestService.updateAllIndex(request);

        return ResponseEntity.ok(Result.OK("update all index success", request.getUid()));
    }

    @Operation(summary = "更新所有文章向量索引", description = "更新所有文章的向量索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody ArticleRequest request) {

        articleRestService.updateAllVectorIndex(request);
        return ResponseEntity.ok(Result.OK("update all vector index success", request.getUid()));
    }

    @Operation(summary = "搜索文章", description = "输入联想搜索文章")
    @ApiResponse(responseCode = "200", description = "搜索成功")
    @GetMapping("/search")
    public ResponseEntity<?> searchElastic(ArticleRequest request) {

        List<ArticleResponse> suggestList = articleRestService.searchArticle(request);

        return ResponseEntity.ok(Result.OK(suggestList));
    }

    protected <E, SVC> Object exportTemplate(
            ArticleRequest request, 
            HttpServletResponse response,
            SVC service,
            Class<E> excelClass,
            String sheetName, 
            String filePrefix) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            
            String fileName = filePrefix + "-" + System.currentTimeMillis() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            return "";

        } catch (Exception e) {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            return Result.error(e.getMessage());
        }
    }
}