package ra.escrow;

import ra.common.content.JSON;

import java.util.Map;

public class Comment extends JSON {

    public Integer id;
    public Integer escrowId;
    public Boolean isBuyer;
    public String comment;

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        if(id!=null) m.put("id", id);
        if(escrowId!=null) m.put("escrowId", escrowId);
        if(isBuyer!=null) m.put("isBuyer", isBuyer ? 1 : 0);
        if(comment!=null) m.put("comment", comment);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        super.fromMap(m);
        if(m.get("id")!=null) id = Integer.parseInt((String)m.get("id"));
        if(m.get("escrowId")!=null) escrowId = Integer.parseInt((String)m.get("escrowId"));
        if(m.get("isBuyer")!=null) isBuyer = Integer.parseInt((String)m.get("isBuyer")) == 1;
        if(m.get("comment")!=null) comment = (String)m.get("comment");
    }
}
