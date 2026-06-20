package com.market.scale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RecheckRequest {
    @NotNull(message = "摊位不能为空")
    private Long stallId;
    @NotBlank(message = "商品名称不能为空")
    private String commodity;
    @NotNull(message = "计价重量不能为空")
    @Positive(message = "计价重量必须为正数")
    private Integer claimedWeightG;
    @NotNull(message = "实测重量不能为空")
    @Positive(message = "实测重量必须为正数")
    private Integer actualWeightG;
    private String handledBy;
    private String remark;

    public Long getStallId() { return stallId; }
    public void setStallId(Long stallId) { this.stallId = stallId; }

    public String getCommodity() { return commodity; }
    public void setCommodity(String commodity) { this.commodity = commodity; }

    public Integer getClaimedWeightG() { return claimedWeightG; }
    public void setClaimedWeightG(Integer claimedWeightG) { this.claimedWeightG = claimedWeightG; }

    public Integer getActualWeightG() { return actualWeightG; }
    public void setActualWeightG(Integer actualWeightG) { this.actualWeightG = actualWeightG; }

    public String getHandledBy() { return handledBy; }
    public void setHandledBy(String handledBy) { this.handledBy = handledBy; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
