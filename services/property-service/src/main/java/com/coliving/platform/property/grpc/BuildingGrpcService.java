package com.coliving.platform.property.grpc;

import com.coliving.platform.grpc.property.BuildingServiceGrpc;
import com.coliving.platform.grpc.property.GetBuildingsRequest;
import com.coliving.platform.grpc.property.GetBuildingsResponse;
import com.coliving.platform.property.internal.GetBuildingsService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/**
 * 커뮤니티용 건물 목록 gRPC. 포트는 application.yaml의 spring.grpc.server.port (9083).
 *
 * @author 김정호
 * 작성일: 2026-11-04
 */
@GrpcService
public class BuildingGrpcService extends BuildingServiceGrpc.BuildingServiceImplBase {

    private final GetBuildingsService getBuildingsService;

    /** 건물 목록 조회를 주입받는다. */
    public BuildingGrpcService(GetBuildingsService getBuildingsService) {
        this.getBuildingsService = getBuildingsService;
    }

    /** 삭제되지 않은 건물 목록을 돌려준다. 실패하면 INTERNAL. */
    @Override
    public void getBuildings(GetBuildingsRequest request, StreamObserver<GetBuildingsResponse> responseObserver) {
        try {
            responseObserver.onNext(getBuildingsService.getBuildings());
            responseObserver.onCompleted();
        } catch (RuntimeException ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("건물 목록 조회에 실패했습니다.").asRuntimeException());
        }
    }
}
