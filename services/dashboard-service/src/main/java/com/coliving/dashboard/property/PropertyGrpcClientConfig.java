package com.coliving.dashboard.property;

import com.coliving.platform.grpc.property.PropertyServiceGrpc;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ImportGrpcClients;

/**
 * 매물 서비스 stub 등록.
 * target = "property" → application.yaml의 spring.grpc.client.channel.property.* (localhost:9083).
 * @author 조휘재
 * @version 1.0
 * @since 2026-11-12
 */
@Configuration
@ImportGrpcClients(target = "property", types = PropertyServiceGrpc.PropertyServiceBlockingStub.class)
public class PropertyGrpcClientConfig {
}
