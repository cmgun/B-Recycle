package com.brecycle.service;

import com.brecycle.entity.dto.BatteryInfoParam;
import com.brecycle.entity.dto.BatterySafeCheckParam;
import com.brecycle.entity.dto.BatteryTransferParam;
import com.brecycle.entity.dto.TraceInfoDTO;

import java.util.List;

public interface BatteryService {

    /**
     * 新增单个电池
     * @param param
     */
    void add(BatteryInfoParam param) throws Exception;

    /**
     * 安全审查
     * @param param
     * @throws Exception
     */
    void safeCheck(BatterySafeCheckParam param);

    /**
     * 电池流转
     * @param param
     */
    void transfer(BatteryTransferParam param);

    /**
     * 获取电池溯源信息
     * @param batteryId
     * @return
     */
    List<TraceInfoDTO> getTraceInfo(String batteryId, String currentUserName) throws Exception;
}
