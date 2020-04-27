package tier.three;

import com.tier.api.BackendInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.Date;

@Remote(BackendInterface.class)
@Stateful
public class BackendEJB implements BackendInterface {

    private static final Logger logger = LoggerFactory.getLogger(BackendEJB.class);

    public String impl() {
        logger.info("TIER THREE on HOST: {}", System.getProperty("jboss.node.name", ""));
        return (new Date()).toString();
    }
}
