package com.market.scale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class ScaleRequest {
    @NotNull(message = "关联摊位不能为空")
    private Long stallId;
    @NotBlank(message = "器具编号不能为空")
    private String assetNo;
    private String model;
    private String manufacturer;
    @Positive(message = "最大称量必须为正数")
    private Integer maxCapacityG;
    private LocalDate verifiedAt;
    @Positive(message = "检定周期必须为正数")
    private Integer verifyCycleDays;
    private String status;

    public Long getStallId() { return stallId; }
    public void setStallId(Long stallId) { this.stallId = stallId; }

    public String getAssetNo() { return assetNo; }
    public void setAssetNo(String assetNo) { this.assetNo = assetNo; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public Integer getMaxCapacityG() { return maxCapacityG; }
    public void setMaxCapacityG(Integer maxCapacityG) { this.maxCapacityG = maxCapacityG; }

    public LocalDate getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDate verifiedAt) { this.verifiedAt = verifiedAt; }

    public Integer getVerifyCycleDays() { return verifyCycleDays; }
    public void setVerifyCycleDays(Integer verifyCycleDays) { this.verifyCycleDays = verifyCycleDays; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
