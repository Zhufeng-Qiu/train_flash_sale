// package com.zephyr.train.business.config;
//
// import org.apache.rocketmq.client.producer.DefaultMQProducer;
// import org.apache.rocketmq.spring.core.RocketMQTemplate;
// import org.springframework.context.annotation.Bean;
// import org.springframework.stereotype.Component;
//
// @Component
// public class RocketMQConfig {
//
//     /**
//      * Need to declare RocketMQTemplate in new version; otherwise, injection will fail
//      * @return
//      */
//     @Bean
//     public RocketMQTemplate rocketMQTemplate() {
//         RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
//         DefaultMQProducer producer = new DefaultMQProducer();
//         producer.setProducerGroup("default");
//         producer.setNamesrvAddr("http://localhost:9876");
//         producer.setSendMsgTimeout(3000);
//         rocketMQTemplate.setProducer(producer);
//         return rocketMQTemplate;
//     }
// }
