package com.begonia.easy.http.jashttp.context;

import com.begonia.easy.http.jashttp.annotation.RequestClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Set;

public class RequestClientScanner extends ClassPathBeanDefinitionScanner {
    public RequestClientScanner(BeanDefinitionRegistry registry) {
        super(registry);
        registerFilter();
    }

    public void  registerFilter(){

        addIncludeFilter(new TypeFilter() {
            @Override
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                return true;
            }
        });
        addExcludeFilter(new TypeFilter() {
            @Override
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                String className=metadataReader.getClassMetadata().getClassName();
                return className.endsWith("package-info");
            }
        });
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> holders=super.doScan(basePackages);
        if(holders.isEmpty()){
            logger.warn("BeanDefinitionHolder is  empty....");
        }
        GenericBeanDefinition gbf;
        for(BeanDefinitionHolder  holder: holders){
            gbf= (GenericBeanDefinition) holder.getBeanDefinition();
            MergedAnnotation<RequestClient> easyClients=((ScannedGenericBeanDefinition)gbf).getMetadata().getAnnotations().get(RequestClient.class);
            MergedAnnotation<RequestMapping> requestMapping=((ScannedGenericBeanDefinition)gbf).getMetadata().getAnnotations().get(RequestMapping.class);
            String beanClassName=gbf.getBeanClassName();
            gbf.setBeanClass(RequestClientBeanFactory.class);
            gbf.getPropertyValues().add("proxyInterface",beanClassName);
            gbf.getPropertyValues().add("url",buildUrl(easyClients,requestMapping));
        }
        return holders;
    }


    private String buildUrl(MergedAnnotation<RequestClient> feignPlus, MergedAnnotation<RequestMapping> requestMapping) {
        String url = feignPlus.getString("url");
        if (requestMapping.isPresent()) {
            String[] value = (String[]) requestMapping.getValue("value").get();
            url += value[0];
        }
        return url;
    }
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface();
    }
}
