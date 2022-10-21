package com.brecycle.service.impl;

import com.alibaba.fastjson2.JSON;
import com.brecycle.config.FiscoBcos;
import com.brecycle.contract.BatteryContract;
import com.brecycle.controller.hanlder.BusinessException;
import com.brecycle.entity.Battery;
import com.brecycle.entity.User;
import com.brecycle.entity.dto.BatteryInfoParam;
import com.brecycle.entity.dto.BatterySafeCheckParam;
import com.brecycle.entity.dto.BatteryTransferParam;
import com.brecycle.entity.dto.TraceInfoDTO;
import com.brecycle.enums.BatteryStatus;
import com.brecycle.mapper.BatteryMapper;
import com.brecycle.mapper.UserMapper;
import com.brecycle.service.BatteryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author cmgun
 */
@Slf4j
@Service
public class BatteryServiceImpl implements BatteryService {

    @Autowired
    private FiscoBcos fiscoBcos;
    @Autowired
    BatteryMapper batteryMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(BatteryInfoParam param) throws Exception {
        // 查看是否已有相同的信息
        Battery existsBattery = batteryMapper.selectById(param.getId());
        if (existsBattery != null) {
            throw new BusinessException("已有编号为" + param.getId() + "的电池信息");
        }
        // SDK配置
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        User user = userMapper.selectByUserName(param.getProductorUserName());
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(user.getPrivateKey());
        BatteryContract contract = BatteryContract.deploy(client, currentKeyPair, param.getId()
                , param.getBatchNo(), JSON.toJSONString(param));
        // FIXME 调用积分合约

        // 保存数据
        Battery battery = new Battery();
        BeanUtils.copyProperties(battery, param);
        battery.setAddress(contract.getContractAddress());
        battery.setCreateTime(new Date());
        battery.setStatus(BatteryStatus.SAFE_CHECK.getValue());
        battery.setOwnerId(user.getId());
        batteryMapper.insert(battery);
    }

    @Override
    public void safeCheck(BatterySafeCheckParam param) {
        Battery battery = batteryMapper.selectById(param.getId());
        if (battery == null) {
            throw new BusinessException("编号为" + param.getId() + "的电池信息缺失");
        }
        User safeGrd = userMapper.selectByUserName(param.getSafeCheckUserName());
        if (safeGrd == null) {
            throw new BusinessException("账号为" + param.getSafeCheckUserName() + "的用户信息缺失");
        }
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(safeGrd.getPrivateKey());
        BatteryContract contract = BatteryContract.load(battery.getAddress(), client, currentKeyPair);
        TransactionReceipt result = contract.safeCheck(param.getRemark());
        log.info("安全认证执行结果：{}", result);
        // 持有方不变
        battery.setStatus(BatteryStatus.NORMAL.getValue());
        batteryMapper.updateById(battery);
    }

    @Override
    public void transfer(BatteryTransferParam param) {
        Battery battery = batteryMapper.selectById(param.getId());
        if (battery == null) {
            throw new BusinessException("编号为" + param.getId() + "的电池信息缺失");
        }
        User origin = userMapper.selectByUserName(param.getOriginUserName());
        if (origin == null) {
            throw new BusinessException("账号为" + param.getOriginUserName() + "的用户信息缺失");
        }
        User to = userMapper.selectByUserName(param.getToUserName());
        if (to == null) {
            throw new BusinessException("账号为" + param.getToUserName() + "的用户信息缺失");
        }
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(origin.getPrivateKey());
        BatteryContract contract = BatteryContract.load(battery.getAddress(), client, currentKeyPair);
        TransactionReceipt result = contract.transfer(to.getAddr(), param.getRemark(), BigInteger.valueOf(Long.parseLong(BatteryStatus.NORMAL.getValue())));
        log.info("电池流转执行结果：{}", result);
        // 更新数据库，便于查询
        battery.setOwnerId(to.getId());
        batteryMapper.updateById(battery);
    }

    @Override
    public List<TraceInfoDTO> getTraceInfo(String batteryId, String currentUserName) throws Exception{
        Battery battery = batteryMapper.selectById(batteryId);
        if (battery == null) {
            throw new BusinessException("编号为" + batteryId + "的电池信息缺失");
        }
        User currentUser = userMapper.selectByUserName(currentUserName);
        if (currentUser == null) {
            throw new BusinessException("账号为" + currentUserName + "的用户信息缺失");
        }
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(currentUser.getPrivateKey());
        BatteryContract contract = BatteryContract.load(battery.getAddress(), client, currentKeyPair);
        DynamicArray<BatteryContract.Struct0> result = contract.getTraceInfo();
        List<TraceInfoDTO> traceInfos = Lists.newArrayList();
        for (BatteryContract.Struct0 item : result.getValue()) {
            TraceInfoDTO info = new TraceInfoDTO();
            info.setAddress(item.addr);
            Date optTime = new Date(Long.parseLong(item.timestamp.toString()));
            String formatOptTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(optTime);
            info.setOptTime(formatOptTime);
            info.setRemark(item.remark);
            traceInfos.add(info);
        }
        return traceInfos;
    }
}
