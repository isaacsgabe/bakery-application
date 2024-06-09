package com.reshipies.project.service;

import com.reshipies.project.model.CombinedOrder;
import com.reshipies.project.model.Item;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface EmailService {

    public void sendSimpleMailMessage(String to,  String Subject, String token);
    public void sendHTMLEmail(String to,String subject, CombinedOrder order, Map<Long, Item> bakeryItemMap);
}
