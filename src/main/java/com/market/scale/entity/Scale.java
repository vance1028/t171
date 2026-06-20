package com.market.scale.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 计量器具（电子秤）台账。属于强制检定计量器具，按周期检定。
 */
public class Scale {
    private Long id;
    private Long stallId;          // 关联摊位
    private String assetNo;        // 器具编号/铅封号
    private String model;          // 型号规格
    private String manufacturer;   // 生产厂家
    private Integer maxCapacityG;  // 最大称量，单位克
    private LocalDate verifiedAt;  // 上次检定日期
    private Integer verifyCycleDays; // 检定有效周期（天）
    private String status;         // in_use / sealed / scrapped
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
