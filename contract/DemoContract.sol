// SPDX-License-Identifier: MIT
//pragma solidity ^0.8.0;
pragma solidity>=0.8.0;

contract DemoContract {

    struct User {

        uint160 id;

        string name;

        string sex;
    }

    mapping(uint160 => User) users;

    uint160[] ids;

    modifier notExist() {
        bool exist = isExist(uint160(msg.sender));
        require(!exist, "User already exist");
        _;
    }

    function isExist(uint160 id) private view returns(bool) {
        // 这种校验实际可以放在应用层
        for (uint8 i = 0; i < ids.length; i++) {
            if (uint160(id) == ids[i]){
                return true;
            }
        }
        return false;
    }

    function add(string memory name, string memory sex) public notExist returns(uint160, string memory, string memory) {
        // id为当前合约的调用者
        uint160 id = uint160(msg.sender);
        // memory修饰的变量都只存在于函数调用期间
        User memory user = User(id, name, sex);
        ids.push(id);
        users[id] = user;
        return (users[id].id, users[id].name, users[id].sex);
    }

    function update(string memory name, string memory sex) public returns(uint160, string memory, string memory) {
        bool exist = isExist(uint160(msg.sender));
        require(exist, "User not exist");
        uint160 id = uint160(msg.sender);
        User memory user = User(id, name, sex);
        users[id] = user;
        return (users[id].id, users[id].name, users[id].sex);
    }

    function del(uint160 id) public {
        uint8 index = 0;
        bool exist = false;
        for (uint8 i = 0; i < ids.length; i++){
            if (uint160(msg.sender) == ids[i]){
                exist = true;
                index = i;
                break;
            }
        }
        require(exist, "User not exist");
        delete ids[index];
        delete users[id];
    }

    function get(uint160 id) public view returns(uint160, string memory, string memory) {
        return (users[id].id, users[id].name, users[id].sex);
    }

    function test() public pure returns(string memory) {
        return "hello world";
    }
}
