package ra.escrow;

import ra.common.DLC;
import ra.common.Envelope;
import ra.common.InfoVault;
import ra.common.InfoVaultDB;
import ra.common.route.Route;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatus;

import java.util.Properties;
import java.util.logging.Logger;

public class EscrowService extends BaseService {

    private static final Logger LOG = Logger.getLogger(EscrowService.class.getName());

    // Config
    public static final String OPERATION_GET_CONFIG = "GET_CONFIG";
    public static final String OPERATION_SET_CONFIG = "SET_CONFIG";

    // Stats
    public static final String OPERATION_GET_STATS = "GET_STATS";
    public static final String OPERATION_GET_STATUS = "GET_STATUS";

    // Buy
    public static final String OPERATION_SUBMIT_BUY_OFFER = "SUBMIT_BUY_OFFER"; // (1)
    public static final String OPERATION_CHECK_BUY_OFFER_STATUS = "CHECK_BUY_OFFER_STATUS"; // (2)
    public static final String OPERATION_ACCEPT_SELL_OFFER = "ACCEPT_SELL_OFFER"; // (3)
    public static final String OPERATION_SETTLE_INSTRUCTIONS = "SETTLE_INSTRUCTIONS"; // (4)
    public static final String OPERATION_VERIFY_FUNDS = "VERIFY_FUNDS"; // (5)

    // Sell
    public static final String OPERATION_SUBMIT_SELL_OFFER = "SUBMIT_SELL_OFFER"; // (1)
    public static final String OPERATION_CHECK_SELL_OFFER_STATUS = "CHECK_SELL_OFFER_STATUS"; // (2)
    public static final String OPERATION_FUND_ESCROW = "FUND_ESCROW"; // (3)
    public static final String OPERATION_INSTRUCTIONS_SETTLED = "INSTRUCTIONS_SETTLED"; // (4)
    public static final String OPERATION_VERIFY_ESCROW_CLOSED = "VERIFY_ESCROW_CLOSED"; // (5)

    // Admin
    public static final String OPERATION_SET_STATIC_ESCROW_ACCOUNT = "SET_STATIC_ESCROW_ACCOUNT";
    public static final String OPERATION_GET_ESCROWS = "GET_ESCROWS";
    public static final String OPERATION_CLOSE_ESCROW = "CLOSE_ESCROW";
    public static final String OPERATION_GET_DISTRIBUTABLE_REVENUE = "GET_DISTRIBUTABLE_REVENUE";
    public static final String OPERATION_DISTRIBUTE_REVENUE = "DISTRIBUTE_REVENUE";
    public static final String OPERATION_PAUSE = "PAUSE";
    public static final String OPERATION_UNPAUSE = "UNPAUSE";

    private Config config;
    private InfoVaultDB infoVaultDB;
    private final Stats stats = new Stats();

    // Fees
    private Integer btcFeesEst;
    private Integer txFeesPerc;

    // Escrow
    private String escrowAddress;
    private Integer escrowMaxLockHours;

    @Override
    public void handleDocument(Envelope e) {
        Route r = e.getRoute();
        LOG.info("Operation received: "+r.getOperation());
        switch (r.getOperation()) {
            case OPERATION_GET_CONFIG:{getConfig(e);break;}
            case OPERATION_SET_CONFIG:{setConfig(e);break;}
            case OPERATION_GET_STATS:{getStats(e);break;}
            case OPERATION_GET_STATUS: {getStatus(e);break;}
            case OPERATION_SUBMIT_BUY_OFFER:{submitBuyOffer(e);break;}
            case OPERATION_CHECK_BUY_OFFER_STATUS:{checkBuyOfferStatus(e);break;}
            case OPERATION_CHECK_SELL_OFFER_STATUS:{checkSellOfferStatus(e);break;}
            case OPERATION_ACCEPT_SELL_OFFER:{acceptSellOffer(e);break;}
            case OPERATION_SETTLE_INSTRUCTIONS:{settleInstructions(e);break;}
            case OPERATION_VERIFY_FUNDS:{verifyFunds(e);break;}
            case OPERATION_SUBMIT_SELL_OFFER:{submitSellOffer(e);break;}
            case OPERATION_FUND_ESCROW:{fundEscrow(e);break;}
            case OPERATION_INSTRUCTIONS_SETTLED:{instructionsSettled(e);break;}
            case OPERATION_VERIFY_ESCROW_CLOSED:{verifyEscrowClosed(e);break;}
            case OPERATION_SET_STATIC_ESCROW_ACCOUNT:{setStaticEscrowAccount(e);break;}
            case OPERATION_GET_ESCROWS:{getEscrows(e);break;}
            case OPERATION_CLOSE_ESCROW:{closeEscrow(e);break;}
            case OPERATION_GET_DISTRIBUTABLE_REVENUE:{getDistributableRevenue(e);break;}
            case OPERATION_DISTRIBUTE_REVENUE:{distributeRevenue(e);break;}
            case OPERATION_PAUSE:{pause();break;}
            case OPERATION_UNPAUSE:{unpause();break;}
            default: {
                LOG.warning("Unsupported Operation: "+r.getOperation());
                e.getMessage().addErrorMessage("Unsupported Operation: "+r.getOperation());
                return;
            }
        }
    }

