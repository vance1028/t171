package com.market.scale.service;

import com.market.scale.common.ApiException;
import com.market.scale.entity.Scale;
import com.market.scale.entity.ScaleStatusLog;
import com.market.scale.mapper.ScaleMapper;
import com.market.scale.mapper.ScaleStatusLogMapper;
import com.market.scale.statemachine.ScaleStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScaleStatusService {

    private final ScaleMapper scaleMapper;
    private final ScaleStatusLogMapper statusLogMapper;

    public ScaleStatusService(ScaleMapper scaleMapper, ScaleStatusLogMapper statusLogMapper) {
        this.scaleMapper = scaleMapper;
        this.statusLogMapper = statusLogMapper;
    }

    @Transactional
    public Scale transitStatus(Long scaleId, String targetStatus, String reason, String operatedBy) {
        Scale scale = scaleMapper.findById(scaleId);
        if (scale == null) {
            throw ApiException.notFound("计量器具不存在");
        }
        String fromStatus = scale.getStatus();
        ScaleStatus.checkTransition(fromStatus, targetStatus);

        scaleMapper.updateStatus(scaleId, targetStatus);

        ScaleStatusLog log = new ScaleStatusLog();
        log.setScaleId(scaleId);
        log.setFromStatus(fromStatus);
        log.setToStatus(targetStatus);
        log.setReason(reason);
        log.setOperatedBy(operatedBy);
        statusLogMapper.insert(log);

        return scaleMapper.findById(scaleId);
    }

    @Transactional
    public Scale suspendOverdueScale(Long scaleId, String operatedBy) {
        Scale scale = scaleMapper.findById(scaleId);
        if (scale == null) {
            throw ApiException.notFound("计量器具不存在");
        }
        if (ScaleStatus.isSuspended(scale.getStatus())) {
            return scale;
        }
        return transitStatus(scaleId, ScaleStatus.SUSPENDED.getCode(),
                "检定超期，自动停用", operatedBy);
    }

    @Transactional
    public Scale suspendFailedScale(Long scaleId, String operatedBy) {
        return transitStatus(scaleId, ScaleStatus.SUSPENDED.getCode(),
                "检定不合格，强制停用", operatedBy);
    }

    @Transactional
    public Scale toPendingVerify(Long scaleId, String reason, String operatedBy) {
        return transitStatus(scaleId, ScaleStatus.PENDING_VERIFY.getCode(), reason, operatedBy);
    }

    @Transactional
    public Scale toVerifying(Long scaleId, String operatedBy) {
        return transitStatus(scaleId, ScaleStatus.VERIFYING.getCode(),
                "开始检定", operatedBy);
    }

    @Transactional
    public Scale toVerifiedPass(Long scaleId, String operatedBy) {
        return transitStatus(scaleId, ScaleStatus.VERIFIED_PASS.getCode(),
                "检定合格", operatedBy);
    }

    @Transactional
    public Scale toVerifiedFail(Long scaleId, String operatedBy) {
        return transitStatus(scaleId, ScaleStatus.VERIFIED_FAIL.getCode(),
                "检定不合格", operatedBy);
    }

    @Transactional
    public Scale toInUse(Long scaleId, String operatedBy) {
        return transitStatus(scaleId, ScaleStatus.IN_USE.getCode(),
                "检定合格，恢复在用", operatedBy);
    }

    public void ensureUsable(Long scaleId) {
        Scale scale = scaleMapper.findById(scaleId);
        if (scale == null) {
            throw ApiException.notFound("计量器具不存在");
        }
        if (!ScaleStatus.isUsable(scale.getStatus())) {
            throw ApiException.badRequest("器具[" + scale.getAssetNo() + "]已停用，不允许参与称重业务");
        }
        if (isExpired(scale)) {
            throw ApiException.badRequest("器具[" + scale.getAssetNo() + "]已超过检定有效期，不允许参与称重业务");
        }
    }

    public boolean isExpired(Scale scale) {
        if (scale.getNextVerifyDate() == null) {
            if (scale.getVerifiedAt() == null || scale.getVerifyCycleDays() == null) {
                return true;
            }
            return scale.getVerifiedAt().plusDays(scale.getVerifyCycleDays()).isBefore(java.time.LocalDate.now());
        }
        return scale.getNextVerifyDate().isBefore(java.time.LocalDate.now());
    }

    public long daysUntilNextVerify(Scale scale) {
        java.time.LocalDate due = scale.getNextVerifyDate();
        if (due == null) {
            if (scale.getVerifiedAt() == null || scale.getVerifyCycleDays() == null) {
                return Long.MIN_VALUE;
            }
            due = scale.getVerifiedAt().plusDays(scale.getVerifyCycleDays());
        }
        return java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), due);
    }

    public Map<String, Object> getStatusLogs(Long scaleId, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 200);
        List<ScaleStatusLog> rows = statusLogMapper.pageByScaleId(scaleId, (p - 1) * s, s);
        long total = statusLogMapper.countByScaleId(scaleId);
        Map<String, Object> result = new HashMap<>();
        result.put("items", rows);
        result.put("total", total);
        result.put("page", p);
        result.put("size", s);
        return result;
    }
}
