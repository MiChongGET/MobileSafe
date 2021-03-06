package cn.buildworld.com.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * 作者：MiChong on 2017/4/27 0027 15:37
 * 邮箱：1564666023@qq.com
 */
public class AppInfo {
    private Drawable icon;
    private String name;
    private String packname;
    private boolean inRom;
    private boolean userApp;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }


    @Override
    public String toString() {
        return "AppInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", packname='" + packname + '\'' +
                ", inRom=" + inRom +
                ", userApp=" + userApp +
                '}';
    }
}
