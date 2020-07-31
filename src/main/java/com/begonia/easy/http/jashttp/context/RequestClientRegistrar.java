package com.begonia.easy.http.jashttp.context;

import com.begonia.easy.http.jashttp.annotation.RequestClient;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RequestClientRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes=AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RequestClient.class.getName()));
        List<String> packages=new ArrayList<>();
        for(String pkg:attributes.getStringArray("basePackages")){
            if(StringUtils.hasText(pkg)){
                packages.add(pkg);
            }
        }
        RequestClientScanner  scanner=new RequestClientScanner(registry);
        scanner.doScan(StringUtils.toStringArray(packages));
    }
}
