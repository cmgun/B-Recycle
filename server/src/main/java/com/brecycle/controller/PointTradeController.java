package com.brecycle.controller;

import com.alibaba.fastjson.JSON;
import com.brecycle.common.Response;
import com.brecycle.service.PointService;
import com.webank.weevent.client.SendResult;
import com.webank.weevent.client.WeEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cmgun
 */
@Slf4j
@Api(tags = "积分模块")
@RestController
@RequestMapping("point")
public class PointTradeController {

    @Autowired
    PointService pointService;

    @ApiOperation("初始化积分合约")
    @PostMapping("/init/deploy")
    Response deployContract() throws Exception {
        String addr = pointService.deployContract();
        log.info("初始化积分合约，发送结果:{}", addr);
        return Response.success("处理成功", addr);
    }

    @ApiOperation("初始化dao")
    @PostMapping("/init/dao")
    Response addDao() throws Exception {
        pointService.addDAO();
        return Response.success("处理成功");
    }

    @ApiOperation("初始化积分")
    @PostMapping("/init/total")
    Response initTotalPoint() throws Exception {
        pointService.initPoint();
        return Response.success("处理成功");
    }
}
