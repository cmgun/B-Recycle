package com.brecycle.service;

import com.brecycle.entity.Battery;
import com.brecycle.entity.EntInfo;
import com.brecycle.entity.Trade;
import com.brecycle.entity.User;
import com.brecycle.entity.dto.*;

import java.util.List;

public interface PointService {

    /**
     * 部署积分合约
     */
    String deployContract() throws Exception;

    /**
     * 添加DAO账户
     */
    void addDAO();

    /**
     * 积分总额初始化
     * DAO账户操作
     */
    void initPoint() throws Exception;

    /**
     * 积分账户注册
     * DAO账户操作
     * @param user
     */
    void registAccount(User user);

    /**
     * 企业注册积分派发
     * 由DAO分发给对应企业账户
     * @param user
     * @param entInfo
     */
    void registPointPublish(User user, EntInfo entInfo);

    /**
     * 年度结算，对回收商拆解行为进行年度积分派发
     * @param recycleEnt
     */
    void yearRecycleEntPublish(User recycleEnt);

    /**
     * 积分缴纳
     * @param payEnt 电池生产商、车企
     * @param batteryList
     */
    void payPoint(User payEnt, List<Battery> batteryList);

    /**
     * 消费者积分获取
     * DAO执行transfer
     *
     * @param customer
     * @param battery
     */
    void customerPoint(User customer, List<Battery> battery);

    /**
     * 拆解环节梯次利用企业积分获取
     * @param storedEnt
     * @param batteryList
     */
    void secondUsedPoint(User storedEnt, List<Battery> batteryList);

    /**
     * 拆解环节回收商积分获取
     * @param recycleEnt
     * @param batteryList
     */
    void secondRecyclePoint(User recycleEnt, List<Battery> batteryList);

    /**
     * 积分交易申请
     * @param param
     * @param currentUserName
     */
    void apply(PointTradeApplyParam param, String currentUserName) throws Exception;

    /**
     * 交易查询
     * @param param
     * @return
     */
    PageResult<TradeListDTO> list(TradeListParam param);

    /**
     * 竞价交易
     * @param param
     */
    void bid(TradeParam param);

    /**
     * 到期交易
     * @param trade
     */
    void deal(Trade trade);
}
