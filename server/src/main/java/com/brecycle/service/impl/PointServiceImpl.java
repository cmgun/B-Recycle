package com.brecycle.service.impl;

import com.brecycle.config.FiscoBcos;
import com.brecycle.config.PointConfig;
import com.brecycle.contract.PointController;
import com.brecycle.contract.TradeContract;
import com.brecycle.controller.hanlder.BusinessException;
import com.brecycle.entity.Battery;
import com.brecycle.entity.EntInfo;
import com.brecycle.entity.Trade;
import com.brecycle.entity.User;
import com.brecycle.entity.dto.*;
import com.brecycle.enums.TradeStatus;
import com.brecycle.enums.TradeType;
import com.brecycle.mapper.TradeMapper;
import com.brecycle.mapper.UserMapper;
import com.brecycle.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 积分模块
 *
 * @author cmgun
 */
@Slf4j
@Service
public class PointServiceImpl implements PointService {
    // FIXME 拆解的计算需要根据不同类型的电池进行，国标参数暂时放在这里
    // FIXME 其余环节待定

    @Autowired
    FiscoBcos fiscoBcos;
    @Autowired
    PointConfig pointConfig;
    @Autowired
    UserMapper userMapper;
    @Autowired
    TradeMapper tradeMapper;

    @Override
    public String deployContract() throws Exception {
        // 查找DAO账户
        User admin = userMapper.selectByUserName(pointConfig.dao);
        // SDK配置
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(admin.getPrivateKey());
        // 部署交易，标记为成功
        PointController pointController = PointController.deploy(client, currentKeyPair);
        log.info("pointController addr:{}", pointController.getContractAddress());
        return pointController.getContractAddress();
    }

    @Override
    public void addDAO() {
        User dao = userMapper.selectByUserName(pointConfig.dao);
        // SDK配置
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(dao.getPrivateKey());
        // 部署交易，标记为成功
        PointController pointController = PointController.load(pointConfig.getPointController(), client, currentKeyPair);
        TransactionReceipt result = pointController.addDAO(dao.getAddr());

        log.info("addDAO执行结果：{}", result);
        if (!StringUtils.equals(result.getStatus(), "0x0")) {
            throw new BusinessException("执行交易失败");
        }
    }

    @Override
    public void initPoint() throws Exception {
        // 查找DAO账户
        User admin = userMapper.selectByUserName(pointConfig.dao);
        // SDK配置
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(admin.getPrivateKey());
        // 计算初始积分1：电池生产商、车企
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currentYear = calendar.get(Calendar.YEAR);
        Map<String, Integer> marketPredict = pointConfig.getMarketPredict();
        Map<String, Integer> systemPredict = pointConfig.getSystemPredict();
        BigDecimal bt_1 = new BigDecimal(systemPredict.get(String.valueOf(currentYear - 1)));
        BigDecimal at = new BigDecimal(marketPredict.get(String.valueOf(currentYear)));
        BigDecimal at_1 = new BigDecimal(marketPredict.get(String.valueOf(currentYear - 1)));
        BigDecimal carAvgKmh = new BigDecimal(pointConfig.getCarAvgKmh());
        BigDecimal point1 = bt_1.multiply(at).multiply(carAvgKmh).divide(at_1, 0, RoundingMode.DOWN);
        log.info("initPoint, point1={}", point1);
        // 初始积分2：回收商
        BigDecimal point2;
        BigDecimal bt1 = new BigDecimal(systemPredict.get(String.valueOf(currentYear + 1)));
        if (pointConfig.getVarT() <= 5) {
            point2 = bt1.multiply(carAvgKmh).setScale(0, RoundingMode.DOWN);
        } else {
            Float partRatio = pointConfig.getEndRecyclePartRatio();
            BigDecimal endRecyclePartRatio = new BigDecimal(1 - partRatio / 10);
            BigDecimal bt_4 = new BigDecimal(systemPredict.get(String.valueOf(currentYear - 1)));
            point2 = bt1.add(bt_4.multiply(endRecyclePartRatio)).multiply(carAvgKmh).setScale(0, RoundingMode.DOWN);
        }
        log.info("initPoint, point2={}", point2);
        // 获取积分合同
        PointController pointController = PointController.load(pointConfig.getPointController(), client, currentKeyPair);
        TransactionReceipt result = pointController.initPoint(point1.toBigInteger(), point2.toBigInteger());
        log.info("initPoint执行结果：{}", result);
        if (!StringUtils.equals(result.getStatus(), "0x0")) {
            throw new BusinessException("执行交易失败");
        }
        BigInteger totalPoint = pointController.getPoint(admin.getAddr());
        log.info("initPoint, current dao point:{}", totalPoint);
    }

