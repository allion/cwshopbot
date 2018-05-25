//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.eu.cwshopbot.application.config;

import name.maratik.cw.eu.spring.annotation.EnableTelegramBot;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Configuration
@EnableTelegramBot
public class ExternalConfig {

//    @Configuration
//    @EnableRabbit
//    public static class RabbitConfiguration {
//        @Bean
//        public ConnectionFactory connectionFactory(
//            @Value("${cwapi.host}") String host, @Value("${cwapi.port}") int port,
//            @Value("${name.maratik.cw.eu.cwshopbot.cwapi.username}") String username,
//            @Value("${name.maratik.cw.eu.cwshopbot.cwapi.password}") String password,
//            com.rabbitmq.client.ConnectionFactory clientConnectionFactory
//        ) {
//            CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
//                clientConnectionFactory
//            );
//            connectionFactory.setHost(host);
//            connectionFactory.setPort(port);
//            connectionFactory.setUsername(username);
//            connectionFactory.setPassword(password);
//            ThreadFactory tf = new CustomizableThreadFactory("rabbitmq-");
//            connectionFactory.setConnectionThreadFactory(tf);
//            return connectionFactory;
//        }
//
//        @Bean
//        public FactoryBean<com.rabbitmq.client.ConnectionFactory> clientConnectionFactory() {
//            RabbitConnectionFactoryBean rabbitConnectionFactoryBean = new RabbitConnectionFactoryBean();
//            rabbitConnectionFactoryBean.setUseSSL(true);
//            rabbitConnectionFactoryBean.setUseNio(true);
//            rabbitConnectionFactoryBean.setSslAlgorithm("TLSv1.2");
//            return rabbitConnectionFactoryBean;
//        }
//
//        @Bean
//        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//            RetryTemplate retryTemplate = new RetryTemplate();
//            ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
//            exponentialBackOffPolicy.setInitialInterval(500);
//            exponentialBackOffPolicy.setMultiplier(10);
//            exponentialBackOffPolicy.setMaxInterval(10_000);
//            retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
//            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//            rabbitTemplate.setRetryTemplate(retryTemplate);
//            return rabbitTemplate;
//        }
//
//        @Bean
//        public DirectRabbitListenerContainerFactoryConfigurer
//    }

    @Configuration
    public static class AWSConfiguration {
        @Bean
        public AWSCredentials awsCredentials(
            @Value("${name.maratik.cw.eu.cwshopbot.accessKeyId}") String accessKey,
            @Value("${name.maratik.cw.eu.cwshopbot.secretAccessKey}") String secretKey
        ) {
            return new BasicAWSCredentials(accessKey, secretKey);
        }

        @Bean
        public AWSCredentialsProvider awsCredentialsProvider(AWSCredentials awsCredentials) {
            return new AWSStaticCredentialsProvider(awsCredentials);
        }

        @Bean
        public Regions regions(@Value("${name.maratik.cw.eu.cwshopbot.region}") String regionName) {
            return Regions.fromName(regionName);
        }

        @Bean
        public AmazonDynamoDB amazonDynamoDB(AWSCredentialsProvider awsCredentialsProvider) {
            return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_1)
                .withCredentials(awsCredentialsProvider)
                .build();
        }
    }
}
