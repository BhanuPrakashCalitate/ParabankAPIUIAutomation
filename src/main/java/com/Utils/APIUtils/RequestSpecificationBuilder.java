package com.Utils.APIUtils;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class RequestSpecificationBuilder {

    @Getter
    private final Map<String, Object> headers = new HashMap<>();
    @Getter
    private final Map<String, Object> queryParams = new HashMap<>();
    @Getter
    private final Map<String, Object> pathParams = new HashMap<>();

    public RequestSpecificationBuilder setHeaders(String key, String value){
        headers.put(key, value);
        return this;
    }

    public RequestSpecificationBuilder setQueryParams(String key, Object value){
        queryParams.put(key, value);
        return this;
    }

    public RequestSpecificationBuilder setPathParams(String key, Object value){
        pathParams.put(key, value);
        return this;
    }

    public boolean hasHeaders(){ return headers != null && !headers.isEmpty();}
    public boolean hasQueryParams(){return queryParams != null && !queryParams.isEmpty();}
    public boolean hasPathParams(){return pathParams != null && !pathParams.isEmpty();}

    public void reset(){
        this.headers.clear();
        this.queryParams.clear();
        this.pathParams.clear();
    }
}
