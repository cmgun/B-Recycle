pragma solidity ^0.4.25;

import "./BasicAuth.sol";
import "./PointController.sol";
import "./PointData.sol";

contract Admin is BasicAuth {
    address public _dataAddress;
    address public _controllerAddress;

    constructor() public {
        PointData data = new PointData("Point of V1");
        _dataAddress = address(data);
        PointController controller = new PointController(_dataAddress);
        _controllerAddress = address(controller);
        data.upgradeVersion(_controllerAddress);
        data.addIssuer(msg.sender);
        data.addIssuer(_controllerAddress);//要不要加DAO合约信息
    }

    function upgradeVersion(address newVersion) public {
        PointData data = PointData(_dataAddress);
        data.upgradeVersion(newVersion);
    }

}
