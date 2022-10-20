pragma solidity ^0.4.25;

import "./PointData.sol";
import "./BasicAuth.sol";
import "./LibMath.sol";
import "./P2PPoint.sol";
import "./DAO.sol";

contract PointController {
    using LibMath for uint256;

    PointData _PointData;
    uint256 public state;
    address public highestBidder;
    uint256 public highestBid;
    uint256 public amount;

    event LogRegister(address account);
    event LogUnregister(address account);
    event Transaction_msg(uint256 indexed t);

    modifier accountExist(address addr) {
        require(_PointData.hasAccount(addr)==true && addr != address(0), "Only existed account!");
        _;
    }

    modifier accountNotExist(address account) {
        require(_PointData.hasAccount(account)==false, "Account already existed!");
        _;
    }

    modifier canUnregister(address account) {
        require(_PointData.hasAccount(account)==true && _PointData.getBalance(account) == 0 , "Cann't unregister!");
        _;
    }

    modifier checkAccount(address sender) {
        require(msg.sender != sender && sender != address(0), "Can't transfer to illegal address!");
        _;
    }

    modifier onlyDAO() {
        require(_PointData.isDAO(msg.sender), "IssuerRole: caller does not have the Issuer role");
        _;
    }


    constructor(address dataAddress) public {
        _PointData = PointData(dataAddress);
    }

    //注册模块：消费者、生产者（初始化积分后期由DAO转账）
    function register() accountNotExist(msg.sender) public returns (address) {
        _PointData.setAccount(msg.sender, true);
        // init balances
        _PointData.setBalance(msg.sender, 0);
        emit LogRegister(msg.sender);
    }

    function unregister() canUnregister(msg.sender) public returns (address) {
        _PointData.setAccount(msg.sender, false);
        emit LogUnregister(msg.sender);
    }

    //公共账户不知道是否能这么用
    function ADD_DAO(address account,uint256 value) public accountExist(account) returns (bool) {
        _PointData.addDAO(account);
        uint256 totalAmount = _PointData._totalAmount();
        totalAmount = totalAmount.add(value);
        _PointData.setTotalAmount(totalAmount);
        uint256 balance1 = _PointData.getBalance(account);
        balance1 = balance1.add(value);
        _PointData.setBalance(account, balance1);
        return true;
    }


    //信息查询：地址（账户）是否存在、返回账户信息
    function isRegistered(address addr) public view returns (bool) {
        return _PointData.hasAccount(addr);
    }

    function balance(address addr) public view returns (uint256) {
        return _PointData.getBalance(addr);
    }



    //交易模块；相当于P2P（生产者购买消费者回收者积分）
    function sell(address _seller, uint256 _quant) public{
        require(msg.sender == owner);
        require(P2PPoint.transferFrom(msg.sender, this, _quant));
        quant = _quant;
        sell_time = now + 7 days
        state = 1;
        emit Transaction_msg(0); // send the msg to web3 if the function run to this point, which mean the function execute successfully
    }


    function bid(address _buyer, unit256 _price) public {
        require(now <= auction_end, "Auction already ended.");
        require(_price > highestBid, "There already is a higher bid.");
        highestBidder = msg.sender;
        highestBid = _price;
        emit HighestBidIncreased(msg.sender, value);
    }


    function deal() public{
        require(msg.sender == owner);
        require(now >= sell_time, "Auction not yet ended.");
        require(P2PPoint.transferTo(highestBidder, quant));
        emit AuctionEnded(highestBidder, highestBid);
    }


    //纳税：向DAO、积分派发：给与消费者、回收企业积分
    //企业缴纳积分
    function pay(uint256 quant) public returns(bool){
        require(msg.sender == owner);
        require(P2PPoint.transferFrom(msg.sender, this, quant));
        emit FundTransfer(msg.sender, quant, true);
        return true;
    }


    //消费者、回收企业获得积分
    //DAO组织发布任务
    function publish(address account, uint256 value,string title) public{
        require(msg.sender == owner);
        require(P2PPoint.transferFrom(msg.sender, this, price));
        state = 1;
        emit Transaction_msg(1); // send the msg to web3 if the function run to this point, which mean the function execute successfully
    }

    //申请任务
    function apply(address applicant,string info) public {
        require(state == 1);
        require(deadline < now);
        candidate_info = msg.sender[info];
        state = 2;
        emit Transaction_msg(2);
    }

    //评价申请者
    function assess_applicant(uint score) public {
        require(msg.sender == owner);
        require(state == 2);
        //applicant_score = score;
        if{
            score >= standard_value;
            P2PPoint.transferTo(applicant, price);//申请者获得积分
        } else
            P2PPoint.transferTo(owner, price);//申请者不合格，积分退回


        emit Transaction_msg(3);
    }

}