    private void getConfig(Envelope e) {
        DLC.addContent(config, e);
    }

    private void setConfig(Envelope e){
        config = (Config)DLC.getContent(e);
        if(config==null) {
            LOG.warning("Config not present in Envelope. Unable to save.");
            return;
        }
        InfoVault iv = new InfoVault();
        iv.content = config;
        if(!infoVaultDB.save(iv)) {
            LOG.warning("Config unable to save.");
        }
    }

    private void getStats(Envelope e) {
        DLC.addContent(stats,e);
    }

    private void saveStats() {
        InfoVault iv = new InfoVault();
        iv.content = stats;
        if(!infoVaultDB.save(iv)){
            LOG.warning("Stats unable to save.");
        }
    }

    private void getStatus(Envelope e) {
        DLC.addContent(getServiceStatus().name(), e);
    }


    /**
     * Buy BTC
     * Step 1: Make Buy Offer
     * @param e
     */
    private void submitBuyOffer(Envelope e) {
        Offer buyOffer = new Offer();
        buyOffer.fromMap(DLC.getValues(e));

        InfoVault infoVault = new InfoVault();
        infoVault.content = buyOffer;
        infoVault.autoCreate = true;
        infoVaultDB.save(infoVault);

        // Response
        DLC.addNVP("buyOfferId", buyOffer.id, e);
    }

    /**
     * Buy BTC
     * Step 2: Check to see if there's a Sell Offer we can Match with Buy Offer
     * and if so, return Sell Offer. Can only auto-match Offers not location-dependent
     * (latitude/longitude not set).
     * @param e
     */
    private void checkBuyOfferStatus(Envelope e) {

        Offer buyOffer = new Offer();
        buyOffer.fromMap(DLC.getValues(e));
        if(buyOffer.latitude!=null || buyOffer.longitude!=null) {

        }

        InfoVault ivBuy = new InfoVault();
        ivBuy.content = buyOffer;
        infoVaultDB.load(ivBuy);

        Offer offer = new Offer();
        offer.amount = buyOffer.amount;
        offer.currency = buyOffer.currency;

        InfoVault ivSell = new InfoVault();
        ivSell.content = offer;
        infoVaultDB.load(ivSell);

        if(offer.id != null) {
            // Response
            DLC.addNVP("sellOfferId", offer.toJSON(), e);
        }

    }

    /**
     * Buy BTC
     * Step 3: Accept Sell Offer locking in Escrow
     * @param e
     */
    private void acceptSellOffer(Envelope e) {
        Integer buyOfferId = (Integer)DLC.getValue("buyOfferId", e);
        Integer sellOfferId = (Integer)DLC.getValue("sellOfferId", e);

        Offer buyOffer = new Offer();
        buyOffer.id = buyOfferId;

        InfoVault ivBuy = new InfoVault();
        ivBuy.content = buyOffer;
        infoVaultDB.load(ivBuy);

        Offer offer = new Offer();
        offer.id = sellOfferId;

        InfoVault ivSell = new InfoVault();
        ivSell.content = offer;
        infoVaultDB.load(ivSell);

        Escrow escrow = new Escrow();
        escrow.buyOfferId = buyOfferId;
        escrow.sellOfferId = sellOfferId;
        escrow.currency = offer.currency;
        escrow.amount = offer.amount;
        // Must set Escrow Address via Admin prior to use
//        escrow.escrowAddress = config.escrowAddress;
//        escrow.senderAddress = sellOffer.

    }

    private void settleInstructions(Envelope e) {


        InfoVault infoVault = new InfoVault();
    }

    private void verifyFunds(Envelope e) {
        String escrowId = (String)DLC.getValue("escrowId", e);

        InfoVault infoVault = new InfoVault();
    }

