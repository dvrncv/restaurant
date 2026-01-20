package demo;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

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
        List<String> recommendations = new java.util.ArrayList<>();

        if (cookingTimeMinutes > 60) {
            recommendations.add(
                    "Общее время приготовления превышает 60 минут. Рекомендуется оптимизировать процесс приготовления."
            );
        } else if (cookingTimeMinutes > 45) {
            recommendations.add(
                    "Рекомендуется заранее подготавливать ингредиенты для сокращения времени приготовления."
            );
        } else if (cookingTimeMinutes <= 20) {
            recommendations.add(
                    "Отличная скорость приготовления заказа."
            );
        }

        if (totalItems > 15) {
            recommendations.add(
                    "Большой объем заказа. Убедитесь, что на кухне достаточно персонала."
            );
        } else if (totalItems > 10) {
            recommendations.add(
                    "Средний объем заказа. Стандартной загрузки кухни должно быть достаточно."
            );
        }

        if (averageTimePerDish > 20) {
            recommendations.add(
                    "Обнаружены сложные блюда. Рассмотрите возможность добавления более простых позиций в меню."
            );
        } else if (averageTimePerDish <= 10) {
            recommendations.add(
                    "Блюда готовятся быстро. Подходит для высокой оборачиваемости заказов."
            );
        }

        if (dishCount > 10) {
            recommendations.add(
                    "Заказ содержит большое разнообразие блюд. Проверьте наличие ингредиентов на складе."
            );
        }

        if (totalItems > 15 && cookingTimeMinutes > 40) {
            recommendations.add(
                    "Рекомендуется использовать параллельное приготовление для ускорения выполнения заказа."
            );
        }

        if (recommendations.isEmpty()) {
            recommendations.add(
                    "Параметры заказа находятся в оптимальных пределах."
            );
        }

        return recommendations;
    }
}
