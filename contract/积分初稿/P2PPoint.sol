pragma solidity ^0.4.25;

//需要引用接口吗？待测试
interface tokenRecipient{function receiveApproval(address _from, uint256 _value, address _token, bytes _extraData) public;}

contract P2PPoint{


    event LogSend( address indexed from, address indexed to, uint256 value);

    function _transfer(address _from, address _to, uint256 _value) internal{
        require(_to != 0x0); // address can't be null
        require(balanceOf[_from] >= _value);
        require(balanceOf[_to] + _value > balanceOf[_to]);

        uint previousBalances = balanceOf[_from] + balanceOf[_to];
        balanceOf[_from] -= _value;
        balanceOf[_to] += _value;
        emit LogSend(msg.sender, toAddress, value);

        assert(balanceOf[_from] + balanceOf[_to] == previousBalances);

    }
    // token transfer function, call its internal function
    function transferTo(address _to, uint256 _value) public returns(bool success) {
        require(_to != 0x0);
        _transfer(msg.sender, _to, _value);
        return true;
    }

    function transferFrom(address _from, address _to, uint256 _value) public returns(bool success){
        require(_value <= allowance[_from][msg.sender]);
        allowance[_from][msg.sender] -= _value;
        _transfer(_from, _to, _value);
        return true;
    }
}