package com.brecycle.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.brecycle.config.FiscoBcos;
import com.brecycle.contract.BatteryContract;
import com.brecycle.controller.hanlder.BusinessException;
import com.brecycle.entity.Battery;
import com.brecycle.entity.CarBattery;
import com.brecycle.entity.CarInfo;
import com.brecycle.entity.User;
import com.brecycle.entity.dto.*;
import com.brecycle.enums.BatteryStatus;
import com.brecycle.enums.RoleEnums;
import com.brecycle.mapper.BatteryMapper;
import com.brecycle.mapper.CarBatteryMapper;
import com.brecycle.mapper.CarInfoMapper;
import com.brecycle.mapper.UserMapper;
import com.brecycle.service.BatteryService;
import com.brecycle.service.PointService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.stream.Collectors;

/**
 * @author cmgun
 */
@Slf4j
@Service
public class BatteryServiceImpl implements BatteryService {

    @Autowired
    FiscoBcos fiscoBcos;
    @Autowired
    BatteryMapper batteryMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CarInfoMapper carInfoMapper;
    @Autowired
    CarBatteryMapper carBatteryMapper;
    @Autowired
    PointService pointService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(BatteryInfoParam param) throws Exception {
        // ?????????????????????????????????
        Battery existsBattery = batteryMapper.selectById(param.getId());
        if (existsBattery != null) {
            throw new BusinessException("???????????????" + param.getId() + "???????????????");
        }
        // SDK??????
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        User user = userMapper.selectByUserName(param.getProductorUserName());
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(user.getPrivateKey());
        BatteryContract contract = BatteryContract.deploy(client, currentKeyPair, param.getId()
                , param.getBatchNo(), JSON.toJSONString(param));
        // ????????????
        Battery battery = new Battery();
        BeanUtils.copyProperties(battery, param);
        battery.setAddress(contract.getContractAddress());
        battery.setCreateTime(new Date());
        battery.setStatus(BatteryStatus.SAFE_CHECK.getValue());
        battery.setOwnerId(user.getId());
        batteryMapper.insert(battery);
        // ??????????????????
        pointService.payPoint(user, Lists.newArrayList(battery), RoleEnums.PRODUCTOR.getKey());
    }

    @Override
    public void safeCheck(BatterySafeCheckParam param) {
        Battery battery = batteryMapper.selectById(param.getId());
        if (battery == null) {
            throw new BusinessException("?????????" + param.getId() + "?????????????????????");
        }
        User safeGrd = userMapper.selectByUserName(param.getSafeCheckUserName());
        if (safeGrd == null) {
            throw new BusinessException("?????????" + param.getSafeCheckUserName() + "?????????????????????");
        }
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(safeGrd.getPrivateKey());
        BatteryContract contract = BatteryContract.load(battery.getAddress(), client, currentKeyPair);
        TransactionReceipt result = contract.safeCheck(param.getRemark());
        log.info("???????????????????????????{}", result);
        // ???????????????
        battery.setStatus(BatteryStatus.NORMAL.getValue());
        batteryMapper.updateById(battery);
    }

    @Override
    public void transfer(BatteryTransferParam param, String batteryStatus) {
        Battery battery = batteryMapper.selectById(param.getId());
        if (battery == null) {
            throw new BusinessException("?????????" + param.getId() + "?????????????????????");
        }
        User origin = userMapper.selectByUserName(param.getOriginUserName());
        if (origin == null) {
            throw new BusinessException("?????????" + param.getOriginUserName() + "?????????????????????");
        }
        User to = userMapper.selectByUserName(param.getToUserName());
        if (to == null) {
            throw new BusinessException("?????????" + param.getToUserName() + "?????????????????????");
        }
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(origin.getPrivateKey());
        BatteryContract contract = BatteryContract.load(battery.getAddress(), client, currentKeyPair);
        String status = StringUtils.isNotBlank(batteryStatus) ? batteryStatus : BatteryStatus.NORMAL.getValue();
        TransactionReceipt result = contract.transfer(to.getAddr(), param.getRemark(), BigInteger.valueOf(Long.parseLong(status)));
        log.info("???????????????????????????{}", result);
        if (!StringUtils.equals(result.getStatus(), "0x0")) {
            throw new BusinessException("??????????????????");
        }
        // ??????????????????????????????
        battery.setOwnerId(to.getId());
        battery.setStatus(batteryStatus);
        batteryMapper.updateById(battery);
    }

