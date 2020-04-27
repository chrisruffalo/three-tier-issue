package tier.one;
import com.tier.api.FrontInterface;
import com.tier.api.GatewayInterface;
import com.tier.client.EJBClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tier.two.GatewayEJB;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateful;

@Local(FrontLocal.class)
@Remote(FrontInterface.class)
@Stateful
public class FrontEJB extends EJBClient implements FrontInterface {

    private static final Logger logger = LoggerFactory.getLogger(FrontEJB.class);

    public String ask(final String protocol) {
        logger.info("TIER ONE on HOST: {}", System.getProperty("jboss.node.name", ""));
        return this.lookup(protocol, GatewayEJB.class, GatewayInterface.class).ask(protocol);
    }
}
