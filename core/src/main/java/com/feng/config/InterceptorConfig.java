//package com.feng.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
///**
// * @author: ladidol
// * @date: 2022/9/27 23:13
// * @description:
// */
//@Configuration
//public class InterceptorConfig extends WebMvcConfigurationSupport {
//    private final TokenInterceptor tokenInterceptor;
//
//    public InterceptorConfig(TokenInterceptor tokenInterceptor) {
//        this.tokenInterceptor = tokenInterceptor;
//    }
//
//    @Override
//    protected void addInterceptors(InterceptorRegistry registry) {
//        //拦截所有目录，除了通向login的接口
//        registry.addInterceptor(tokenInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/login")
//                .excludePathPatterns("/register")
//                .excludePathPatterns("/codes/image/**")
//                .excludePathPatterns("/codes/email")
//                .excludePathPatterns("/reset/password")
//                .excludePathPatterns("/**/*.html", "/**/*.js", "/**/*.css")
//                .excludePathPatterns("/swagger-ui.html")
//                .excludePathPatterns("/swagger-resources/**")
//                .excludePathPatterns("/error")
//                .excludePathPatterns("/webjars/**");
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        //将templates目录下的CSS、JS文件映射为静态资源，防止Spring把这些资源识别成thymeleaf模版
//        registry.addResourceHandler("/templates/**.js").addResourceLocations("classpath:/templates/");
//        registry.addResourceHandler("/templates/**.css").addResourceLocations("classpath:/templates/");
//        //其他静态资源
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        //swagger增加url映射
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//
//}