    @Override
    public void endLife(BatteryEndParam param) throws Exception {
        Battery battery = batteryMapper.selectById(param.getId());
        User owner = userMapper.selectByUserName(param.getOriginUserName());
        if (owner == null) {
            throw new BusinessException("?????????" + param.getOriginUserName() + "?????????????????????");
        }
        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair currentKeyPair = cryptoSuite.getKeyPairFactory().createKeyPair(owner.getPrivateKey());
        BatteryContract contract = BatteryContract.load(battery.getAddress(), client, currentKeyPair);
        TransactionReceipt result = contract.endLife("?????????");
        log.info("???????????????????????????{}", result);
        if (!StringUtils.equals(result.getStatus(), "0x0")) {
            throw new BusinessException("??????????????????");
        }
        battery.setStatus(BatteryStatus.END.getValue());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        battery.setEndTime(dateFormat.parse(param.getEndTime()));
        batteryMapper.updateById(battery);
    }

    @Override
    public List<TraceInfoDTO> getTraceInfo(String batteryId, String currentUserName) throws Exception{
        Battery battery = batteryMapper.selectById(batteryId);
        if (battery == null) {
            throw new BusinessException("?????????" + batteryId + "?????????????????????");
        }
        User currentUser = userMapper.selectByUserName(currentUserName);
        if (currentUser == null) {
            throw new BusinessException("?????????" + currentUserName + "?????????????????????");
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

    @Override
    public PageResult<BatteryListDTO> batteryList(BatteryListParam param, String currentUserName) throws Exception {
        User currentUser = userMapper.selectByUserName(currentUserName);
        if (currentUser == null) {
            throw new BusinessException("?????????" + currentUserName + "?????????????????????");
        }
        IPage page = new Page<>(param.getPageNo(), param.getPageSize());
        IPage<Battery> data = batteryMapper.selectPage(page, new LambdaUpdateWrapper<Battery>()
                .eq(Battery::getOwnerId, currentUser.getId())
                .and(StringUtils.isNotBlank(param.getId()), batteryLambdaUpdateWrapper -> batteryLambdaUpdateWrapper.eq(Battery::getId, param.getId()))
        );
        List<BatteryListDTO> list = data.getRecords().stream().map(item -> {
            BatteryListDTO dto = new BatteryListDTO();
            dto.setId(item.getId());
            dto.setStatus(item.getStatus());
            return dto;
        }).collect(Collectors.toList());
        PageResult<BatteryListDTO> result = new PageResult<>();
        result.setTotal(data.getTotal());
        result.setPageNo(data.getCurrent());
        result.setPageCount(data.getPages());
        result.setPageSize(data.getSize());
        result.setData(list);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCarInfo(User car, BatteryCarInfoParam param) throws Exception {
        // ???????????????????????????????????????????????????
        List<Battery> batteries = batteryMapper.selectList(new LambdaUpdateWrapper<Battery>()
                .eq(Battery::getOwnerId, car.getId()).in(Battery::getId, param.getBatteryIds()));
        if (CollectionUtils.size(batteries) != param.getBatteryIds().size()) {
            throw new BusinessException("????????????:" + param.getCarBatchNo() + "??????????????????????????????????????????");
        }
        CarInfo carInfo = new CarInfo();
        carInfo.setId(param.getCarBatchNo());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        carInfo.setCreateTime(dateFormat.parse(param.getCreateTime()));
        carInfo.setKah(param.getKah());
        carInfo.setCreatorId(car.getId());
        carInfoMapper.insert(carInfo);
        for (String batteryId : param.getBatteryIds()) {
            CarBattery carBattery = new CarBattery();
            carBattery.setBatteryId(batteryId);
            carBattery.setCarId(carInfo.getId());
            carBatteryMapper.insert(carBattery);
        }
        List<Battery> batteryList = batteryMapper.selectBatchIds(param.getBatteryIds());
        // ????????????
        pointService.payPoint(car, batteryList, RoleEnums.CAR.getKey());
    }
}
