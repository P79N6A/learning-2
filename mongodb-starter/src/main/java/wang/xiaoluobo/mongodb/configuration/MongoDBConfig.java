package wang.xiaoluobo.mongodb.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import javax.annotation.PreDestroy;
import java.net.UnknownHostException;

/**
 * mongodb configuration
 * @author wangyd
 * @date 2018/7/19 下午2:10
 */
@Configuration
@ConditionalOnClass({MongoClient.class})
@ConditionalOnMissingBean(
        type = {"org.springframework.data.mongodb.MongoDbFactory"}
)
@EnableConfigurationProperties(MongoDBProperties.class)
public class MongoDBConfig {

    private final MongoDBProperties properties;
    private final MongoClientOptions options;
    private final Environment environment;
    private MongoClient mongo;

    public MongoDBConfig(MongoDBProperties properties, ObjectProvider<MongoClientOptions> options, Environment environment) {
        this.properties = properties;
        this.options = options.getIfAvailable();
        this.environment = environment;
    }

    @PreDestroy
    public void close() {
        if (this.mongo != null) {
            this.mongo.close();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoClient mongo() throws UnknownHostException {
        this.mongo = this.properties.createMongoClient(this.options, this.environment);
        return this.mongo;
    }

    @Bean
    @ConditionalOnMissingBean({MongoDbFactory.class})
    public SimpleMongoDbFactory mongoDbFactory(MongoClient mongo) {
        String database = this.properties.getMongoClientDatabase();
        return new SimpleMongoDbFactory(mongo, database);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(mongoDbFactory, converter);
    }
}
