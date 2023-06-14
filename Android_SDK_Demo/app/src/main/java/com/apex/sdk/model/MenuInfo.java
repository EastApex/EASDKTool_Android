package com.apex.sdk.model;

import java.io.Serializable;

public class MenuInfo implements Serializable, Comparable<MenuInfo> {
    private int type;//页面类型,分组类型,页面类型为1,分组类型为2;
    private String menuTitle;
    private int order;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(MenuInfo o) {
        if (o != null) {
            if (o.getOrder() > this.getOrder()) {
                return -1;
            } else if (o.getOrder() == this.getOrder()) {
                return 0;
            } else {
                return 1;
            }
        }
        return 0;
    }
}

