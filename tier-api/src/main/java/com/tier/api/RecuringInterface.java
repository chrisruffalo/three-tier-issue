package com.tier.api;

public interface RecuringInterface {

    /**
     * Calls depth - 1, returns on 0 call
     *
     * @param remote if true make a remote call to the next host, otherwise call this host (over remote, but same instance)
     * @param depth of the call, 0 ends the call chain, the next call is at depth -1
     * @return current time
     */
    String call(String protocol, boolean remote, int depth);

}
