package com.velazco.velazco_backend.validation.impl;

import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.repositories.ProductRepository;
import com.velazco.velazco_backend.validation.QuantityAvailable;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class QuantityAvailableValidator implements ConstraintValidator<QuantityAvailable, OrderStartRequestDto> {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public boolean isValid(OrderStartRequestDto order, ConstraintValidatorContext context) {
        if (order.getDetails() == null)
            return true;

        boolean valid = true;

        for (OrderStartRequestDto.DetailOrderStartRequestDto detail : order.getDetails()) {
            if (detail.getProductId() != null && detail.getQuantity() != null) {
                var productOpt = productRepository.findById(detail.getProductId());
                if (productOpt.isEmpty()) {
                    valid = false;
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "El producto con ID: " + detail.getProductId() + " no existe.").addPropertyNode("details")
                            .addConstraintViolation();
                } else {
                    Integer stock = productOpt.get().getStock();
                    if (detail.getQuantity() > stock) {
                        valid = false;
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(
                                "Stock insuficiente para el producto ID: " + detail.getProductId())
                                .addPropertyNode("details")
                                .addConstraintViolation();
                    }
                }
            }
        }
        return valid;
    }
}
