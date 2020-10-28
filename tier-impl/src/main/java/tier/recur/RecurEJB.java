package tier.recur;

import com.tier.api.RecuringInterface;
import com.tier.client.EJBClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.Date;

@Remote(RecuringInterface.class)
@Local(RecurLocal.class)
@Stateless
public class RecurEJB implements RecurLocal {

    private static final String NODE = System.getProperty("jboss.node.name", "NONAME");

    private static final Logger logger = LoggerFactory.getLogger(RecurEJB.class);

    @Override
    public String call(String protocol, boolean remote, int depth) {
        if (depth < 1) {
            logger.info("End recur on node={}", NODE);
            return (new Date()).toString();
        }
        logger.info("{} Recurring... protocol={}, remote={}, depth={}", NODE, protocol, remote, depth);
        return EJBClient.lookup(protocol, remote, RecurEJB.class, RecuringInterface.class).call(protocol, remote, depth - 1);
    }
}
