package demo;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class AnalyticsServiceImpl
        extends AnalyticsServiceGrpc.AnalyticsServiceImplBase {

    @Override
    public void analyzeOrder(
            OrderAnalyticsRequest request,
            StreamObserver<OrderAnalyticsResponse> responseObserver
    ) {
        int totalItems = request.getItemsList()
                .stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        int cookingTime = request.getCookingTimeMinutes();

        int complexityScore = totalItems + cookingTime;

        String verdict;
        if (complexityScore <= 50) {
            verdict = "FAST";
        } else if (complexityScore <= 120) {
            verdict = "NORMAL";
        } else {
            verdict = "COMPLEX";
        }

        OrderAnalyticsResponse response =
                OrderAnalyticsResponse.newBuilder()
                        .setOrderId(request.getOrderId())
                        .setTotalItems(totalItems)
                        .setComplexityScore(complexityScore)
                        .setVerdict(verdict)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}