    /**
     * Sell BTC
     * Step 1: Submit Offer to Sell BTC for fiat providing fiat amount, fiat currency symbol,
     * and any additional information needed based on type of Offer.
     * @param e
     */
    private void submitSellOffer(Envelope e) {
        String amountStr = (String)DLC.getValue("fiatAmount", e);
        String currencyStr = (String)DLC.getValue("fiatCurrencySymbol", e);
        String ticketNameStr = (String)DLC.getValue("ticketName", e);
        Offer buyOffer = new Offer();

        InfoVault infoVault = new InfoVault();
    }

    private void checkSellOfferStatus(Envelope e) {
        Integer offerId = (Integer)DLC.getValue("offerId", e);

        Offer offer = new Offer();
        offer.id = offerId;
        InfoVault infoVault = new InfoVault();
        infoVault.content = offer;
        infoVaultDB.load(infoVault);
        DLC.addNVP("offer", offer.toJSON(), e);
        if(offer.status==Offer.Status.MATCHED) {
            Escrow escrow = new Escrow();
            escrow.sellOfferId = offer.id;
            InfoVault ivEscrow = new InfoVault();
            ivEscrow.content = escrow;
            infoVaultDB.load(ivEscrow);
            DLC.addNVP("escrow", escrow.toJSON(), e);
        }
    }

    private void fundEscrow(Envelope e) {
        String escrowId = (String)DLC.getValue("escrowId", e);

        InfoVault infoVault = new InfoVault();
    }

    private void instructionsSettled(Envelope e) {
        String escrowId = (String)DLC.getValue("escrowId", e);

        InfoVault infoVault = new InfoVault();
    }

    private void verifyEscrowClosed(Envelope e) {
        String escrowId = (String)DLC.getValue("escrowId", e);

        InfoVault infoVault = new InfoVault();
    }

    private void setStaticEscrowAccount(Envelope e) {
        String btcAddress = (String)DLC.getValue("btcAddress", e);

        InfoVault infoVault = new InfoVault();
    }

    private void getEscrows(Envelope e) {

    }

    private void closeEscrow(Envelope e) {
        String escrowId = (String)DLC.getValue("escrowId", e);

        InfoVault infoVault = new InfoVault();
    }

    private void getDistributableRevenue(Envelope e) {

    }

    private void distributeRevenue(Envelope e) {
        String btcAmountSatsStr = (String)DLC.getValue("btcAmountSats", e);
        String btcAddress1 = (String)DLC.getValue("btcAddress1", e);
        String btcAddress2 = (String)DLC.getValue("btcAddress2", e);

        InfoVault infoVault = new InfoVault();
    }

    @Override
    protected void updateStatus(ServiceStatus serviceStatus) {
        super.updateStatus(serviceStatus);
        stats.setStatus(serviceStatus.name());
    }

    @Override
    public boolean start(Properties p) {
        LOG.info("Starting...");
        updateStatus(ServiceStatus.STARTING);
        if(super.start(p)) {
            try {
                p = ra.util.Config.loadFromClasspath("ra-escrow.config", p, false);
            } catch (Exception e) {
                LOG.severe(e.getLocalizedMessage());
                return false;
            }
            infoVaultDB = (InfoVaultDB)p.get(InfoVaultDB.DB);
            config = new Config();
            InfoVault iv = new InfoVault();
            iv.content = config;
            iv.content.setLocation(getServiceDirectory().getAbsolutePath()+"/config.json");
            if(!infoVaultDB.load(iv)) {
                config.updateFromProperties(p);
                infoVaultDB.save(iv);
            }
            LOG.info("Config loaded: "+config.toJSON());

            infoVaultDB = (InfoVaultDB)p.get("ra.infovault.jdbc.db");
            iv.content = stats;
            if(!infoVaultDB.load(iv)) {
                infoVaultDB.save(iv);
            }
            LOG.info("Stats loaded: "+stats.toJSON());
            updateStatus(ServiceStatus.RUNNING);
            LOG.info("Started.");
            return true;
        }
        LOG.info("Failed to Start.");
        return false;
    }

    @Override
    public boolean pause() {
        updateStatus(ServiceStatus.PAUSED);
        return true;
    }

    @Override
    public boolean unpause() {
        updateStatus(ServiceStatus.RUNNING);
        return true;
    }

    @Override
    public boolean shutdown() {
        updateStatus(ServiceStatus.SHUTTING_DOWN);
        return super.shutdown();
    }

    @Override
    public boolean gracefulShutdown() {
        updateStatus(ServiceStatus.GRACEFULLY_SHUTTING_DOWN);
        return super.gracefulShutdown();
    }
}
