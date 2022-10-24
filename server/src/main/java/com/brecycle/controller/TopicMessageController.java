package com.brecycle.controller;

import com.alibaba.fastjson.JSON;
import com.brecycle.common.Response;
import com.brecycle.config.WeEventConfig;
import com.brecycle.entity.dto.BatteryInfoParam;
import com.brecycle.entity.dto.BatterySafeCheckParam;
import com.brecycle.entity.dto.BatteryTransferParam;
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

import javax.annotation.Resource;
import java.util.List;

/**
 * 提供给特定系统进行topic消息发送
 *
 * @author cmgun
 */
@Slf4j
@Api(tags = "消息发送模块")
@RestController
@RequestMapping("topic")
public class TopicMessageController {

    @Autowired
    WeEventConfig weEventConfig;
    @Autowired
    IWeEventClient client;


    @ApiOperation("批量新增电池")
    @PostMapping("/battery/add")
    Response add(@RequestBody @ApiParam(value = "参数", required = true) List<BatteryInfoParam> param) throws Exception {
        String topicName = weEventConfig.getBatteryAddTopic();
        WeEvent weEvent = new WeEvent(topicName, JSON.toJSONString(param).getBytes());
        SendResult sendResult = client.publish(weEvent);
        log.info("批量新增电池，发送结果:{}", sendResult);
        return Response.success("消息发送成功");
    }

    @ApiOperation("电池安全认证")
    @PostMapping("/battery/safeCheck")
    Response safeCheck(@RequestBody @ApiParam(value = "参数", required = true) List<BatterySafeCheckParam> param) throws Exception {
        String topicName = weEventConfig.getSafeCheckTopic();
        WeEvent weEvent = new WeEvent(topicName, JSON.toJSONString(param).getBytes());
        SendResult sendResult = client.publish(weEvent);
        log.info("电池安全认证，发送结果:{}", sendResult);
        return Response.success("消息发送成功");
    }

    @ApiOperation("电池流转-生产商")
    @PostMapping("/battery/transfer/productor")
    Response productorTransfer(@RequestBody @ApiParam(value = "参数", required = true) List<BatteryTransferParam> param) throws Exception {
        String topicName = weEventConfig.getProductorTransferTopic();
        WeEvent weEvent = new WeEvent(topicName, JSON.toJSONString(param).getBytes());
        SendResult sendResult = client.publish(weEvent);
        log.info("电池流转-生产商，发送结果:{}", sendResult);
        return Response.success("消息发送成功");
    }

    @ApiOperation("电池流转-车企")
    @PostMapping("/battery/transfer/car")
    Response carTransfer(@RequestBody @ApiParam(value = "参数", required = true) List<BatteryTransferParam> param) throws Exception {
        String topicName = weEventConfig.getCarTransferTopic();
        WeEvent weEvent = new WeEvent(topicName, JSON.toJSONString(param).getBytes());
        SendResult sendResult = client.publish(weEvent);
        log.info("电池流转-生产商，发送结果:{}", sendResult);
        return Response.success("消息发送成功");
    }

    @ApiOperation("电池流转-电池租赁商")
    @PostMapping("/battery/transfer/rent")
    Response rentTransfer(@RequestBody @ApiParam(value = "参数", required = true) List<BatteryTransferParam> param) throws Exception {
        String topicName = weEventConfig.getRentTransferTopic();
        WeEvent weEvent = new WeEvent(topicName, JSON.toJSONString(param).getBytes());
        SendResult sendResult = client.publish(weEvent);
        log.info("电池流转-电池租赁商，发送结果:{}", sendResult);
        return Response.success("消息发送成功");
    }
}
