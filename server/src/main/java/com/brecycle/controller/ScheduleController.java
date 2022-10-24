package com.brecycle.controller;

import com.alibaba.fastjson.JSON;
import com.brecycle.common.Response;
import com.brecycle.config.WeEventConfig;
import com.brecycle.entity.dto.BatteryInfoParam;
import com.brecycle.entity.dto.BatterySafeCheckParam;
import com.brecycle.entity.dto.BatteryTransferParam;
import com.brecycle.schedule.RecycleDealSchedule;
import com.brecycle.schedule.SecondUsedDealSchedule;
import com.webank.weevent.client.IWeEventClient;
import com.webank.weevent.client.SendResult;
import com.webank.weevent.client.WeEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 提供给特定系统进行topic消息发送
 *
 * @author cmgun
 */
@Slf4j
@Api(tags = "定时器手动执行模块")
@RestController
@RequestMapping("schedule")
public class ScheduleController {

    @Autowired
    RecycleDealSchedule recycleDealSchedule;
    @Autowired
    SecondUsedDealSchedule secondUsedDealSchedule;


    @ApiOperation("回收交易到期")
    @PostMapping("/recycle/deal")
    Response recycleDeal() {
        recycleDealSchedule.process();
        return Response.success("执行成功");
    }

    @ApiOperation("梯次利用交易到期")
    @PostMapping("/secondUsed/deal")
    Response secondUsedDeal() {
        secondUsedDealSchedule.process();
        return Response.success("执行成功");
    }
}
