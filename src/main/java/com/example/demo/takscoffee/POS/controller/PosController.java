package com.example.demo.takscoffee.POS.controller;


import com.example.demo.takscoffee.POS.dto.OrderRequest;
import com.example.demo.takscoffee.POS.model.Order;
import com.example.demo.takscoffee.POS.model.OrderItem;
import com.example.demo.takscoffee.POS.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class PosController {

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/checkout")
    public ResponseEntity<Order> processCheckout(@RequestBody OrderRequest request) {
        Order newOrder = new Order();
        newOrder.setCustomerName(request.getCustomerName());
        newOrder.setPaymentMethod(request.getPaymentMethod());
        newOrder.setSubtotal(request.getSubtotal());
        newOrder.setTaxAmount(request.getTax());
        newOrder.setTotalAmount(request.getTotalAmount());

        // Membuat list untuk teks ringkasan
        java.util.List<String> summaryList = new java.util.ArrayList<>();

        for (OrderRequest.CartItem item : request.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(item.getName());
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQty());
            orderItem.setOrder(newOrder); 
            
            newOrder.getItems().add(orderItem);
            
            // Masukkan ke teks ringkasan (Contoh: "2x Americano")
            summaryList.add(item.getQty() + "x " + item.getName());
        }

        // Gabungkan dengan koma dan simpan ke database
        newOrder.setItemsSummary(String.join(", ", summaryList));

        Order savedOrder = orderRepository.save(newOrder);
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Order>> getOrderHistory() {
        return ResponseEntity.ok(orderRepository.findAll());
    }
}