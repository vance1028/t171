package com.market.scale.entity;

import java.time.LocalDateTime;

public class VerificationPlanItem {
    private Long id;
    private Long planId;
    private Long scaleId;
    private String assetNo;
    private String result;
    private String failReason;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public Long getScaleId() { return scaleId; }
    public void setScaleId(Long scaleId) { this.scaleId = scaleId; }

    public String getAssetNo() { return assetNo; }
    public void setAssetNo(String assetNo) { this.assetNo = assetNo; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
