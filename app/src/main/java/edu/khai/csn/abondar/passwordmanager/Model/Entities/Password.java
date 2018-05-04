package edu.khai.csn.abondar.passwordmanager.Model.Entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Alexey Bondar on 16-Apr-18.
 */

public class Password implements Serializable{
    private String password;
    private String serviceName;
    private String userName;
    private String additionalInformation;
    private int id;
    private Date creationDate;
    private Date modifyingDate;

    public  Password(){}
    public Password(String password, String serviceName, String userName, String additionalInformation, int id, Date creationDate, Date modifyingDate) {
        this.password = password;
        this.serviceName = serviceName;
        this.userName = userName;
        this.additionalInformation = additionalInformation;
        this.id = id;
        this.creationDate = creationDate;
        this.modifyingDate = modifyingDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModifyingDate() {
        return modifyingDate;
    }

    public void setModifyingDate(Date modifyingDate) {
        this.modifyingDate = modifyingDate;
    }
}
