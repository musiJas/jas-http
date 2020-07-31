package com.begonia.easy.http.jashttp.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableRequestClient {
    String[]  value() default {};
    String[]  basePackages() default {};
}
