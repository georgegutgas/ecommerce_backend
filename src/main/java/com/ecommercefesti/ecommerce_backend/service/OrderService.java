package com.ecommercefesti.ecommerce_backend.service;

import com.ecommercefesti.ecommerce_backend.dto.*;
import com.ecommercefesti.ecommerce_backend.entity.*;
import com.ecommercefesti.ecommerce_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }


    // 2. Obtener una orden por ID (verificando propiedad o permisos)
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + orderId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Permite acceso solo si es el dueño de la orden o si es ADMIN
        if (!order.getUser().getId().equals(user.getId()) && !"ROLE_ADMIN".equals(String.valueOf(user.getRole()))) {
            throw new RuntimeException("No tienes permisos para consultar esta orden");
        }

        return mapToOrderResponse(order);
    }

    // 3. Cancelar una orden (por parte del cliente)
    @Transactional
    public OrderResponse cancelOrder(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + orderId));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("No tienes permisos para cancelar esta orden");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Solo se pueden cancelar órdenes en estado PENDING");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    // 4. Actualizar estado de la orden (Uso exclusivo Administrador)
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + orderId));

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse createOrder(String userEmail, OrderRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.shippingAddress()) // <--- Asignación del valor
                .shippingCity(request.shippingCity())
                .shippingPostalCode(request.shippingPostalCode())
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + itemReq.productId()));

            // Validación de stock (asumiendo que Product tiene campo 'stock')
            if (product.getStock() != null && product.getStock() < itemReq.quantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            // Descontar stock
            if (product.getStock() != null) {
                product.setStock(product.getStock() - itemReq.quantity());
                productRepository.save(product);
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            total = total.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.quantity())
                    .price(product.getPrice())
                    .build();

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(String userEmail) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    // Método mapeador de Entidad a DTO
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getEmail(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getShippingCity(),
                order.getShippingPostalCode(),

                order.getStatus(),
                order.getCreatedAt(),
                itemResponses
        );
    }

}