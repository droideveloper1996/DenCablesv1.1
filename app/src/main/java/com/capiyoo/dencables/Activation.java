package com.capiyoo.dencables;

public class Activation {
    Activation(){}

    private String activationKey;
    private boolean isActivated;
    private String reedemedBy;

    public String activationTime;



    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public String getReedemedBy() {
        return reedemedBy;
    }

    public void setReedemedBy(String reedemedBy) {
        this.reedemedBy = reedemedBy;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public String getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(String activationTime) {
        this.activationTime = activationTime;
    }


}
