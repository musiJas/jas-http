package com.begonia.easy.http.jashttp.context;

import com.begonia.easy.http.jashttp.properties.RequestClientConfigProperties;
import feign.Client;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RequestClientBeanFactory<T> implements FactoryBean<T>, ApplicationContextAware {
    private ApplicationContext ctx;
    private  String  url;
    private  Class<T>  proxyInterface;


    @Override
    public T getObject() throws Exception {
        RequestClientConfigProperties fac=ctx.getBean(RequestClientConfigProperties.class);
        Client client;
        try {
            ctx.getBean("client",Client.class);
        }catch (NoSuchBeanDefinitionException no){
            throw new NullPointerException("No suchBeanDefinaition....  ");
        }
        T target= Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .retryer(new Retryer.Default(100, SECONDS.toMillis(1), 0))
                .options(new Request.Options(fac.getConnectTimeout(),fac.getReadTimeout(),true))
                .target(proxyInterface,url);
        return target;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Class<?> getObjectType() {
        return proxyInterface;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx=applicationContext;
    }

    public ApplicationContext getCtx() {
        return ctx;
    }

    public void setCtx(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Class<T> getProxyInterface() {
        return proxyInterface;
    }

    public void setProxyInterface(Class<T> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }


}
