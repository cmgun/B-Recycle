package com.brecycle;

import com.brecycle.config.FiscoBcos;
import com.brecycle.contract.HelloWorld;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BRecycleApplicationTest {
    @Autowired
    private FiscoBcos fiscoBcos;

    @Test
    public void testClient() throws Exception {

        BcosSDK bcosSDK = fiscoBcos.getBcosSDK();
        Client client = bcosSDK.getClient(1);

        // 获取区块号
        BlockNumber blockNumber = client.getBlockNumber();
        log.info("blockNumber: {}", blockNumber);
        log.info("blockHeight: {}", client.getBlockLimit());

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();

        // 部署合约
        HelloWorld contract = HelloWorld.deploy(client, cryptoKeyPair);
        String contractAddress = contract.getContractAddress();
        log.info("contractAddress: {}", contractAddress);

        // 调用合约测试
        String result1 = contract.get();
        log.info("get: {}", result1);
        log.info("blockHeight: {}", client.getBlockLimit());

        TransactionReceipt receipt = contract.set("cmgun");
        log.info("TransactionReceipt: {}", receipt.toString());

        String result2 = contract.get();
        log.info("get: {}", result2);
        log.info("blockHeight: {}", client.getBlockLimit());
    }

}