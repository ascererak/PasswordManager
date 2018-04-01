package edu.khai.csn.abondar.passwordmanager;

public class Service {
    private String serviceName;
    private String loginName;
    private int iconId;

    public Service(String serviceName,String loginName, int iconId){
        this.setServiceName(serviceName);
        this.setLoginName(loginName);
        this.setIconId(iconId);
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setServiceName(String name) {
        this.serviceName = name;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}