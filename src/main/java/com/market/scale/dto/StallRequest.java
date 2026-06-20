package com.market.scale.dto;

import jakarta.validation.constraints.NotBlank;

public class StallRequest {
    @NotBlank(message = "摊位编号不能为空")
    private String stallNo;
    @NotBlank(message = "所属市场不能为空")
    private String marketName;
    @NotBlank(message = "经营户姓名不能为空")
    private String merchantName;
    private String category;
    private String contactPhone;
    private String status;

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
}