    @Override
    public void registAccount(User user) {
        // FIXME
    }

    @Override
    public void registPointPublish(User user, EntInfo entInfo) {
        // FIXME
    }

    @Override
    public void yearRecycleEntPublish(User recycleEnt) {
        // FIXME
    }

    @Override
    public void payPoint(User payEnt, List<Battery> batteryList) {
        // FIXME
    }

    @Override
    public void customerPoint(User customer, List<Battery> battery) {
        // FIXME
    }

    @Override
    public void secondUsedPoint(User storedEnt, List<Battery> batteryList) {
        // FIXME
    }

    @Override
    public void secondRecyclePoint(User recycleEnt, List<Battery> batteryList) {
        // FIXME
    }

    @Override
    public void apply(PointTradeApplyParam param, String currentUserName) throws Exception {
        // 获取当前登录用户的账户信息
        User seller = userMapper.selectByUserName(currentUserName);
        // 指定买方
        if (param.isHasTarget()) {
            saveSuccessTrade(param, seller);
            return;
        }
        // 委托交易
        // SDK配置
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(seller.getPrivateKey());
        // FIXME 冻结积分

        // 部署交易，标记为成功
        TradeContract tradeContract = TradeContract.deploy(client, currentKeyPair, ""
                , BigInteger.valueOf(param.getLowestAmt().longValue()), BigInteger.valueOf(param.getExpectAmt().longValue())
                , BigInteger.valueOf(param.getBidDays().longValue()));
        // 保存交易数据
        // 保存数据
        Trade trade = new Trade();
        trade.setSellerId(seller.getId());
        trade.setLowestAmt(param.getLowestAmt());
        trade.setExpectAmt(param.getExpectAmt());
        trade.setBidDays(param.getBidDays());
        trade.setCreateTime(new Date());
        trade.setTradeType(TradeType.POINT.getValue());
        trade.setStatus(TradeStatus.BIDING.getValue());
        trade.setAddr(tradeContract.getContractAddress());
        tradeMapper.insert(trade);
    }

    @Override
    public PageResult<TradeListDTO> list(TradeListParam param) {
        // FIXME
        return null;
    }

    @Override
    public void bid(TradeParam param) {
        // FIXME

    }

    @Override
    public void deal(Trade trade) {
        // FIXME
    }

    /**
     * 记录一笔积分交易明细
     */
    private void saveSuccessTrade(PointTradeApplyParam param, User seller) throws Exception {
        User buyer = userMapper.selectByName(param.getName());
        if (buyer == null) {
            throw new BusinessException("指定买方企业名称不存在");
        }
        // SDK配置
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(seller.getPrivateKey());

        // FIXME 积分转移

        // 部署交易，标记为成功
        TradeContract tradeContract = TradeContract.deploy(client, currentKeyPair, ""
                , BigInteger.valueOf(param.getLowestAmt().longValue()), BigInteger.valueOf(param.getExpectAmt().longValue())
                , BigInteger.valueOf(param.getBidDays().longValue()));
        TransactionReceipt result = tradeContract.targetDeal(buyer.getAddr());
        log.info("pointTrade.apply执行结果：{}", result);
        if (!StringUtils.equals(result.getStatus(), "0x0")) {
            throw new BusinessException("执行交易失败");
        }

        // 保存数据
        Trade trade = new Trade();
        trade.setSellerId(seller.getId());
        trade.setBuyerId(buyer.getId());
        trade.setLowestAmt(param.getLowestAmt());
        trade.setExpectAmt(param.getExpectAmt());
        trade.setTradeAmt(param.getExpectAmt());
        trade.setCreateTime(new Date());
        trade.setTradeType(TradeType.POINT.getValue());
        trade.setStatus(TradeStatus.SUCCESS.getValue());
        trade.setAddr(tradeContract.getContractAddress());
        tradeMapper.insert(trade);
    }
}
