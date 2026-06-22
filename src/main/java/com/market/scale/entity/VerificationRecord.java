package com.market.scale.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VerificationRecord {
    private Long id;
    private Long scaleId;
    private Long planId;
    private String verifyOrg;
    private String verifyPerson;
    private String conclusion;
    private String certNo;
    private String sealNo;
    private LocalDate verifiedAt;
    private LocalDate nextVerifyDate;
    private LocalDate validUntil;
    private Integer limitedUseDays;
    private String remark;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getScaleId() { return scaleId; }
    public void setScaleId(Long scaleId) { this.scaleId = scaleId; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getVerifyOrg() { return verifyOrg; }
    public void setVerifyOrg(String verifyOrg) { this.verifyOrg = verifyOrg; }

    public String getVerifyPerson() { return verifyPerson; }
    public void setVerifyPerson(String verifyPerson) { this.verifyPerson = verifyPerson; }

    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public String getCertNo() { return certNo; }
    public void setCertNo(String certNo) { this.certNo = certNo; }

    public String getSealNo() { return sealNo; }
    public void setSealNo(String sealNo) { this.sealNo = sealNo; }

    public LocalDate getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDate verifiedAt) { this.verifiedAt = verifiedAt; }

    public LocalDate getNextVerifyDate() { return nextVerifyDate; }
    public void setNextVerifyDate(LocalDate nextVerifyDate) { this.nextVerifyDate = nextVerifyDate; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public Integer getLimitedUseDays() { return limitedUseDays; }
    public void setLimitedUseDays(Integer limitedUseDays) { this.limitedUseDays = limitedUseDays; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
