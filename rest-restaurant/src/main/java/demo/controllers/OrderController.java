package demo.controllers;


import demo.assemblers.OrderModelAssembler;
import demo.dto.OrderRequest;
import demo.dto.OrderResponse;
import demo.dto.PagedResponse;
import demo.endpoints.OrderApi;
import demo.services.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController implements OrderApi {

    private final OrderModelAssembler orderModelAssembler;
    private final PagedResourcesAssembler pagedResourcesAssembler;
    private final OrderService orderService;

    public OrderController(OrderModelAssembler orderModelAssembler, PagedResourcesAssembler pagedResourcesAssembler, OrderService orderService) {
        this.orderModelAssembler = orderModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.orderService = orderService;
    }

    @Override
    public PagedModel<EntityModel<OrderResponse>> getAllOrders(Long orderId, int page, int size) {
        PagedResponse<OrderResponse> pagedResponse = orderService.findAllOrders(orderId, page, size);
        Page<OrderResponse> bookPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return pagedResourcesAssembler.toModel(bookPage, orderModelAssembler);
    }

    @Override
    public EntityModel<OrderResponse> getOrderById (Long id){
        OrderResponse order = orderService.findOrderById(id);
        return orderModelAssembler.toModel(order);
    }

    @Override
    public ResponseEntity<EntityModel<OrderResponse>> createOrder(OrderRequest request) {
        OrderResponse created = orderService.createOrder(request);
        EntityModel<OrderResponse> entityModel = orderModelAssembler.toModel(created);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<OrderResponse> updateOrder(Long id, OrderRequest request) {
        OrderResponse updated = orderService.updateOrder(id, request);
        return orderModelAssembler.toModel(updated);
    }

    @Override
    public EntityModel<OrderResponse> updateOrderStatus(Long id, String status) {
        OrderResponse updated = orderService.updateOrderStatus(id, status);
        return orderModelAssembler.toModel(updated);
    }


    @Override
    public void deleteOrder(Long id){
        orderService.deleteOrder(id);
    }
}

