package com.Api.ticket.config;

import com.Api.ticket.model.TicketEntity;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hibernate.SessionFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
@Configuration
public class SpringBootConfig {
        @Value("${spring.datasource.driver-class-name}")
        private String driverClassName;

        @Value("${spring.datasource.url}")
        private String url;

        @Value("${spring.datasource.username}")
        private String username;

        @Value("${spring.datasource.password}")
        private String password;

        @Bean(name = "dataSource")
        public DataSource getDataSource() {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName(driverClassName);
            hikariConfig.setJdbcUrl(url);
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);

            hikariConfig.setMaximumPoolSize(200);
            hikariConfig.setConnectionTestQuery("SELECT 1");
            hikariConfig.setPoolName("springHikariCP");

            hikariConfig.setConnectionTimeout(180000);
            hikariConfig.setMinimumIdle(5);

            HikariDataSource dataSource = new HikariDataSource(hikariConfig);

            return dataSource;
        }

        @Bean(name = "sessionFactory")
        public SessionFactory getSessionFactory(DataSource dataSource) {
            LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
            sessionBuilder.scanPackages("com.Api.ticket.model");
            return sessionBuilder.buildSessionFactory();
        }

        @Bean(name = "transactionManager")
        public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
            HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
            return transactionManager;
        }

        @Bean
        public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
            final DataSourceInitializer initializer = new DataSourceInitializer();
            initializer.setDataSource(dataSource);
            return initializer;
        }


        @Value("${redisson.address}")
        private String redissonAddress;

        @Bean
        public RedissonClient redissonClient() {
            Config config = new Config();
            config.useSingleServer().setAddress(redissonAddress);
            return Redisson.create(config);
        }


        @Bean
        public ProducerFactory<String, TicketEntity> producerFactory(){
            Map<String,Object> config= new HashMap<>();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(config);
        }
        @Bean
        public KafkaTemplate<String,TicketEntity> kafkaTemplate(){
            return new KafkaTemplate<>(producerFactory());
        }
        @Bean
        public ConsumerFactory<String, TicketEntity> userConsumerFactory(){
            Map<String,Object> config= new HashMap<>();
            config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
            config.put(ConsumerConfig.GROUP_ID_CONFIG,"group_id");
            config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),new JsonDeserializer<>(TicketEntity.class));
        }
        @Bean
        public ConcurrentKafkaListenerContainerFactory<String,TicketEntity> userKafkaListenerFactory(){
            ConcurrentKafkaListenerContainerFactory<String,TicketEntity> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(userConsumerFactory());
            return factory;
        }



    }
