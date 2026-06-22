package com.market.scale.dto;

import java.util.List;

public class VerificationPlanRequest {
    private String marketName;
    private String assignedOrg;
    private String assignedPerson;
    private String remark;
    private List<Long> scaleIds;

    public String getMarketName() { return marketName; }
    public void setMarketName(String marketName) { this.marketName = marketName; }

    public String getAssignedOrg() { return assignedOrg; }
    public void setAssignedOrg(String assignedOrg) { this.assignedOrg = assignedOrg; }

    public String getAssignedPerson() { return assignedPerson; }
    public void setAssignedPerson(String assignedPerson) { this.assignedPerson = assignedPerson; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public List<Long> getScaleIds() { return scaleIds; }
    public void setScaleIds(List<Long> scaleIds) { this.scaleIds = scaleIds; }
}
