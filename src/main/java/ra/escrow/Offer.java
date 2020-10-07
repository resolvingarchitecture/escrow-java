package ra.escrow;

import ra.common.content.JSON;

import java.util.Map;

public class Offer extends JSON {

    public Integer id;
    public Boolean isBuy; // true=buy false=sell
    public String instructions;
    public Float latitude;
    public Float longitude;
    public Long amount;
    public String currency;
    public Long amountSats; // amount of Bitcoin to buy/sell in Satoshis
    public Integer minerFeeSats; // Bitcoin blockchain fee - estimated
    public Integer txFeeSats; // Trading fee
    public Integer volatilityFloatSats; // Float to take into consideration fiat/Bitcoin volatility and blockchain fee estimation volatility; excess refunded
    public Long totalFundingSats; // amountSats + minerFeeSats + txFeeSats + volatilityFloatSats
    public Integer matchedId; // Matched Offer
    public Status status = Status.OFFERED;

    public enum Status {OFFERED, MATCHED, LOCKED}

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = super.toMap();
        if(id!=null) m.put("id", id);
        if(isBuy!=null) m.put("isBuy", isBuy?1:0);
        if(instructions!=null) m.put("instructions", instructions);
        if(latitude!=null) m.put("latitude", latitude.toString());
        if(longitude!=null) m.put("longitude", longitude.toString());
        if(amount!=null) m.put("amount", amount);
        if(currency!=null) m.put("currency", currency);
        if(amountSats!=null) m.put("amountSats", amountSats);
        if(minerFeeSats!=null) m.put("minerFeeSats", minerFeeSats);
        if(txFeeSats!=null) m.put("txFeeSats", txFeeSats);
        if(volatilityFloatSats !=null) m.put("volatilityFloatSats", volatilityFloatSats);
        if(totalFundingSats!=null) m.put("totalFundingSats", totalFundingSats);
        if(matchedId!=null) m.put("matchedId", matchedId);
        if(status!=null) m.put("status", status.name());
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        super.fromMap(m);
        if(m.get("id")!=null) id = (Integer)m.get("id");
        if(m.get("isBuy")!=null) isBuy = ((Integer)m.get("isBuy"))==1;
        if(m.get("instructions")!=null) instructions = (String)m.get("instructions");
        if(m.get("latitude")!=null) latitude = Float.parseFloat((String)m.get("latitude"));
        if(m.get("longitude")!=null) longitude = Float.parseFloat((String)m.get("longitude"));
        if(m.get("amount")!=null) amount = (Long)m.get("amount");
        if(m.get("currency")!=null) currency = (String)m.get("currency");
        if(m.get("amountSats")!=null) amountSats = (Long)m.get("amountSats");
        if(m.get("minerFeeSats")!=null) minerFeeSats = (Integer)m.get("minerFeeSats");
        if(m.get("txFeeSats")!=null) txFeeSats = (Integer)m.get("txFeeSats");
        if(m.get("volatilityFloatSats")!=null) volatilityFloatSats = (Integer)m.get("volatilityFloatSats");
        if(m.get("totalFundingSats")!=null) totalFundingSats = (Long)m.get("totalFundingSats");
        if(m.get("matchedId")!=null) matchedId = (Integer)m.get("matchedId");
        if(m.get("status")!=null) status = Status.valueOf((String)m.get("status"));
    }

}
