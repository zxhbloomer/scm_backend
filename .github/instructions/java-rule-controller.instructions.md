---
applyTo: '**'
---
# Controller 开发规范

## 在 Controller 开发时，默认包含以下标准 URL 和方法：
具体需要的接口会通过需求告诉你，不要擅自增加
### 第一个：新增接口
- **URL**: `@PostMapping("/insert")`
- **方法名**: `insert`
- **注解**: `@SysLogAnnotion("模块名 新增")`、`@RepeatSubmitAnnotion`
- **参数**: `@RequestBody XxxVo searchCondition`
- **返回类型**: `ResponseEntity<JsonResultAo<XxxVo>>`
- **核心逻辑**: 调用 `service.startInsert(searchCondition)`
- **成功处理**: 返回 `ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功")`
- **失败处理**: 抛出 `InsertErrorException("新增失败，请编辑后重新新增。")`

### 第二个：校验接口
- **URL**: `@PostMapping("/validate")`
- **方法名**: `checkLogic`
- **注解**: `@SysLogAnnotion("模块名校验")`、`@ResponseBody`、`@RepeatSubmitAnnotion`
- **参数**: `@RequestBody(required = false) XxxVo searchCondition`
- **返回类型**: `ResponseEntity<JsonResultAo<String>>`
- **核心逻辑**: 调用 `service.checkLogic(searchCondition, searchCondition.getCheck_type())`
- **成功处理**: 返回 `ResultUtil.OK("OK")`
- **失败处理**: 抛出 `BusinessException(checkResultAo.getMessage())`

### 第三个：分页查询接口
- **URL**: `@PostMapping("/pagelist")`
- **方法名**: `list`
- **注解**: `@SysLogAnnotion("根据查询条件，获取模块名集合信息")`
- **参数**: `@RequestBody(required = false) XxxVo searchCondition`
- **返回类型**: `ResponseEntity<JsonResultAo<IPage<XxxVo>>>`
- **核心逻辑**: 调用 `service.selectPage(searchCondition)`
- **返回**: `ResultUtil.OK(list)`

### 第四个：合计查询接口
- **URL**: `@PostMapping("/sum")`
- **方法名**: `querySum`
- **注解**: `@SysLogAnnotion("按模块名合计")`、`@ResponseBody`
- **参数**: `@RequestBody(required = false) XxxVo searchCondition`
- **返回类型**: `ResponseEntity<JsonResultAo<XxxVo>>`
- **核心逻辑**: 调用 `service.querySum(searchCondition)`
- **返回**: `ResultUtil.OK(result)`

### 第五个：单个查询接口
- **URL**: `@PostMapping("/get")`
- **方法名**: `get`
- **注解**: `@SysLogAnnotion("根据查询条件，获取模块名信息")`
- **参数**: `@RequestBody(required = false) XxxVo searchCondition`
- **返回类型**: `ResponseEntity<JsonResultAo<XxxVo>>`
- **核心逻辑**: 调用 `service.selectById(searchCondition.getId())`
- **返回**: `ResultUtil.OK(vo)`

### 第六个：更新保存接口
- **URL**: `@PostMapping("/save")`
- **方法名**: `save`
- **注解**: `@SysLogAnnotion("模块名更新保存")`、`@RepeatSubmitAnnotion`
- **参数**: `@RequestBody(required = false) XxxVo searchCondition`
- **返回类型**: `ResponseEntity<JsonResultAo<XxxVo>>`
- **核心逻辑**: 调用 `service.startUpdate(searchCondition)`
- **成功处理**: 返回 `ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功")`
- **失败处理**: 抛出 `UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。")`

### 第七个：删除接口
- **URL**: `@PostMapping("/delete")`
- **方法名**: `delete`
- **注解**: `@SysLogAnnotion("根据查询条件，模块名逻辑删除")`
- **参数**: `@RequestBody(required = false) List<XxxVo> searchCondition`
- **返回类型**: `ResponseEntity<JsonResultAo<XxxVo>>`
- **核心逻辑**: 调用 `service.delete(searchCondition)`
- **成功处理**: 返回 `ResultUtil.OK(null,"删除成功")`
- **失败处理**: 抛出 `UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。")`

