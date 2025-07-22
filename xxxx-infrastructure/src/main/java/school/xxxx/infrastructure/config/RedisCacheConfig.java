// xxxx-infrastructure/src/main/java/school/xxxx/infrastructure/config/RedisCacheConfig.java
package school.xxxx.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis Cache Configuration - hoạt động với Redis không password
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "redis") // ✅ Chỉ active khi cache type = redis
@Slf4j
public class RedisCacheConfig {

    /**
     * Redis Template for manual cache operations
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("🔧 Configuring Redis Template...");

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // JSON serialization setup
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = createObjectMapper();
        serializer.setObjectMapper(mapper);

        // Key and Value serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        log.info("✅ Redis Template configured successfully!");
        return template;
    }

    /**
     * Cache Manager với different TTL cho different cache regions
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        log.info("🔧 Configuring Redis Cache Manager...");

        RedisCacheConfiguration defaultConfig = createCacheConfiguration(Duration.ofMinutes(30));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // User cache - 1 hour TTL
        cacheConfigurations.put("users", createCacheConfiguration(Duration.ofHours(1)));

        // Active users cache - 30 minutes TTL
        cacheConfigurations.put("activeUsers", createCacheConfiguration(Duration.ofMinutes(30)));

        // User statistics - 15 minutes TTL (frequently changing data)
        cacheConfigurations.put("userStats", createCacheConfiguration(Duration.ofMinutes(15)));

        // User reports - 6 hours TTL (expensive queries)
        cacheConfigurations.put("userReports", createCacheConfiguration(Duration.ofHours(6)));

        // Session cache - 24 hours TTL
        cacheConfigurations.put("sessions", createCacheConfiguration(Duration.ofHours(24)));

        // Security cache - 10 minutes TTL
        cacheConfigurations.put("security", createCacheConfiguration(Duration.ofMinutes(10)));

        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();

        log.info("✅ Redis Cache Manager configured with {} cache regions", cacheConfigurations.size());
        return cacheManager;
    }

    /**
     * Create cache configuration với TTL và serialization
     */
    private RedisCacheConfiguration createCacheConfiguration(Duration ttl) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = createObjectMapper();
        serializer.setObjectMapper(mapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues()
                .prefixCacheNameWith("school:cache:");
    }

    /**
     * ObjectMapper cho JSON serialization/deserialization
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Handle Java 8 time types
        mapper.registerModule(new JavaTimeModule());

        // Configure visibility
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // Enable type information for polymorphic types
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return mapper;
    }

    /**
     * Cache key generator cho custom cache keys
     */
    @Bean("customKeyGenerator")
    public org.springframework.cache.interceptor.KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder key = new StringBuilder();
            key.append(target.getClass().getSimpleName()).append(":");
            key.append(method.getName()).append(":");

            for (Object param : params) {
                if (param != null) {
                    key.append(param.toString()).append(":");
                }
            }

            return key.toString();
        };
    }
}