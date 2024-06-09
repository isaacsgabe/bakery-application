package com.reshipies.project.service;


import com.reshipies.project.DTO.EmailToSendBackDTO;
import com.reshipies.project.DTO.IndividualItem;
import com.reshipies.project.controller.OrderController;
import com.reshipies.project.model.CombinedOrder;
import com.reshipies.project.model.Item;
import com.reshipies.project.model.OrderItem;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);


    @Autowired
    private TemplateEngine templateEngine;

//    @Autowired
//    private ApplicationContext;

    @Override
    public void sendSimpleMailMessage(String to, String Subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("reshipesbakery@gmail.com");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(Subject);
        simpleMailMessage.setText(body);
        mailSender.send(simpleMailMessage);
        logger.info("sent email succesfully");
    }

    public void sendHTMLEmail(String to, String subject, CombinedOrder order, Map<Long, Item> bakeryItemMap) {
        try {
            // Prepare the Thymeleaf context with dynamic data
            Context thymeleafContext = new Context();
            EmailToSendBackDTO emailToSendBackDTO = createDTO(order,bakeryItemMap);
            thymeleafContext.setVariable("emailToSendBackDTO", emailToSendBackDTO);
            // Process the Thymeleaf template to generate HTML content
            String htmlContent = templateEngine.process("order-template", thymeleafContext);

            // Create MIME message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("reshipesbakery@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Send email
            mailSender.send(message);
            logger.info("HTML email sent successfully");
        } catch (MessagingException e) {
            logger.error("Failed to send HTML email: " + e.getMessage());
        }
    }

    private EmailToSendBackDTO createDTO(CombinedOrder order, Map<Long, Item> bakeryItemMap) {
        EmailToSendBackDTO emailToSendBackDTO = new EmailToSendBackDTO();
        emailToSendBackDTO.setName(order.getCustomerInfo().getName());
        double totalAmount = 0;
        List<IndividualItem> allItems = new LinkedList<>();
        for (OrderItem i : order.getOrderItems()) {
            IndividualItem individualItem = new IndividualItem();
                    // Check if the item is present in the bakeryItemMap
            String itemName = bakeryItemMap.get(i.getItemID()).getFoodName();
            double price = bakeryItemMap.get(i.getItemID()).getPrice();
            if (itemName == null) {
                bakeryItemMap.clear();
                OrderController oc = new OrderController();
                bakeryItemMap = oc.getAllItemsMap(); // Assuming this method retrieves items and populates bakeryItemMap
                itemName = bakeryItemMap.get(i.getItemID()).getFoodName();
                price = bakeryItemMap.get(i.getItemID()).getPrice();
            }
            double itemTotal = price * i.getQuantity();
            individualItem.setName(itemName);
            individualItem.setItemTotal(itemTotal);
            individualItem.setPrice(price);
            individualItem.setQuantity(i.getQuantity());
            totalAmount += itemTotal;
            allItems.add(individualItem);
        }
        emailToSendBackDTO.setTotalAmount(totalAmount);
        emailToSendBackDTO.setAllItems(allItems);
        return emailToSendBackDTO;
    }
}
