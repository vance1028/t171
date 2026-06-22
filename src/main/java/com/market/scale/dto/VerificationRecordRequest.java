package com.market.scale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class VerificationRecordRequest {
    @NotNull(message = "器具ID不能为空")
    private Long scaleId;
    private Long planId;
    private String verifyOrg;
    private String verifyPerson;
    @NotBlank(message = "检定结论不能为空")
    private String conclusion;
    private String certNo;
    private String sealNo;
    @NotNull(message = "检定日期不能为空")
    private LocalDate verifiedAt;
    private Integer limitedUseDays;
    private String remark;

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

    public Integer getLimitedUseDays() { return limitedUseDays; }
    public void setLimitedUseDays(Integer limitedUseDays) { this.limitedUseDays = limitedUseDays; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
