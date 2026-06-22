-- 农贸市场计量公平秤监管平台 数据库结构
-- 字符集统一 utf8mb4，避免中文乱码

CREATE DATABASE IF NOT EXISTS fairscale
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE fairscale;

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(64)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    display_name  VARCHAR(64)           DEFAULT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'viewer',
    enabled       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS stalls (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    stall_no      VARCHAR(32)  NOT NULL,
    market_name   VARCHAR(128) NOT NULL,
    merchant_name VARCHAR(64)  NOT NULL,
    category      VARCHAR(32)           DEFAULT NULL,
    contact_phone VARCHAR(32)           DEFAULT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'active',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_stalls_no (stall_no),
    KEY idx_stalls_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS scales (
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    stall_id          BIGINT      NOT NULL,
    asset_no          VARCHAR(48) NOT NULL,
    model             VARCHAR(64)          DEFAULT NULL,
    manufacturer      VARCHAR(128)         DEFAULT NULL,
    max_capacity_g    INT                  DEFAULT NULL,
    verified_at       DATE                 DEFAULT NULL,
    verify_cycle_days INT         NOT NULL DEFAULT 365,
    next_verify_date  DATE                 DEFAULT NULL,
    current_seal_no   VARCHAR(48)          DEFAULT NULL,
    status            VARCHAR(24) NOT NULL DEFAULT 'in_use',
    created_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_scales_asset (asset_no),
    KEY idx_scales_stall (stall_id),
    KEY idx_scales_status (status),
    KEY idx_scales_next_verify (next_verify_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS verification_plans (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    plan_no        VARCHAR(48)  NOT NULL,
    market_name    VARCHAR(128)          DEFAULT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'draft',
    total_count    INT          NOT NULL DEFAULT 0,
    assigned_org   VARCHAR(128)          DEFAULT NULL,
    assigned_person VARCHAR(64)          DEFAULT NULL,
    remark         VARCHAR(255)          DEFAULT NULL,
    created_by     VARCHAR(64)           DEFAULT NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_vplan_no (plan_no),
    KEY idx_vplan_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS verification_plan_items (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    plan_id     BIGINT       NOT NULL,
    scale_id    BIGINT       NOT NULL,
    asset_no    VARCHAR(48)  NOT NULL,
    result      VARCHAR(20)  NOT NULL DEFAULT 'pending',
    fail_reason VARCHAR(255)          DEFAULT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_vpi_plan (plan_id),
    KEY idx_vpi_scale (scale_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS verification_records (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    scale_id        BIGINT       NOT NULL,
    plan_id         BIGINT                DEFAULT NULL,
    verify_org      VARCHAR(128)          DEFAULT NULL,
    verify_person   VARCHAR(64)           DEFAULT NULL,
    conclusion      VARCHAR(20)  NOT NULL,
    cert_no         VARCHAR(64)           DEFAULT NULL,
    seal_no         VARCHAR(48)           DEFAULT NULL,
    verified_at     DATE         NOT NULL,
    next_verify_date DATE                 DEFAULT NULL,
    valid_until     DATE                  DEFAULT NULL,
    limited_use_days INT                  DEFAULT NULL,
    remark          VARCHAR(255)          DEFAULT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_vr_scale (scale_id),
    KEY idx_vr_plan (plan_id),
    KEY idx_vr_date (verified_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS scale_status_logs (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    scale_id     BIGINT       NOT NULL,
    from_status  VARCHAR(24)           DEFAULT NULL,
    to_status    VARCHAR(24)  NOT NULL,
    reason       VARCHAR(255) NOT NULL,
    operated_by  VARCHAR(64)  NOT NULL,
    operated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_ssl_scale (scale_id),
    KEY idx_ssl_time (operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS recheck_records (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    stall_id        BIGINT      NOT NULL,
    commodity       VARCHAR(64) NOT NULL,
    claimed_weight_g INT        NOT NULL,
    actual_weight_g  INT        NOT NULL,
    shortage_g       INT        NOT NULL DEFAULT 0,
    result          VARCHAR(16) NOT NULL DEFAULT 'pass',
    handled_by      VARCHAR(64)          DEFAULT NULL,
    remark          VARCHAR(255)         DEFAULT NULL,
    rechecked_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_recheck_stall (stall_id),
    KEY idx_recheck_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
