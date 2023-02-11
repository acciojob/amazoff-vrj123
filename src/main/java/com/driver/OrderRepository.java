package com.driver;

import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    Map<String, List<String>> partnerOrderDB;
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
        partnerOrderDB.put(id, new ArrayList<>());
        return "Added";
    }
    public String addOrderPartnerPair(String orderId, String partnerId){
//        DeliveryPartner deliveryPartner=partnerDB.get(partnerId);
//        int count=deliveryPartner.getNumberOfOrders();
//        deliveryPartner.setNumberOfOrders(count+1);
//        partnerDB.put(partnerId, deliveryPartner);
        partnerOrderDB.get(partnerId).add(orderId);
        orders--;
        return "Added";
    }
    public Order getOrderById(String orderId){
        return orderDB.getOrDefault(orderId, null);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        return partnerDB.getOrDefault(partnerId, null);
    }
    public int getOrderCountByPartnerId(String partnerId){
        if(partnerOrderDB.containsKey(partnerId)){
            return partnerOrderDB.get(partnerId).size();
        }
        return 0;
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        if (partnerOrderDB.containsKey(partnerId)){
            return partnerOrderDB.get(partnerId);
        }
        return new ArrayList<>();
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
        int m=Integer.valueOf(time.substring(3));
        int t=h*60+m, count=0;
        DeliveryPartner deliveryPartner=partnerDB.get(partnerId);
        List<String> order=partnerOrderDB.get(partnerId);
        for(String o:order){
            if (orderDB.get(o).getDeliveryTime()>t){
                count++;
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        List<String> orderList=partnerOrderDB.get(partnerId);
        int time=0;
        for(String order:orderList){
            time=Math.max(time, orderDB.get(order).getDeliveryTime());
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
        partnerDB.remove(partnerId);
        if(partnerOrderDB.containsKey(partnerId)){
            orders+=partnerOrderDB.get(partnerId).size();
            partnerOrderDB.remove(partnerId);
        }
        return "Deleted";
    }
    public String deleteOrderById(String orderId){
        orderDB.remove(orderId);
        boolean found=false;
        for(String k:partnerOrderDB.keySet()){
            for(String order:partnerOrderDB.get(k)){
                if(order==orderId){
                    partnerOrderDB.get(k).remove(order);
                    found=true;
                    break;
                }
            }
        }
        if(!found){
            orders--;
        }
        return "Deleted";
    }
}
