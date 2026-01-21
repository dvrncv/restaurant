package demo;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class AnalyticsServiceImpl extends AnalyticsServiceGrpc.AnalyticsServiceImplBase {

    @Override
    public void analyzeOrder(
            OrderAnalyticsRequest request,
            StreamObserver<OrderAnalyticsResponse> responseObserver
    ) {
        int totalItems = request.getItemsList()
                .stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        int dishCount = request.getItemsList().size();
        int cookingTimeMinutes = request.getCookingTimeMinutes();

        int averageTimePerDish = dishCount > 0
                ? cookingTimeMinutes / dishCount
                : 0;

        int complexityScore = totalItems + cookingTimeMinutes;

        List<String> recommendations = generateRecommendations(
                cookingTimeMinutes,
                totalItems,
                averageTimePerDish,
                dishCount
        );

        OrderAnalyticsResponse response =
                OrderAnalyticsResponse.newBuilder()
                        .setOrderId(request.getOrderId())
                        .setOrderStartTime(request.getOrderCreated())
                        .setOrderReadyTime(request.getOrderCompleted())
                        .setDishCount(dishCount)
                        .setTotalItems(totalItems)
                        .setComplexityScore(complexityScore)
                        .setAverageTimePerDish(averageTimePerDish)
                        .addAllRecommendations(recommendations)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private List<String> generateRecommendations(
            int cookingTimeMinutes,
            int totalItems,
            int averageTimePerDish,
            int dishCount
    ) {
        List<String> recommendations = new ArrayList<>();

        if (cookingTimeMinutes > 60) {
            recommendations.add("Заказ готовился слишком долго. Стоит ускорить процесс.");
        } else if (cookingTimeMinutes > 45) {
            recommendations.add("Можно ускорить готовку, если заранее подготовить ингредиенты.");
        } else if (cookingTimeMinutes <= 20) {
            recommendations.add("Заказ приготовлен быстро.");
        }

        if (totalItems > 15) {
            recommendations.add("Большой заказ. Проверьте, хватает ли сотрудников на кухне.");
        } else if (totalItems > 10) {
            recommendations.add("Заказ среднего размера. Обычной загрузки кухни достаточно.");
        }

        if (averageTimePerDish > 20) {
            recommendations.add("Некоторые блюда готовятся долго. Возможно, они слишком сложные.");
        } else if (averageTimePerDish <= 10) {
            recommendations.add("Блюда готовятся быстро, кухня хорошо справляется.");
        }

        if (dishCount > 10) {
            recommendations.add("В заказе много разных блюд. Проверьте запасы ингредиентов.");
        }

        if (totalItems > 15 && cookingTimeMinutes > 40) {
            recommendations.add("Заказ большой и долгий. Лучше готовить блюда параллельно.");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("С заказом всё в порядке.");
        }

        return recommendations;
    }
}
