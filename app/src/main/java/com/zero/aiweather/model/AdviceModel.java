package com.zero.aiweather.model;

public class AdviceModel {
    private String title;
    private int iconId;
    private AdviceResponse.Daily adviceDaily;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public AdviceResponse.Daily getAdviceDaily() {
        return adviceDaily;
    }

    public void setAdviceDaily(AdviceResponse.Daily adviceDaily) {
        this.adviceDaily = adviceDaily;
    }
}
