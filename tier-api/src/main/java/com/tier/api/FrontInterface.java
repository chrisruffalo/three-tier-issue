package com.tier.api;

import javax.ejb.Remote;

@Remote
public interface FrontInterface {

    String ask(final String protocol);

}
