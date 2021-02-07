package com.tommy.tabsinfo;

public class Item {

    private String tab, information;

    public Item() {
    }

    public Item(String tab, String information) {
        this.tab = tab;
        this.information = information;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
