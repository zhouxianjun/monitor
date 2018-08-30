package com.all580.monitor.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/11 10:00
 */
@Data
@Builder
@Accessors(chain = true)
@ApiModel("返回结果")
public class Result<E> implements Serializable {
    private static final long serialVersionUID = 816334571679000487L;
    /**当前操作是否成功*/
    @ApiModelProperty("是否成功")
    private boolean success;
    /**返回文本信息*/
    @ApiModelProperty("描述信息")
    private String msg;
    @ApiModelProperty("操作代码")
    private int code;
    /**附加信息*/
    @ApiModelProperty("附加信息")
    private Map<String, Object> data;
    @ApiModelProperty("返回值")
    private E value;

    public final static int SUCCESS = 200;
    public final static int FAIL = 0;
    public final static int PARAM_FAIL = 400;
    public final static int NO_LOGIN = 99;

    public boolean isSuccess() {
        return code == SUCCESS;
    }

    private void setSuccess(boolean success) {}

    public Result<E> put(String key, Object value){
        getData().put(key, value);
        return this;
    }

    public Result<E> putAll(Map<String, Object> data){
        getData().putAll(data);
        return this;
    }

    public Result<E> value(E value) {
        this.value = value;
        return this;
    }

    public Map<String, Object> getData() {
        if (data == null) {
            data = new HashMap<>();
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    public <T> T get(String key){
        return (T) getData().get(key);
    }

    public static Result ok() {
        return Result.builder().code(SUCCESS).build();
    }

    public static Result ok(String msg) {
        return Result.builder().code(SUCCESS).msg(msg).build();
    }

    public static Result fail() {
        return Result.builder().code(FAIL).msg("操作失败").build();
    }
    public static Result fail(int code) {
        return Result.builder().code(code).msg("操作失败").build();
    }
    public static Result fail(String msg) {
        return Result.builder().code(FAIL).msg(msg).build();
    }
    public static Result fail(int code, String msg) {
        return Result.builder().code(code).msg(msg).build();
    }
}
