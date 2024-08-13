package org.teamchallenge.bookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.teamchallenge.bookshop.dto.OrderDeliveryDto;
import org.teamchallenge.bookshop.dto.OrderDto;
import org.teamchallenge.bookshop.dto.OrderResponse;
import org.teamchallenge.bookshop.enums.OrderStatus;
import org.teamchallenge.bookshop.service.OrderService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/api/nova-post-order")
    @Operation(summary = "Create a new order", description = "Creates a new order with the provided delivery details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request if required fields are missing"),
            @ApiResponse(responseCode = "500", description = "Internal server error if something goes wrong")
    })
    public ResponseEntity<?> createOrder(
            @RequestBody
            @Parameter(description = "Delivery details for the order") OrderDeliveryDto orderDeliveryDto) {
        try {
            if (orderDeliveryDto.getCity() == null || orderDeliveryDto.getFirstName() == null || orderDeliveryDto.getLastName() == null) {
                return ResponseEntity.badRequest().body("Не всі обов'язкові поля заповнені");
            }

            String orderNumber = "ORDER-" + UUID.randomUUID();
            String trackingNumber = orderService.createOrder(orderDeliveryDto, orderNumber);

            return ResponseEntity.ok(new OrderResponse(orderNumber, trackingNumber));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Помилка при створенні замовлення: " + e.getMessage());
        }
    }

    @GetMapping("/statuses")
    @Operation(summary = "Get all order statuses", description = "Retrieves all possible order statuses.")
    @ApiResponse(responseCode = "200", description = "List of order statuses")
    public ResponseEntity<List<OrderStatus>> getOrderStatuses() {
        List<OrderStatus> orderStatuses = Arrays.asList(OrderStatus.values());
        return ResponseEntity.ok(orderStatuses);
    }

    @GetMapping("/findById/{id}")
    @Operation(summary = "Get an order by ID", description = "Retrieves an order by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable
            @Parameter(description = "ID of the order to retrieve") Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete an order by ID", description = "Deletes an order by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> deleteOrderById(
            @PathVariable
            @Parameter(description = "ID of the order to delete") Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update an order by ID", description = "Updates an order with new information.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Bad request if invalid data is provided")
    })
    public ResponseEntity<OrderDto> updateOrderById(
            @PathVariable
            @Parameter(description = "ID of the order to update") Long id,
            @RequestBody
            @Parameter(description = "Updated order information") OrderDto orderDto) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDto));
    }
}
