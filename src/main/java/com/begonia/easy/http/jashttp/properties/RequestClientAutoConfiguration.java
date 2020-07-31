package com.begonia.easy.http.jashttp.properties;

import feign.Client;
import feign.http2client.Http2Client;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(RequestClientConfigProperties.class)
public class RequestClientAutoConfiguration {

    private  RequestClientConfigProperties   requestClientConfigProperties;

    public RequestClientAutoConfiguration(RequestClientConfigProperties requestClientConfigProperties) {
        this.requestClientConfigProperties = requestClientConfigProperties;
    }


    @Bean
    public ConnectionPool  initialConnectionPool(){
        return  new ConnectionPool(requestClientConfigProperties.getMaxIdleConnections(),requestClientConfigProperties.getKeepAliveDuration(), TimeUnit.MINUTES);
    }

    @Bean(value = "client")
    @ConditionalOnExpression("'okhttp3'.equals('${feign.httpclient:okhttp3}')")
    public Client okHttpClient(ConnectionPool connectionPool){
        OkHttpClient delegate = new OkHttpClient().newBuilder()
                .connectionPool(connectionPool)
                .connectTimeout(requestClientConfigProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(requestClientConfigProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(requestClientConfigProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .build();
        return new feign.okhttp.OkHttpClient(delegate) ;
    }

    @Bean(value = "client")
    @ConditionalOnExpression("'http2Client'.equals('${feign.httpclient:okhttp3}')")
    public Client client(){
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofMillis(requestClientConfigProperties.getConnectTimeout()))
                .build();
        return new Http2Client(httpClient) ;
    }
}
