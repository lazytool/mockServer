package com.letv.mocker.ui.vo;

public class SystemContext {
    public String name;
    public String value;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "System [name=" + this.name + ", value=" + this.value + "]";
    }

}
