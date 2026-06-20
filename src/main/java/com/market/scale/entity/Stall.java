package com.market.scale.entity;

import java.time.LocalDateTime;

/**
 * 市场摊位（含经营户基本信息）。
 */
public class Stall {
    private Long id;
    private String stallNo;        // 摊位编号，如 A-12
    private String marketName;     // 所属农贸市场
    private String merchantName;   // 经营户姓名
    private String category;       // 经营品类：蔬菜/水产/肉类/熟食 等
    private String contactPhone;
    private String status;         // active / suspended / vacated
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStallNo() { return stallNo; }
    public void setStallNo(String stallNo) { this.stallNo = stallNo; }

    public String getMarketName() { return marketName; }
    public void setMarketName(String marketName) { this.marketName = marketName; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
