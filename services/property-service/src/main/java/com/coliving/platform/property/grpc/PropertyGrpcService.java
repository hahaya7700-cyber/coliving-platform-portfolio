package com.coliving.platform.property.grpc;

import com.coliving.platform.grpc.property.ListBuildingRoomsRequest;
import com.coliving.platform.grpc.property.ListBuildingRoomsResponse;
import com.coliving.platform.grpc.property.PropertyServiceGrpc;
import com.coliving.platform.property.internal.ListBuildingRoomsService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/**
 * 대시보드가 건물 호실 목록을 조회한다. 포트는 application.yaml의 spring.grpc.server.port (9083).
 *
 * @author 김정호
 * 작성일: 2026-11-04
 */
@GrpcService
public class PropertyGrpcService extends PropertyServiceGrpc.PropertyServiceImplBase {

    private final ListBuildingRoomsService listBuildingRoomsService;

    /** 건물 호실 목록 조회를 주입받는다. */
    public PropertyGrpcService(ListBuildingRoomsService listBuildingRoomsService) {
        this.listBuildingRoomsService = listBuildingRoomsService;
    }

    /** 건물 호실 목록을 돌려준다. 건물이 없으면 NOT_FOUND, 그 밖 실패는 INTERNAL. */
    @Override
    public void listBuildingRooms(ListBuildingRoomsRequest request,
                                  StreamObserver<ListBuildingRoomsResponse> responseObserver) {
        try {
            listBuildingRoomsService.listBuildingRooms(request.getBuildingId())
                    .ifPresentOrElse(response -> {
                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                    }, () -> responseObserver.onError(Status.NOT_FOUND
                            .withDescription("건물을 찾을 수 없습니다.")
                            .asRuntimeException()));
        } catch (RuntimeException ex) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("호실 목록 조회에 실패했습니다.")
                    .asRuntimeException());
        }
    }
}