### 第八个：打印接口
- **URL**: `@PostMapping("/print")`
- **方法名**: `print`
- **注解**: `@SysLogAnnotion("获取报表系统参数，并组装打印参数")`、`@ResponseBody`
- **参数**: `@RequestBody(required = false) XxxVo searchCondition`
- **返回类型**: `ResponseEntity<JsonResultAo<XxxVo>>`
- **核心逻辑**: 调用 `service.getPrintInfo(searchCondition)`
- **返回**: `ResultUtil.OK(printInfo)`

### 第九个：作废接口
- **URL**: `@PostMapping("/cancel")`
- **方法名**: `cancel`
- **注解**: `@SysLogAnnotion("模块名，作废")`、`@ResponseBody`
- **参数**: `@RequestBody(required = false) XxxVo searchCondition`
- **返回类型**: `ResponseEntity<JsonResultAo<XxxVo>>`
- **核心逻辑**: 调用 `service.cancel(searchCondition)`
- **成功处理**: 返回 `ResultUtil.OK(null,"更新成功")`
- **失败处理**: 抛出 `UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。")`

### 第十个：完成接口
- **URL**: `@PostMapping("/finish")`
- **方法名**: `finish`
- **注解**: `@SysLogAnnotion("模块名，完成")`、`@ResponseBody`
- **参数**: `@RequestBody(required = false) XxxVo searchCondition`
- **返回类型**: `ResponseEntity<JsonResultAo<XxxVo>>`
- **核心逻辑**: 调用 `service.finish(searchCondition)`
- **成功处理**: 返回 `ResultUtil.OK(null,"更新成功")`
- **失败处理**: 抛出 `UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。")`

### 第十一个：导出接口
- **URL**: `@PostMapping("/export")`
- **方法名**: `export`
- **注解**: `@SysLogAnnotion("导出")`
- **参数**: `@RequestBody(required = false) XxxVo param, HttpServletResponse response`
- **返回类型**: `void`
- **异常声明**: `throws IOException`
- **核心逻辑**: 
  1. 调用 `service.selectExportList(param)` 获取数据
  2. 数据转换为导出格式
  3. 使用 `EasyExcel` 导出到 `HttpServletResponse`
- **导出工具**: 使用 `EasyExcelUtil` 和 `CustomMergeStrategy` 处理合并单元格

## Controller 类结构规范

### 类基本结构
```java
@RestController
@RequestMapping("/api/v1/模块名")
public class XxxController {

    @Autowired
    private IXxxService service;
    
    // 方法实现...
}
```

### 通用规范
- **类注解**: `@RestController`、`@RequestMapping("/api/v1/模块名")`
- **Service 注入**: `@Autowired private IXxxService service;`
- **日志注解**: 所有方法都使用 `@SysLogAnnotion` 注解记录操作日志
- **防重复提交**: 涉及数据修改的方法使用 `@RepeatSubmitAnnotion` 防重复提交
- **返回类型**: 统一使用 `ResponseEntity<JsonResultAo<T>>` 作为返回类型（导出接口除外）
- **成功响应**: 统一使用 `ResultUtil.OK()` 构造成功响应
- **异常处理**: 统一抛出相应的业务异常类
  - `InsertErrorException`: 新增失败
  - `UpdateErrorException`: 更新失败
  - `BusinessException`: 业务逻辑失败

### 导入依赖
```java
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
```

这个规范确保了 Controller 层的标准化和一致性，遵循了项目的架构设计原则。

```java
@RestController
@RequestMapping("/api/v1/模块名")
public class XxxController {

    @Autowired
    private IXxxService service;
    
    // 方法实现...
}
```

```java
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
```
