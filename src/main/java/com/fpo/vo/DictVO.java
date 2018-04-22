package com.fpo.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/22 0022.
 */
public class DictVO implements Serializable {
    private static final long serialVersionUID = 6196454261807706090L;

    private String key;
    private String value;

    public DictVO() {
    }

    public DictVO(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
