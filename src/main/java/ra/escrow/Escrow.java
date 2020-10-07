package ra.escrow;

import ra.common.content.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Escrow extends JSON {

    public Integer id;
    public Integer buyOfferId;
    public Integer sellOfferId;
    public String escrowAddress;
    public Long amount;
    public String currency;
    public String senderAddress;
    public Long timeSent;
    public Integer buyerFees;
    public String receiverAddress;
    public Integer sellerFees;
    public List<Comment> comments = new ArrayList<>();

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = super.toMap();
        if(id!=null) m.put("id", id);
        if(buyOfferId!=null) m.put("buyOfferId", buyOfferId);
        if(sellOfferId!=null) m.put("sellOfferId", sellOfferId);
        if(escrowAddress!=null) m.put("escrowAddress", escrowAddress);
        if(amount!=null) m.put("amount", amount);
        if(currency!=null) m.put("currency", currency);
        if(senderAddress!=null) m.put("senderAddress", senderAddress);
        if(timeSent!=null) m.put("timeSent", timeSent);
        if(buyerFees !=null) m.put("senderFee", buyerFees);
        if(receiverAddress!=null) m.put("receiverAddress", receiverAddress);
        if(sellerFees!=null) m.put("receiverFee", sellerFees);
        if(comments!=null) m.put("comments", comments);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        super.fromMap(m);
        if(m.get("id")!=null) id = (Integer)m.get("id");
        if(m.get("buyOfferId")!=null) buyOfferId = (Integer)m.get("buyOfferId");
        if(m.get("sellOfferId")!=null) sellOfferId = (Integer)m.get("sellOfferId");
        if(m.get("escrowAddress")!=null) escrowAddress = (String)m.get("escrowAddress");
        if(m.get("amount")!=null) amount = (Long)m.get("amount");
        if(m.get("currency")!=null) currency = (String)m.get("currency");
        if(m.get("senderAddress")!=null) senderAddress = (String)m.get("senderAddress");
        if(m.get("timeSent")!=null) timeSent = (Long) m.get("timeSent");
        if(m.get("senderFee")!=null) buyerFees = (Integer) m.get("senderFee");
        if(m.get("receiverAddress")!=null) receiverAddress = (String)m.get("receiverAddress");
        if(m.get("receiverFee")!=null) sellerFees = (Integer) m.get("receiverFee");
        if(m.get("comments")!=null) {
            comments = (List<Comment>)m.get("comments");
        }
    }

}
