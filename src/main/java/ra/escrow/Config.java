package ra.escrow;

import ra.common.content.JSON;

import java.util.*;

public class Config extends JSON {

    // InfoVaultDB class
    public String infoVaultDBClass;

    public final Map<String,Currency> supportedCurrencies = new HashMap<>();

    void updateFromProperties(Properties p) {
        if(p.getProperty("ra.common.InfoVaultDB")!=null)
            infoVaultDBClass = p.getProperty("ra.common.InfoVaultDB");
    }

}
