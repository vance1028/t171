package com.market.scale.entity;

import java.time.LocalDateTime;

/**
 * 公平秤复称记录：消费者在市场公平秤复核购买商品的实际重量，
 * 与摊位计价重量比对，判定是否缺斤短两。
 */
public class RecheckRecord {
    private Long id;
    private Long stallId;          // 被复称的摊位
    private String commodity;      // 商品名称
    private Integer claimedWeightG; // 摊位计价重量（克）
    private Integer actualWeightG;  // 公平秤实测重量（克）
    private Integer shortageG;      // 短缺量（克），由计价-实测得出
    private String result;          // pass / shortage
    private String handledBy;       // 受理人/巡查员
    private String remark;
    private LocalDateTime recheckedAt;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStallId() { return stallId; }
    public void setStallId(Long stallId) { this.stallId = stallId; }

    public String getCommodity() { return commodity; }
    public void setCommodity(String commodity) { this.commodity = commodity; }

    public Integer getClaimedWeightG() { return claimedWeightG; }
    public void setClaimedWeightG(Integer claimedWeightG) { this.claimedWeightG = claimedWeightG; }

    public Integer getActualWeightG() { return actualWeightG; }
    public void setActualWeightG(Integer actualWeightG) { this.actualWeightG = actualWeightG; }

    public Integer getShortageG() { return shortageG; }
    public void setShortageG(Integer shortageG) { this.shortageG = shortageG; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getHandledBy() { return handledBy; }
    public void setHandledBy(String handledBy) { this.handledBy = handledBy; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getRecheckedAt() { return recheckedAt; }
    public void setRecheckedAt(LocalDateTime recheckedAt) { this.recheckedAt = recheckedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
