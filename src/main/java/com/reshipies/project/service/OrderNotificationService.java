package com.reshipies.project.service;

import com.reshipies.project.model.CombinedOrder;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;

public class OrderNotificationService {

    private final TemplateEngine templateEngine;

    public OrderNotificationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String generateOrderNotificationHtml(CombinedOrder order) {
        // Create a Thymeleaf context and add dynamic data
        Context context = new Context();
        context.setVariable("order", order);

        // Process the template with dynamic data
        return templateEngine.process("order_notification", context);
    }
}
