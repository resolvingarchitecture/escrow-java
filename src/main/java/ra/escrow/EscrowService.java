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

    // Offer
    public static final String OPERATION_SUBMIT_OFFER = "SUBMIT_OFFER"; // (1, 2)
    public static final String OPERATION_CHECK_OFFER_STATUS = "CHECK_OFFER_STATUS"; // (3)
    public static final String OPERATION_ACCEPT_OFFER = "ACCEPT_OFFER"; // (4)

    // Escrow
    public static final String OPERATION_FUND_ESCROW = "FUND_ESCROW"; // (5)
    public static final String OPERATION_INSTRUCTIONS_SETTLED = "INSTRUCTIONS_SETTLED"; // (6)
    public static final String OPERATION_SETTLEMENT_VERIFIED = "SETTLEMENT_VERIFIED"; // (7)

    // Admin
    public static final String OPERATION_SET_STATIC_ESCROW_ACCOUNT = "SET_STATIC_ESCROW_ACCOUNT";
    public static final String OPERATION_CLOSE_ESCROW = "CLOSE_ESCROW";

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
            case OPERATION_SUBMIT_OFFER:{submitOffer(e);break;}
            case OPERATION_CHECK_OFFER_STATUS:{checkOfferStatus(e);break;}
            case OPERATION_ACCEPT_OFFER:{acceptOffer(e);break;}
            case OPERATION_FUND_ESCROW:{fundEscrow(e);break;}
            case OPERATION_INSTRUCTIONS_SETTLED:{instructionsSettled(e);break;}
            case OPERATION_SETTLEMENT_VERIFIED:{settlementVerified(e);break;}
            case OPERATION_SET_STATIC_ESCROW_ACCOUNT:{setStaticEscrowAccount(e);break;}
            case OPERATION_CLOSE_ESCROW:{closeEscrow(e);break;}
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
    private void submitOffer(Envelope e) {
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
    private void checkOfferStatus(Envelope e) {

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
    private void acceptOffer(Envelope e) {
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
        // Must set Escrow Address via seed prior to use
//        escrow.escrowAddress = config.escrowAddress;
//        escrow.senderAddress = sellOffer.

    }

    private void fundEscrow(Envelope e) {
        String escrowId = (String)DLC.getValue("escrowId", e);

        InfoVault infoVault = new InfoVault();
    }

    private void instructionsSettled(Envelope e) {
        String escrowId = (String)DLC.getValue("escrowId", e);

        InfoVault infoVault = new InfoVault();
    }

    private void settlementVerified(Envelope e) {
        String escrowId = (String)DLC.getValue("escrowId", e);

        InfoVault infoVault = new InfoVault();
    }

    private void setStaticEscrowAccount(Envelope e) {
        String btcAddress = (String)DLC.getValue("btcAddress", e);

        InfoVault infoVault = new InfoVault();
    }

    private void closeEscrow(Envelope e) {
        String escrowId = (String)DLC.getValue("escrowId", e);

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
                // TODO: Now request Config from a seed with Escrow Service available
                Envelope e = Envelope.documentFactory();

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
