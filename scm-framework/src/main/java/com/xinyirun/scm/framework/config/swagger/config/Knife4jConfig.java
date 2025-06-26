//package com.xinyirun.scm.framework.config.swagger.config;
//
//import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
//import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.oas.annotations.EnableOpenApi;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//@Configuration
////@EnableSwagger2       //开启 Swagger2
//@EnableOpenApi     //开启 Swagger3 ，可不写
//@EnableKnife4j     //开启 knife4j ，可不写
//public class Knife4jConfig {
//
//    private final OpenApiExtensionResolver openApiExtensionResolver;
//
//    /**
//     * 通过该扩展给增强模式插件赋值，如自定义文档等
//     *
//     * @param openApiExtensionResolver Knife4j 扩展类
//     */
//    @Autowired
//    public Knife4jConfig(OpenApiExtensionResolver openApiExtensionResolver) {
//
//        this.openApiExtensionResolver = openApiExtensionResolver;
//    }
//
//    @Bean(value = "1、业务中台对接----1.0")
//    public Docket createWmsApi() {
//        // Swagger 2 使用的是：DocumentationType.SWAGGER_2
//        // Swagger 3 使用的是：DocumentationType.OAS_30
//        return new Docket(DocumentationType.OAS_30)
//                // 定义是否开启swagger，false为关闭，可以通过变量控制
//                .enable(true)
//                // 将api的元信息设置为包含在json ResourceListing响应中。
//                .apiInfo(new ApiInfoBuilder()
//                        .title("Knife4j接口文档")
//                        // 描述
//                        .description("平台服务管理api")
//                        .contact(new Contact("鑫一润", "no address", "no mail"))
//                                .version("1.0.0")
//                                .build())
//                                // 分组名称
//                                .groupName("1、业务中台对接----1.0")
//                                // 选择哪些接口作为swagger的doc发布
//                                .select()
//                                // 要扫描的API(Controller)基础包
//                                .apis(RequestHandlerSelectors.basePackage("com.xinyirun.scm.api"))
//                                //                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                                .paths(PathSelectors.any())
//                                .build();
//    }
//
//    @Bean(value = "2、移动端app对接----1.0")
//    public Docket createWmsAppApi() {
//        // Swagger 2 使用的是：DocumentationType.SWAGGER_2
//        // Swagger 3 使用的是：DocumentationType.OAS_30
//        return new Docket(DocumentationType.OAS_30)
//                // 定义是否开启swagger，false为关闭，可以通过变量控制
//                .enable(true)
//                // 将api的元信息设置为包含在json ResourceListing响应中。
//                .apiInfo(new ApiInfoBuilder()
//                        .title("Knife4j接口文档")
//                        // 描述
//                        .description("平台服务管理api")
//                        .contact(new Contact("鑫一润", "no address", "no mail"))
//                        .version("1.0.0")
//                        .build())
//                // 分组名称
//                .groupName("2、移动端app对接----1.0")
//                // 选择哪些接口作为swagger的doc发布
//                .select()
//                // 要扫描的API(Controller)基础包
//                .apis(RequestHandlerSelectors.basePackage("com.xinyirun.scm.app"))
//                //                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .paths(PathSelectors.any())
//                .build()
//                // 构建扩展插件-自定义文档 group
//                .extensions(openApiExtensionResolver.buildExtensions("文件上传说明"));
//    }
//}