pragma solidity ^0.8.0;


contract DAO {
    struct DAO_Info{
        string title;
        string description;
        uint balance0;
        address Dao_addr;
        mapping (address => bool) bearer;
    }


    DAO_Info public dao_info;


    event DAOAdded(address indexed account);
    event DAORemoved(address indexed account);


    constructor () internal {
        dao_info.add(msg.sender);
    }


    modifier onlyDAO() {
        require(isDAO(msg.sender), "DAO: caller does not have the DAO role");
        _;
    }

    function isDAO(DAO_Info storage dao, address account)  public view returns (bool) {
        require(account != address(0), "DAO: account is the zero address");
        return dao.bearer[account];
    }

    function addDAO(
        DAO_Info storage dao,
        address account,
        string _title,
        string _description,
    )public {
        require(!isDAO(dao, account), "DAO: account already has role");
        dao.bearer[account] = true;
        dao_info.title = _title;
        dao_info.description = _description;
        emit DAOAdded(account);
    }

    function renounceDAO(DAO_Info storage dao, address account)public onlyDAO {
        require(isDAO(dao, account), "DAO: account does not have role");
        dao.bearer[account] = false;
        emit DAORemoved(account);
    }
}





