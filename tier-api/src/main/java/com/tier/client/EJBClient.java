package com.tier.client;

import com.tier.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class EJBClient {

    public static final String REMOTE_PATTERN = Config.env("REMOTE_PATTERN", "%s://%s:%s/wildfly-services");
    public static final String REMOTE_HOST = Config.env("REMOTE_HOST", "localhost");
    public static final String REMOTE_HTTP_PORT = Config.env("REMOTE_HTTP_PORT", "8080");
    public static final String REMOTE_HTTPS_PORT = Config.env("REMOTE_HTTPS_PORT", "8443");

    private static final Logger logger = LoggerFactory.getLogger(EJBClient.class);

    // looks up a remote ejb based on the impl and interface
    protected <T> T lookup(final String protocol, final Class<? extends T> impl, final Class<T> inter) {
        String port = REMOTE_HTTPS_PORT;
        if ("http".equalsIgnoreCase(protocol)) {
            port = REMOTE_HTTP_PORT;
        }

        final String endpoint = Config.env("REMOTE_PATTERN", String.format(REMOTE_PATTERN, protocol, REMOTE_HOST, port));

        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY,  "org.wildfly.naming.client.WildFlyInitialContextFactory");

        // do remote JNDI lookup using the remote endpoint
        props.put(Context.PROVIDER_URL, endpoint);

        // get username and password
        final String username = Config.env("REMOTE_USER", "ejb");
        final String password = Config.env("REMOTE_PASSWORD", "ejb");
        props.put(Context.SECURITY_PRINCIPAL, username);
        props.put(Context.SECURITY_CREDENTIALS, password);

        // build the ejb name from the remote endpoint
        final String ejbName = String.format("ejb:/tier/%s!%s?stateful", impl.getSimpleName(), inter.getName());

        // return the found EJB
        final Context context;
        try {
            context = new InitialContext(props);
            T found =  (T)context.lookup(ejbName);
            logger.info("Found EJB:{} via endpoint {}", ejbName, endpoint);
            return found;
        } catch (NamingException e) {
            logger.error("Error during lookup of {} via {}", ejbName, endpoint, e);
            return null;
        }
    }
}
