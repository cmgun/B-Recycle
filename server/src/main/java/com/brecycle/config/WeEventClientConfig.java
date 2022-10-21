package com.brecycle.config;

import com.brecycle.listener.BatteryAddListener;
import com.brecycle.listener.BatterySafeCheckListener;
import com.brecycle.listener.BatteryTransferListener;
import com.webank.weevent.client.IWeEventClient;
import com.webank.weevent.client.WeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cmgun
 */
@Configuration
public class WeEventClientConfig {

    @Autowired
    WeEventConfig eventConfig;
    @Autowired
    BatteryAddListener batteryAddListener;
    @Autowired
    BatterySafeCheckListener batterySafeCheckListener;
    @Autowired
    BatteryTransferListener batteryTransferListener;

    @Bean
    public IWeEventClient clientConfig() throws Exception {
        IWeEventClient client = IWeEventClient.builder()
                .brokerUrl(eventConfig.getBrokerUrl())
                .groupId(WeEvent.DEFAULT_GROUP_ID)
                .build();
        // 打开topic
        client.open(eventConfig.getBatteryAddTopic());
        client.open(eventConfig.getSafeCheckTopic());
        client.open(eventConfig.getProductorTransferTopic());
        client.open(eventConfig.getRentTransferTopic());
        client.open(eventConfig.getCarTransferTopic());
        // 绑定监听器
        client.subscribe(eventConfig.getBatteryAddTopic(), WeEvent.OFFSET_LAST, null, batteryAddListener);
        client.subscribe(eventConfig.getSafeCheckTopic(), WeEvent.OFFSET_LAST, null, batterySafeCheckListener);
        client.subscribe(eventConfig.getProductorTransferTopic(), WeEvent.OFFSET_LAST, null, batteryTransferListener);
        client.subscribe(eventConfig.getRentTransferTopic(), WeEvent.OFFSET_LAST, null, batteryTransferListener);
        client.subscribe(eventConfig.getCarTransferTopic(), WeEvent.OFFSET_LAST, null, batteryTransferListener);

        return client;
    }
}
