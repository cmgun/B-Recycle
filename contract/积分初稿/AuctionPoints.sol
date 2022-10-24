pragma solidity ^0.4.25;
import "./P2PPoint.sol";

contract AuctionPoints {
    P2PPoint P2PPoint;
    address public owner;
    uint256 public quant;
    uint public auction_end;

    event Transaction_msg(uint256 indexed t);
    event HighestBidRaised(address msg.sender,uint256 value);
    event AuctionEnded(address highestBidder,uint256 highestBid);

    constructor() public{
        owner = msg.sender;
    }

    //竞价
    function sell(address _seller, uint256 _quant) public{
        require(msg.sender == owner);
        require(P2PPoint.transferFrom(msg.sender, this, _quant));
        quant = _quant;
        auction_end = now + 7 days
        state = 1;
        emit Transaction_msg(0);
    }


    function bid(address _buyer, uint256 _price) public {
        require(now <= auction_end, "Auction already ended.");
        require(_price > highestBid, "There already is a higher bid.");
        address highestBidder = msg.sender;
        uint256 highestBid = _price;
        emit HighestBidRaised(msg.sender, value);
    }


    function deal() public{
        require(msg.sender == owner);
        require(now >= sell_time, "Auction not yet ended.");
        require(P2PPoint.transferTo(highestBidder, quant));
        emit AuctionEnded(highestBidder, highestBid);
    }

}
