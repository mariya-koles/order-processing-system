package com.platform.ops.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Order {

    @NotBlank(message = "Order ID must not be blank")
    private String orderId;
    @NotBlank(message = "Customer name must not be blank")
    private String customerName;

    @NotBlank(message = "Product name must not be blank")
    @Size(min = 3, message = "Product name must be at least 3 characters")
    private String product;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private double price;
}
