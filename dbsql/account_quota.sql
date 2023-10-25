/*
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for client
-- ----------------------------
CREATE TABLE if not exists `client`
(
    `client_id`   int                                                           NOT NULL AUTO_INCREMENT,
    `username`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
    `create_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status`      int                                                           NOT NULL COMMENT '账户状态 0 停用 1 正常',
    `type`        int                                                           NOT NULL COMMENT '用户类型 0 用户 1 业务',
    PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quota
-- ----------------------------
CREATE TABLE if not exists `quota`
(
    `quota_id`      int            NOT NULL AUTO_INCREMENT,
    `type`          int            NOT NULL COMMENT '用户额度账户类型 0 额度账户0 1 额度账户1 ......',
    `update_time`   datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time`   datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `client_id`     int            NOT NULL COMMENT '用户id',
    `status`        int            NOT NULL COMMENT '账户状态 0 停用 1 正常',
    `account_quota` decimal(20, 6) NOT NULL COMMENT '账户额度',
    `current_quota` decimal(20, 6) NOT NULL COMMENT '剩余额度',
    PRIMARY KEY (`quota_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户额度账户记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quota_detail_log
-- ----------------------------
CREATE TABLE if not exists `quota_detail_log`
(
    `detail_id`   int            NOT NULL AUTO_INCREMENT,
    `create_time` datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `quota_id`    int            NOT NULL COMMENT '额度账户id',
    `quota_value` decimal(20, 6) NOT NULL COMMENT '额度流水值',
    PRIMARY KEY (`detail_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '额度流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quota_operate_log
-- ----------------------------
CREATE TABLE if not exists `quota_operate_log`
(
    `operate_id`  int            NOT NULL AUTO_INCREMENT,
    `create_time` datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `type`        int            NOT NULL COMMENT '操作类型 0 减少额度 1 增加额度',
    `quota_id`    int            NOT NULL COMMENT '额度账户id',
    `quota_value` decimal(20, 6) NOT NULL COMMENT '操作额度值',
    PRIMARY KEY (`operate_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '额度操作表' ROW_FORMAT = Dynamic;

SET
FOREIGN_KEY_CHECKS = 1;
