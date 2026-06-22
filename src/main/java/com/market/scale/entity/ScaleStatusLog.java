package com.market.scale.entity;

import java.time.LocalDateTime;

public class ScaleStatusLog {
    private Long id;
    private Long scaleId;
    private String fromStatus;
    private String toStatus;
    private String reason;
    private String operatedBy;
    private LocalDateTime operatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getScaleId() { return scaleId; }
    public void setScaleId(Long scaleId) { this.scaleId = scaleId; }

    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getOperatedBy() { return operatedBy; }
    public void setOperatedBy(String operatedBy) { this.operatedBy = operatedBy; }

    public LocalDateTime getOperatedAt() { return operatedAt; }
    public void setOperatedAt(LocalDateTime operatedAt) { this.operatedAt = operatedAt; }
}
