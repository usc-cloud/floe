/*
 * Copyright 2011, University of Southern California. All Rights Reserved.
 * 
 * This software is experimental in nature and is provided on an AS-IS basis only. 
 * The University SPECIFICALLY DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT 
 * LIMITATION ANY WARRANTY AS TO MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * This software may be reproduced and used for non-commercial purposes only, 
 * so long as this copyright notice is reproduced with each such copy made.
 */
package edu.usc.pgroup.floe.util;


import edu.usc.pgroup.floe.impl.FloeRuntimeEnvironment;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;

/**
 * <class>NetworkUtil</class> provides fuctions for network related operations
 */
public class NetworkUtil {

    /**
     * Get BASE URL for a given Port.
     *
     * @param port port to be open for communication if local machine
     * @return URI of the local machine return type : {@link URI}
     */
    public static URI getBaseURI(int port) {

        try {
            return UriBuilder.fromUri("http://" + NetworkUtil.getHostAddress() + "/").port(port).build();
        } catch (IOException e) {
            e.printStackTrace();
            return UriBuilder.fromUri("http://localhost/").port(port).build();
        }
    }


    /**
     * Get Current host address that is visible to the out side
     *
     * @return CurrentHost address
     * @throws IOException in case of internet access Error
     */
    public static String getHostAddress() throws IOException {

        String managerHost = FloeRuntimeEnvironment.getEnvironment().
                getSystemConfigParam(Constants.MANAGER_HOST);
        int managerPort = Integer.parseInt(FloeRuntimeEnvironment.getEnvironment().
                getSystemConfigParam(Constants.MANAGER_PORT));

        if(FloeRuntimeEnvironment.getEnvironment().getSystemConfigParam(Constants.CURRET_HOST) != null) {
            return FloeRuntimeEnvironment.getEnvironment().
                    getSystemConfigParam(Constants.CURRET_HOST);
        }

        if("true".equalsIgnoreCase(FloeRuntimeEnvironment.getEnvironment().getSystemConfigParam(Constants.STATIC_HOST))) {
            return managerHost;
        }

        Socket s = null;

        s = new Socket(managerHost, managerPort);


        String currentHost = s.getInetAddress().getHostAddress();
        return currentHost.trim();

    }



}
