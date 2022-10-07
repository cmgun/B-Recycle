pragma solidity ^0.4.25;

import "./Battery.sol";

/*
 电池工厂，记录电池信息
 @author cmgun
*/
contract BatteryFactory {

    address security;
    // 电池列表
    Battery[] _battery;

    // 安全监督修饰符
    modifier onlySecurity() {
        require(msg.sender == security);
        _;
    }

    function BatteryFactory(){

    }


}
