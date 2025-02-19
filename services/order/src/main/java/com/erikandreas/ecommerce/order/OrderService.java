package com.erikandreas.ecommerce.order;

import com.erikandreas.ecommerce.customer.CustomerClient;
import com.erikandreas.ecommerce.exception.BusinessException;
import com.erikandreas.ecommerce.kafka.OrderConfirmation;
import com.erikandreas.ecommerce.kafka.OrderProducer;
import com.erikandreas.ecommerce.orderline.OrderLineRequest;
import com.erikandreas.ecommerce.orderline.OrderLineService;
import com.erikandreas.ecommerce.payment.PaymentClient;
import com.erikandreas.ecommerce.payment.PaymentRequest;
import com.erikandreas.ecommerce.product.ProductClient;
import com.erikandreas.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    @Transactional
    public Integer createOrder(OrderRequest request) {
        var customer = this.customerClient
                .findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        var purchasedProducts = productClient.purchaseProducts(request.products());

        Order newOrder = mapper.toOrder(request);
        var order = this.repository.save(newOrder);

        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(new OrderLineRequest(
                    null,
                    order.getId(),
                    purchaseRequest.productId(),
                    purchaseRequest.quantity()
            ));
        }

        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation(new OrderConfirmation(
                request.reference(),
                request.amount(),
                request.paymentMethod(),
                customer,
                purchasedProducts
        ));

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {
        return this.repository
                .findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository
                .findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "No order found with the provided ID: %d",
                        id
                )));
    }
}
