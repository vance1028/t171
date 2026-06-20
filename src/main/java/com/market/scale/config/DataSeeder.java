package com.market.scale.config;

import com.market.scale.entity.Scale;
import com.market.scale.entity.Stall;
import com.market.scale.entity.User;
import com.market.scale.mapper.ScaleMapper;
import com.market.scale.mapper.StallMapper;
import com.market.scale.mapper.UserMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 首次启动时灌入默认账号与少量示例数据，幂等执行。
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private final UserMapper userMapper;
    private final StallMapper stallMapper;
    private final ScaleMapper scaleMapper;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserMapper userMapper, StallMapper stallMapper,
                      ScaleMapper scaleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.stallMapper = stallMapper;
        this.scaleMapper = scaleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        seedBusinessData();
    }

    private void seedUsers() {
        if (userMapper.findByUsername("admin") == null) {
            createUser("admin", "admin123", "系统管理员", "admin");
        }
        if (userMapper.findByUsername("inspector") == null) {
            createUser("inspector", "inspect123", "计量巡查员", "inspector");
        }
        if (userMapper.findByUsername("viewer") == null) {
            createUser("viewer", "viewer123", "只读查看员", "viewer");
        }
    }

    private void createUser(String username, String rawPwd, String displayName, String role) {
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPwd));
        u.setDisplayName(displayName);
        u.setRole(role);
        u.setEnabled(true);
        userMapper.insert(u);
    }

    private void seedBusinessData() {
        if (stallMapper.count(null, null) > 0) {
            return;
        }
        Stall s1 = newStall("A-12", "城东综合农贸市场", "王桂芳", "蔬菜", "13800010001");
        Stall s2 = newStall("B-07", "城东综合农贸市场", "李建国", "水产", "13800010002");
        Stall s3 = newStall("C-21", "滨江生鲜市场", "赵丽娟", "肉类", "13800010003");
        stallMapper.insert(s1);
        stallMapper.insert(s2);
        stallMapper.insert(s3);

        scaleMapper.insert(newScale(s1.getId(), "JL-2025-0012", "ACS-30", "上海大华衡器", 30000, LocalDate.now().minusDays(40), 365));
        scaleMapper.insert(newScale(s2.getId(), "JL-2025-0033", "TCS-15", "永康精工衡器", 15000, LocalDate.now().minusDays(400), 365));
    }

    private Stall newStall(String no, String market, String merchant, String category, String phone) {
        Stall s = new Stall();
        s.setStallNo(no);
        s.setMarketName(market);
        s.setMerchantName(merchant);
        s.setCategory(category);
        s.setContactPhone(phone);
        s.setStatus("active");
        return s;
    }

    private Scale newScale(Long stallId, String assetNo, String model, String maker, int cap, LocalDate verified, int cycle) {
        Scale sc = new Scale();
        sc.setStallId(stallId);
        sc.setAssetNo(assetNo);
        sc.setModel(model);
        sc.setManufacturer(maker);
        sc.setMaxCapacityG(cap);
        sc.setVerifiedAt(verified);
        sc.setVerifyCycleDays(cycle);
        sc.setStatus("in_use");
        return sc;
    }
}
