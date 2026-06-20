package com.market.scale.service;

import com.market.scale.common.ApiException;
import com.market.scale.dto.ScaleRequest;
import com.market.scale.entity.Scale;
import com.market.scale.mapper.ScaleMapper;
import com.market.scale.mapper.StallMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScaleService {

    private final ScaleMapper scaleMapper;
    private final StallMapper stallMapper;

    public ScaleService(ScaleMapper scaleMapper, StallMapper stallMapper) {
        this.scaleMapper = scaleMapper;
        this.stallMapper = stallMapper;
    }

    public Map<String, Object> page(int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 200);
        List<Scale> rows = scaleMapper.findPage((p - 1) * s, s);
        long total = scaleMapper.count();
        Map<String, Object> result = new HashMap<>();
        result.put("items", rows);
        result.put("total", total);
        result.put("page", p);
        result.put("size", s);
        return result;
    }

    public Scale get(Long id) {
        Scale scale = scaleMapper.findById(id);
        if (scale == null) {
            throw ApiException.notFound("计量器具不存在");
        }
        return scale;
    }

    public Scale create(ScaleRequest req) {
        if (stallMapper.findById(req.getStallId()) == null) {
            throw ApiException.badRequest("关联摊位不存在");
        }
        if (scaleMapper.findByAssetNo(req.getAssetNo()) != null) {
            throw ApiException.conflict("器具编号已存在");
        }
        Scale scale = new Scale();
        scale.setStallId(req.getStallId());
        scale.setAssetNo(req.getAssetNo());
        scale.setModel(req.getModel());
        scale.setManufacturer(req.getManufacturer());
        scale.setMaxCapacityG(req.getMaxCapacityG());
        scale.setVerifiedAt(req.getVerifiedAt());
        scale.setVerifyCycleDays(req.getVerifyCycleDays() == null ? 365 : req.getVerifyCycleDays());
        scale.setStatus(req.getStatus() == null ? "in_use" : req.getStatus());
        scaleMapper.insert(scale);
        return scale;
    }

    public Scale update(Long id, ScaleRequest req) {
        Scale scale = get(id);
        scale.setModel(req.getModel());
        scale.setManufacturer(req.getManufacturer());
        scale.setMaxCapacityG(req.getMaxCapacityG());
        scale.setVerifiedAt(req.getVerifiedAt());
        if (req.getVerifyCycleDays() != null) {
            scale.setVerifyCycleDays(req.getVerifyCycleDays());
        }
        if (req.getStatus() != null) {
            scale.setStatus(req.getStatus());
        }
        scaleMapper.update(scale);
        return scaleMapper.findById(id);
    }

    public void delete(Long id) {
        get(id);
        scaleMapper.delete(id);
    }

    /**
     * 判断器具检定是否已过期：上次检定日期 + 周期 < 今天。
     */
    public boolean isExpired(Scale scale) {
        if (scale.getVerifiedAt() == null || scale.getVerifyCycleDays() == null) {
            return true;
        }
        LocalDate due = scale.getVerifiedAt().plusDays(scale.getVerifyCycleDays());
        return due.isBefore(LocalDate.now());
    }
}
