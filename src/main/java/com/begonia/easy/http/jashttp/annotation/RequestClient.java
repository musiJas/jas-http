package com.begonia.easy.http.jashttp.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestClient {
    String  name() default "";
    String  url() default "";
}
