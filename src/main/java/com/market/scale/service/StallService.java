package com.market.scale.service;

import com.market.scale.common.ApiException;
import com.market.scale.dto.StallRequest;
import com.market.scale.entity.Stall;
import com.market.scale.mapper.StallMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StallService {

    private final StallMapper stallMapper;

    public StallService(StallMapper stallMapper) {
        this.stallMapper = stallMapper;
    }

    public Map<String, Object> page(String keyword, String status, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 200);
        int offset = (p - 1) * s;
        List<Stall> rows = stallMapper.search(keyword, status, offset, s);
        long total = stallMapper.count(keyword, status);
        Map<String, Object> result = new HashMap<>();
        result.put("items", rows);
        result.put("total", total);
        result.put("page", p);
        result.put("size", s);
        return result;
    }

    public Stall get(Long id) {
        Stall stall = stallMapper.findById(id);
        if (stall == null) {
            throw ApiException.notFound("摊位不存在");
        }
        return stall;
    }

    public Stall create(StallRequest req) {
        if (stallMapper.findByStallNo(req.getStallNo()) != null) {
            throw ApiException.conflict("摊位编号已存在");
        }
        Stall stall = new Stall();
        stall.setStallNo(req.getStallNo());
        stall.setMarketName(req.getMarketName());
        stall.setMerchantName(req.getMerchantName());
        stall.setCategory(req.getCategory());
        stall.setContactPhone(req.getContactPhone());
        stall.setStatus(req.getStatus() == null ? "active" : req.getStatus());
        stallMapper.insert(stall);
        return stall;
    }

    public Stall update(Long id, StallRequest req) {
        Stall stall = get(id);
        stall.setMarketName(req.getMarketName());
        stall.setMerchantName(req.getMerchantName());
        stall.setCategory(req.getCategory());
        stall.setContactPhone(req.getContactPhone());
        if (req.getStatus() != null) {
            stall.setStatus(req.getStatus());
        }
        stallMapper.update(stall);
        return stallMapper.findById(id);
    }

    public void delete(Long id) {
        get(id);
        stallMapper.delete(id);
    }
}
