package com.market.scale.service;

import com.market.scale.common.ApiException;
import com.market.scale.dto.RecheckRequest;
import com.market.scale.entity.RecheckRecord;
import com.market.scale.entity.Scale;
import com.market.scale.mapper.RecheckRecordMapper;
import com.market.scale.mapper.ScaleMapper;
import com.market.scale.mapper.StallMapper;
import com.market.scale.statemachine.ScaleStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecheckService {

    private final RecheckRecordMapper recheckMapper;
    private final StallMapper stallMapper;
    private final ScaleMapper scaleMapper;
    private final ScaleStatusService statusService;

    public RecheckService(RecheckRecordMapper recheckMapper, StallMapper stallMapper,
                          ScaleMapper scaleMapper, ScaleStatusService statusService) {
        this.recheckMapper = recheckMapper;
        this.stallMapper = stallMapper;
        this.scaleMapper = scaleMapper;
        this.statusService = statusService;
    }

    public Map<String, Object> page(Long stallId, String result, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 200);
        List<RecheckRecord> rows = recheckMapper.search(stallId, result, (p - 1) * s, s);
        long total = recheckMapper.count(stallId, result);
        Map<String, Object> res = new HashMap<>();
        res.put("items", rows);
        res.put("total", total);
        res.put("page", p);
        res.put("size", s);
        return res;
    }

    public RecheckRecord create(RecheckRequest req) {
        if (stallMapper.findById(req.getStallId()) == null) {
            throw ApiException.badRequest("摊位不存在");
        }

        List<Scale> stallScales = scaleMapper.findByStall(req.getStallId());
        for (Scale s : stallScales) {
            statusService.ensureUsable(s);
        }

        if (req.getActualWeightG() > req.getClaimedWeightG()) {
            throw ApiException.badRequest("实测重量大于计价重量，请核对录入");
        }
        int shortage = req.getClaimedWeightG() - req.getActualWeightG();
        RecheckRecord rec = new RecheckRecord();
        rec.setStallId(req.getStallId());
        rec.setCommodity(req.getCommodity());
        rec.setClaimedWeightG(req.getClaimedWeightG());
        rec.setActualWeightG(req.getActualWeightG());
        rec.setShortageG(shortage);
        rec.setResult(shortage > 0 ? "shortage" : "pass");
        rec.setHandledBy(req.getHandledBy());
        rec.setRemark(req.getRemark());
        rec.setRecheckedAt(LocalDateTime.now());
        recheckMapper.insert(rec);
        return rec;
    }
}
