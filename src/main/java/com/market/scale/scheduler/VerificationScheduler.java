package com.market.scale.scheduler;

import com.market.scale.entity.Scale;
import com.market.scale.mapper.ScaleMapper;
import com.market.scale.service.ScaleStatusService;
import com.market.scale.statemachine.ScaleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VerificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(VerificationScheduler.class);
    private static final String SYSTEM_OPERATOR = "system";

    private final ScaleMapper scaleMapper;
    private final ScaleStatusService statusService;

    public VerificationScheduler(ScaleMapper scaleMapper, ScaleStatusService statusService) {
        this.scaleMapper = scaleMapper;
        this.statusService = statusService;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void markOverdueAsSuspended() {
        List<Scale> overdue = scaleMapper.findOverdue();
        for (Scale scale : overdue) {
            try {
                statusService.suspendOverdueScale(scale.getId(), SYSTEM_OPERATOR);
                log.info("器具[{}]检定超期，已自动停用", scale.getAssetNo());
            } catch (Exception e) {
                log.warn("自动停用器具[{}]失败: {}", scale.getAssetNo(), e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void autoPendingVerifyForExpiring() {
        int warnDays = 30;
        List<Scale> expiring = scaleMapper.findExpiringSoon(warnDays);
        for (Scale scale : expiring) {
            if (ScaleStatus.IN_USE.getCode().equals(scale.getStatus())) {
                try {
                    statusService.toPendingVerify(scale.getId(),
                            "临到期自动进入待检（距下次检定" + statusService.daysUntilNextVerify(scale) + "天）",
                            SYSTEM_OPERATOR);
                    log.info("器具[{}]临到期，已自动进入待检状态", scale.getAssetNo());
                } catch (Exception e) {
                    log.warn("器具[{}]自动待检失败: {}", scale.getAssetNo(), e.getMessage());
                }
            }
        }
    }
}
