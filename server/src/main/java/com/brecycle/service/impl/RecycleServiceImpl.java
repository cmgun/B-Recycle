package com.brecycle.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.brecycle.config.FiscoBcos;
import com.brecycle.contract.TradeContract;
import com.brecycle.controller.hanlder.BusinessException;
import com.brecycle.entity.Battery;
import com.brecycle.entity.Trade;
import com.brecycle.entity.TradeBattery;
import com.brecycle.entity.User;
import com.brecycle.entity.dto.*;
import com.brecycle.enums.BatteryStatus;
import com.brecycle.enums.TradeStatus;
import com.brecycle.enums.TradeType;
import com.brecycle.mapper.BatteryMapper;
import com.brecycle.mapper.TradeBatteryMapper;
import com.brecycle.mapper.TradeMapper;
import com.brecycle.mapper.UserMapper;
import com.brecycle.service.BatteryService;
import com.brecycle.service.RecycleService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cmgun
 */
@Slf4j
@Service
public class RecycleServiceImpl implements RecycleService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    TradeMapper tradeMapper;
    @Autowired
    TradeBatteryMapper tradeBatteryMapper;
    @Autowired
    FiscoBcos fiscoBcos;
    @Autowired
    BatteryService batteryService;
    @Autowired
    BatteryMapper batteryMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(List<String> batteryIds, RecycleApplyParam param, String currentUserName) throws Exception {
        // ???????????????????????????????????????
        User user = userMapper.selectByUserName(currentUserName);
        Integer batteries = batteryMapper.selectCount(new LambdaUpdateWrapper<Battery>()
                .in(Battery::getId, batteryIds)
                .eq(Battery::getOwnerId, user.getId())
                .notIn(Battery::getStatus, Lists.newArrayList(BatteryStatus.RECYCLE_TRADING.getValue()
                        , BatteryStatus.SECOND_USED_TRADING.getValue())));
        if (!batteries.equals(batteryIds.size())) {
            throw new BusinessException("???????????????????????????????????????");
        }
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(user.getPrivateKey());
        // ???????????????
        if (param.isHasTarget()) {
            User recycleEnt = userMapper.selectByName(param.getName());
            if (recycleEnt == null) {
                throw new BusinessException("??????????????????????????????");
            }
            // SDK??????
            BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
            Client client = bcosSDK.getClient(1);
            // ??????????????????????????????
            TradeContract tradeContract = TradeContract.deploy(client, currentKeyPair, JSONArray.toJSONString(batteryIds)
                    , BigInteger.valueOf(param.getLowestAmt().longValue()), BigInteger.valueOf(param.getExpectAmt().longValue())
                    , BigInteger.valueOf(param.getBidDays().longValue()));
            TransactionReceipt result = tradeContract.targetDeal(recycleEnt.getAddr());
            log.info("recycle.apply???????????????{}", result);
            if (!StringUtils.equals(result.getStatus(), "0x0")) {
                throw new BusinessException("??????????????????");
            }
            // ????????????
            for (String batteryId : batteryIds) {
                BatteryTransferParam transferParam = new BatteryTransferParam();
                transferParam.setId(batteryId);
                transferParam.setOriginUserName(currentUserName);
                transferParam.setToUserName(recycleEnt.getUserName());
                transferParam.setRemark("????????????");
                batteryService.transfer(transferParam, BatteryStatus.RECYCLE.getValue());
            }
            // ????????????
            Trade trade = new Trade();
            trade.setSellerId(user.getId());
            trade.setBuyerId(recycleEnt.getId());
            trade.setLowestAmt(param.getLowestAmt());
            trade.setExpectAmt(param.getExpectAmt());
            trade.setTradeAmt(param.getExpectAmt());
            trade.setCreateTime(new Date());
            trade.setTradeType(TradeType.RECYCLE.getValue());
            trade.setStatus(TradeStatus.SUCCESS.getValue());
            trade.setAddr(tradeContract.getContractAddress());
            tradeMapper.insert(trade);
            // ??????????????????
            for (String batteryId : batteryIds) {
                TradeBattery tradeBattery = new TradeBattery();
                tradeBattery.setBatteryId(batteryId);
                tradeBattery.setTradeId(trade.getId());
                tradeBatteryMapper.insert(tradeBattery);
            }
            return;
        }
        // ????????????
        // SDK??????
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        // ??????????????????????????????
        TradeContract tradeContract = TradeContract.deploy(client, currentKeyPair, JSONArray.toJSONString(batteryIds)
                , BigInteger.valueOf(param.getLowestAmt().longValue()), BigInteger.valueOf(param.getExpectAmt().longValue())
                , BigInteger.valueOf(param.getBidDays().longValue()));
        // ??????????????????
        for (String batteryId : batteryIds) {
            Battery battery = new Battery();
            battery.setId(batteryId);
            battery.setStatus(BatteryStatus.RECYCLE_TRADING.getValue());
            batteryMapper.updateById(battery);
        }
        // ??????????????????
        // ????????????
        Trade trade = new Trade();
        trade.setSellerId(user.getId());
        trade.setLowestAmt(param.getLowestAmt());
        trade.setExpectAmt(param.getExpectAmt());
        trade.setBidDays(param.getBidDays());
        trade.setCreateTime(new Date());
        trade.setTradeType(TradeType.RECYCLE.getValue());
        trade.setStatus(TradeStatus.BIDING.getValue());
        trade.setAddr(tradeContract.getContractAddress());
        tradeMapper.insert(trade);
        // ??????????????????
        for (String batteryId : batteryIds) {
            TradeBattery tradeBattery = new TradeBattery();
            tradeBattery.setBatteryId(batteryId);
            tradeBattery.setTradeId(trade.getId());
            tradeBatteryMapper.insert(tradeBattery);
        }
    }

    @Override
    public PageResult<TradeListDTO> list(TradeListParam param) {
        IPage page = new Page<>(param.getPageNo(), param.getPageSize());
        if (param.getMyId() == null) {
            param.setStatus(TradeStatus.BIDING.getValue());
        }
        param.setTradeType(TradeType.RECYCLE.getValue());
        IPage<Trade> data = tradeMapper.selectTradeListByPage(page, param);
        PageResult<TradeListDTO> result = new PageResult<>();
        result.setTotal(data.getTotal());
        result.setPageNo(data.getCurrent());
        result.setPageCount(data.getPages());
        result.setPageSize(data.getSize());
        if (CollectionUtils.isNotEmpty(data.getRecords())) {
            result.setData(data.getRecords().stream().map(item -> {
                TradeListDTO tradeListDTO = new TradeListDTO();
                tradeListDTO.setId(item.getId());
                User seller = userMapper.selectById(item.getSellerId());
                tradeListDTO.setSellerName(seller.getName());
                if (item.getBuyerId() != null) {
                    User buyer = userMapper.selectById(item.getBuyerId());
                    tradeListDTO.setBuyerName(buyer.getName());
                    tradeListDTO.setTradeAmt(item.getTradeAmt());
                }
                tradeListDTO.setStatus(item.getStatus());
                tradeListDTO.setLowestAmt(item.getLowestAmt());
                tradeListDTO.setInfo("??????????????????");
                return tradeListDTO;
            }).collect(Collectors.toList()));
        } else {
            result.setData(Lists.newArrayList());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bid(TradeParam param) {
        User buyer = userMapper.selectByUserName(param.getBuyerUserName());
        Trade trade = tradeMapper.selectById(param.getTradeId());
        if (trade == null) {
            throw new BusinessException("???????????????");
        }
        if (buyer.getId().equals(trade.getSellerId())) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        Date now = new Date();
        Date endTime = DateUtils.addDays(trade.getCreateTime(), trade.getBidDays());
        if (now.compareTo(endTime) > 0) {
            throw new BusinessException("??????????????????");
        }
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(buyer.getPrivateKey());
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        TradeContract tradeContract = TradeContract.load(trade.getAddr(), client, currentKeyPair);
        TransactionReceipt receipt = tradeContract.bid(BigInteger.valueOf(param.getBidAmt().longValue()));
        log.info("recycle.bid???????????????{}", receipt);
        Tuple1<Boolean> result = tradeContract.getBidOutput(receipt);
        if (!StringUtils.equals(receipt.getStatus(), "0x0")) {
            throw new BusinessException("????????????????????????????????????");
        }
        if (result.getValue1()) {
            // ??????????????????
            trade.setStatus(TradeStatus.SUCCESS.getValue());
            trade.setBuyerId(buyer.getId());
            trade.setTradeAmt(param.getBidAmt());
            tradeMapper.updateById(trade);
            batteryTransfer(trade, buyer);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deal(Trade trade) {
        User seller = userMapper.selectById(trade.getSellerId());
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(seller.getPrivateKey());
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        TradeContract tradeContract = TradeContract.load(trade.getAddr(), client, currentKeyPair);
        TransactionReceipt receipt = tradeContract.deal();
        log.info("recycle.deal???????????????{}", receipt);
        if (!StringUtils.equals(receipt.getStatus(), "0x0")) {
            log.error("??????????????????????????????");
        }
        Tuple2<String, BigInteger> result = tradeContract.getDealOutput(receipt);
        // ???????????????????????????????????????
        if (StringUtils.isBlank(result.getValue1()) || result.getValue1().startsWith("0x00000")) {
            // ????????????
            trade.setStatus(TradeStatus.REJECT.getValue());
            tradeMapper.updateById(trade);
            List<TradeBattery> tradeBatteries = tradeBatteryMapper.selectList(new LambdaUpdateWrapper<TradeBattery>()
                            .eq(TradeBattery::getTradeId, trade.getId()));
            for (TradeBattery tradeBattery : tradeBatteries) {
                Battery battery = new Battery();
                battery.setId(tradeBattery.getBatteryId());
                battery.setStatus(BatteryStatus.WAIT_RECYCLE.getValue());
                batteryMapper.updateById(battery);
            }
            return;
        }
        User buyer = userMapper.selectByAddr(result.getValue1());
        trade.setBuyerId(buyer.getId());
        trade.setTradeAmt(BigDecimal.valueOf(result.getValue2().longValue()));
        trade.setStatus(TradeStatus.SUCCESS.getValue());
        tradeMapper.updateById(trade);
        batteryTransfer(trade, buyer);
    }

    private void batteryTransfer(Trade trade, User buyer) {
        List<String> batteryIds = tradeBatteryMapper.selectList(new LambdaUpdateWrapper<TradeBattery>()
                        .eq(TradeBattery::getTradeId, trade.getId()))
                .stream().map(TradeBattery::getBatteryId).collect(Collectors.toList());
        User originUser = userMapper.selectById(trade.getSellerId());
        for (String batteryId : batteryIds) {
            BatteryTransferParam transferParam = new BatteryTransferParam();
            transferParam.setId(batteryId);
            transferParam.setOriginUserName(originUser.getUserName());
            transferParam.setToUserName(buyer.getUserName());
            transferParam.setRemark("????????????");
            batteryService.transfer(transferParam, BatteryStatus.RECYCLE.getValue());
        }
    }
}
