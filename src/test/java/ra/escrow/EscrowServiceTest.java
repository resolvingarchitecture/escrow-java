package ra.escrow;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ra.common.InfoVaultDB;
import ra.common.file.InfoVaultFileDB;

import java.util.Properties;
import java.util.logging.Logger;

public class EscrowServiceTest {

    private static final Logger LOG = Logger.getLogger(EscrowServiceTest.class.getName());

    private static EscrowService service;
    private static Properties props;
    private static boolean serviceRunning = false;

    @BeforeClass
    public static void init() {
        LOG.info("Init...");
        InfoVaultDB fileDB = new InfoVaultFileDB();
        props = new Properties();
        props.put(InfoVaultDB.DB, fileDB);
        service = new EscrowService();
        serviceRunning = service.start(props);
    }

    @AfterClass
    public static void tearDown() {
        LOG.info("Teardown...");
        service.gracefulShutdown();
    }

    @Test
    public void verifyInitializedTest() {
        Assert.assertTrue(serviceRunning);
    }
}
