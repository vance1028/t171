package com.market.scale;

import com.market.scale.common.ApiException;
import com.market.scale.dto.RecheckRequest;
import com.market.scale.entity.RecheckRecord;
import com.market.scale.entity.Stall;
import com.market.scale.mapper.RecheckRecordMapper;
import com.market.scale.mapper.ScaleMapper;
import com.market.scale.mapper.StallMapper;
import com.market.scale.service.RecheckService;
import com.market.scale.service.ScaleStatusService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecheckServiceTest {

    private final RecheckRecordMapper recheckMapper = mock(RecheckRecordMapper.class);
    private final StallMapper stallMapper = mock(StallMapper.class);
    private final ScaleMapper scaleMapper = mock(ScaleMapper.class);
    private final ScaleStatusService statusService = mock(ScaleStatusService.class);
    private final RecheckService service = new RecheckService(recheckMapper, stallMapper, scaleMapper, statusService);

    private RecheckRequest req(int claimed, int actual) {
        RecheckRequest r = new RecheckRequest();
        r.setStallId(1L);
        r.setCommodity("带鱼");
        r.setClaimedWeightG(claimed);
        r.setActualWeightG(actual);
        return r;
    }

    @Test
    void shortageComputedAndFlagged() {
        when(stallMapper.findById(1L)).thenReturn(new Stall());
        RecheckRecord rec = service.create(req(1000, 880));
        assertEquals(120, rec.getShortageG());
        assertEquals("shortage", rec.getResult());
        verify(recheckMapper).insert(any(RecheckRecord.class));
    }

    @Test
    void exactWeightPasses() {
        when(stallMapper.findById(1L)).thenReturn(new Stall());
        RecheckRecord rec = service.create(req(1000, 1000));
        assertEquals(0, rec.getShortageG());
        assertEquals("pass", rec.getResult());
    }

    @Test
    void actualOverClaimedRejected() {
        when(stallMapper.findById(1L)).thenReturn(new Stall());
        assertThrows(ApiException.class, () -> service.create(req(1000, 1100)));
    }

    @Test
    void missingStallRejected() {
        when(stallMapper.findById(1L)).thenReturn(null);
        assertThrows(ApiException.class, () -> service.create(req(1000, 900)));
    }
}
