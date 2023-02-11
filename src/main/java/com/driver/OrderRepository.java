package com.driver;

import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    Map<DeliveryPartner, List<Order>> partnerOrderDB;
    Map<String, Order> orderDB;
    Map<String, DeliveryPartner> partnerDB;
    int orders;
    public OrderRepository(){
        partnerOrderDB=new HashMap<>();
        orderDB=new HashMap<>();
        partnerDB=new HashMap<>();
        orders=0;
    }

    public String addOrder(Order order){
        String id=order.getId();
        orderDB.put(id, order);
        orders++;
        return "Added";
    }
    public String addPartner(String id){
        DeliveryPartner deliveryPartner=new DeliveryPartner(id);
        partnerDB.put(id, deliveryPartner);
        partnerOrderDB.put(deliveryPartner, new ArrayList<>());
        return "Added";
    }
    public String addOrderPartnerPair(String orderId, String partnerId){
        DeliveryPartner deliveryPartner=partnerDB.get(partnerId);
        int count=deliveryPartner.getNumberOfOrders();
        deliveryPartner.setNumberOfOrders(count+1);
        partnerDB.put(partnerId, deliveryPartner);
        orders--;
        Order order=orderDB.get(orderId);
        partnerOrderDB.get(deliveryPartner).add(order);
        return "Added";
    }
    public Order getOrderById(String orderId){
        return orderDB.get(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        return partnerDB.get(partnerId);
    }
    public int getOrderCountByPartnerId(String partnerId){
        return partnerDB.get(partnerId).getNumberOfOrders();
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        DeliveryPartner deliveryPartner=partnerDB.get(partnerId);
        List<String> order=new ArrayList<>();
        for(Order o:partnerOrderDB.get(deliveryPartner)){
            order.add(o.getId());
        }
        return order;
    }
    public List<String> getAllOrders(){
        List<String> orderList=new ArrayList<>();
        for(String k:orderDB.keySet()){
            orderList.add(k);
        }
        return orderList;
    }
    public int getCountOfUnassignedOrders(){
        return orders;
    }
    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int h=Integer.valueOf(time.substring(0, 2));
        int m=Integer.valueOf(time.substring(2));
        int t=h*60+m, count=0;
        DeliveryPartner deliveryPartner=partnerDB.get(partnerId);
        List<Order> orderList=partnerOrderDB.get(deliveryPartner);
        for(Order order:orderList){
            if(order.getDeliveryTime()>t){
                count++;
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        DeliveryPartner deliveryPartner=partnerDB.get(partnerId);
        List<Order> orderList=partnerOrderDB.get(deliveryPartner);
        int time=0;
        for(Order order:orderList){
            time=Math.max(time, order.getDeliveryTime());
        }
        int h=time/60;
        String HH="";
        if(h<10){
            HH="0"+h;
        }
        else{
            HH=String.valueOf(h);
        }
        int m=time%60;
        String MM="";
        if(m<10){
            MM="0"+m;
        }
        else{
            MM=String.valueOf(m);
        }
        return HH+":"+MM;
    }
    public String deletePartnerById(String partnerId){
        DeliveryPartner deliveryPartner=partnerDB.get(partnerId);
        partnerDB.remove(partnerId);
        orders+=partnerOrderDB.get(deliveryPartner).size();
        partnerOrderDB.remove(deliveryPartner);
        return "Deleted";
    }
    public String deleteOrderById(String orderId){
        DeliveryPartner deliveryPartner=null;
        Order o=null;
        for(DeliveryPartner k:partnerOrderDB.keySet()){
            for (Order order:partnerOrderDB.get(k)){
                if(order.getId()==orderId){
                    o=order;
                    deliveryPartner=k;
                    break;
                }
            }
        }
        if (deliveryPartner!=null){
            partnerOrderDB.get(deliveryPartner).remove(o);
        }
        else{
            orders--;
        }
        return "Deleted";
    }
}
