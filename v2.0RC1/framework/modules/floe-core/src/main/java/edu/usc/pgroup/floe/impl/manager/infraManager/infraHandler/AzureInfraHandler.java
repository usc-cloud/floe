package edu.usc.pgroup.floe.impl.manager.infraManager.infraHandler;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.soyatec.windowsazure.management.AsyncResultCallback;
import org.soyatec.windowsazure.management.ServiceManagement;
import org.soyatec.windowsazure.management.ServiceManagementRest;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class AzureInfraHandler extends CloudInrfaHandler {

    /**
     * :) counter to add as a prefix
     */
    public static int counter =  (int) Math.random()*1000;


	AzureInfraHandler()
	{

	}

	@Override
	protected void deployInstance(Map<String,String> params) {


        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("conf"+ File.separator + "azure.properties"));
          //  properties.load(new FileInputStream("C:\\Users\\charith\\Documents\\Projects\\USC\\floe\\trunk\\framework\\modules\\distribution\\manager\\src\\main\\conf\\azure.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String subscriptionId =  properties.getProperty("subscriptionId");
        String keyStoreFile =   properties.getProperty("keyStoreFile");
        String keyStorePassword = properties.getProperty("keyStorePassword");
        String trustStoreFile = properties.getProperty("trustStoreFile");
        String trustStorePassword = properties.getProperty("trustStorePassword");
        String certificateAlias= properties.getProperty("certificateAlias");
        String imageName = properties.getProperty("imageName");
        String managerHost = properties.getProperty("managerHost");
        String coordinatorHost = properties.getProperty("coordinatorHost");



        if(subscriptionId == null || keyStoreFile == null || keyStorePassword == null ||
                trustStoreFile== null || trustStorePassword == null || certificateAlias == null) {
            throw new RuntimeException("Error in azure configuration " + "conf"+ File.separator + "azure.properties");
        }

        try {
            final ServiceManagement management = new ServiceManagementRest(subscriptionId,keyStoreFile,keyStorePassword,
                    trustStoreFile,trustStorePassword,certificateAlias);

            management.createHostedService("FloeService" +counter, "Floelabel" + counter, "newFloedescription",
                    "West US", null);

            final Semaphore blocker = new Semaphore(2);
            blocker.acquire(2);
            management.createLinuxVirtualMachineDeployment("floeService" +counter, "floe-setup"+counter,
                    "Floelabel" + counter, "floe-role"+counter, "floe" + counter, "cwickram", "test@1234",
                    "", imageName, "ExtraLarge", new AsyncResultCallback() {
                @Override
                public void onSuccess(Object result) {
                    System.out.println("CREATED : " + result);
                    blocker.release();
                    management.startVMRole("FloeService" +counter, "floe-setup"+counter ,"floe-role"+counter ,new AsyncResultCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            System.out.println("STARTED : " + result);
                            blocker.release(2);
                        }

                        @Override
                        public void onError(Object result) {
                            System.out.println("Error while Starting VM : " + result);
                            blocker.release(2);
                        }
                    });
                }

                @Override
                public void onError(Object result) {
                    System.out.println("Error while Creating VM : " + result);
                    blocker.release(2);
                }
            } );

            blocker.acquire(2);

            System.out.println("released....");


            String host = "floeService" +counter + ".cloudapp.net";
            counter++;
            System.out.println(host);

            Connection conn = new Connection(host);
            boolean connected = false;
            while (!connected)   {
                try {
                    conn.connect();
                    boolean isAuthenticated = conn.authenticateWithPassword("cwickram", "test@1234");
                    if(!isAuthenticated) {
                        conn.close();
                        Thread.sleep(5000);
                        continue;
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("TIMEOUT");
                }
            }





            Session sess = conn.openSession();

            sess.execCommand(
                    "cd /root/Floe/flow-container-1.0.0-SNAPSHOT/bin;nohup sh container-azure.sh " + managerHost + " "
                            + coordinatorHost + " > ~/out_"+(int)(Math.random()*1000)+"_container.txt  &");
            //sess.execCommand("sh manager.sh");
            System.out.println("Here is some information about the remote host:");
            InputStream stdout = new StreamGobbler(sess.getStdout());

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);

            }

            /* Show exit status, if available (otherwise "null") */

            System.out.println("ExitCode: " + sess.getExitStatus());

            /* Close this session */

            sess.close();

            /* Close the connection */

            conn.close();
            System.out.println("Exit...");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while Initiating resoruces " , e);
        }


    }

	@Override
	protected void releaseInstance(String instanceID) {
		// TODO Auto-generated method stub
		
	}

    public static void main(String[] args) {
        new AzureInfraHandler().deployInstance(null);
    }

}
