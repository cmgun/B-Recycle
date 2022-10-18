pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2;

/*
 企业准入合约
 @author cmgun
*/
contract EntAccess {
    // 企业
    address _enterprise;
    // 电池id
    string _id;
    // 批次号
    string _batchNo;
    // 电池状态 0-初始化,1-待安全审查,2-正常,3-待回收,4-回收中,5-梯次利用,6-拆解
    int16 _status;
    // 电池信息
    string _info;


    // 安全机构审查
    event newStatus(address addr, int16 status, uint timestamp, string remark);

    constructor(string id, string batchNo, string info) public {
        _id = id;
        _batchNo = batchNo;
        _info = info;
        _traceData.push(TraceData({addr:msg.sender, status:0, timestamp:now, remark:"create"}));
        emit newStatus(msg.sender, 0, now, "create");
    }
}
