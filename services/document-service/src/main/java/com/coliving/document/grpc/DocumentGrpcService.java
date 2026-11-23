package com.coliving.document.grpc;

import com.coliving.document.form.repository.DocumentFormRepository;
import com.coliving.platform.grpc.document.DocumentServiceGrpc;
import com.coliving.platform.grpc.document.GetDocumentFormRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

/**
 * 계약 서비스가 양식 본문을 가져갈 때 쓰는 gRPC 서비스 구현체
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
@Service
public class DocumentGrpcService extends DocumentServiceGrpc.DocumentServiceImplBase {

    /* 양식 저장소 */
    private final DocumentFormRepository formRepository;

    /* 양식 저장소 주입 */
    public DocumentGrpcService(DocumentFormRepository formRepository) {
        this.formRepository = formRepository;
    }

    /* 템플릿 ID로 계약서 양식 한 건 조회 (없으면 NOT_FOUND) */
    @Override
    public void getDocumentForm(GetDocumentFormRequest request,
                                StreamObserver<com.coliving.platform.grpc.document.DocumentForm> responseObserver) {
        formRepository.findById(request.getTemplateId()).ifPresentOrElse(
                form -> {
                    responseObserver.onNext(com.coliving.platform.grpc.document.DocumentForm.newBuilder()
                            .setTemplateId(form.getId())
                            .setLanguage(form.getLanguage())
                            .setContent(form.getContent())
                            .build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(Status.NOT_FOUND
                        .withDescription("존재하지 않는 계약서 양식입니다. templateId=" + request.getTemplateId())
                        .asRuntimeException())
        );
    }
}