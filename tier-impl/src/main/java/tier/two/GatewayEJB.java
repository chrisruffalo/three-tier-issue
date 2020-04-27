package tier.two;

import com.tier.api.BackendInterface;
import com.tier.api.GatewayInterface;
import com.tier.client.EJBClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tier.three.BackendEJB;

import javax.ejb.Remote;
import javax.ejb.Stateful;

@Remote(GatewayInterface.class)
@Stateful
public class GatewayEJB extends EJBClient implements GatewayInterface {

    private static final Logger logger = LoggerFactory.getLogger(GatewayEJB.class);

    public String ask(final String protocol) {
        logger.info("TIER TWO on HOST: {}", System.getProperty("jboss.node.name", ""));
        return this.lookup(protocol, BackendEJB.class, BackendInterface.class).impl();
    }
}
