package com.letv.mocker.ui.vo;

import java.util.List;

public class DatagridMsg {
    private int total;
    private List<?> rows;

    public DatagridMsg() {
        super();
        // TODO Auto-generated constructor stub
    }

    public DatagridMsg(int total, List<?> rows) {
        super();
        this.total = total;
        this.rows = rows;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<?> getRows() {
        return this.rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
