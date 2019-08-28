package test;

import message.request.cmd.GetBlockCountCmd;
import message.util.RequestCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testcase.GetBlocksTest;
import testcase.IRunTestCase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainTestRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MainTestRunner.class);

    public static void main (String [] args) throws Exception {

        final String privKeySender = args[0];
        final String addressReceiver = args[1];
        final String rpcUrl = args[2];
        LOG.info(privKeySender);
        LOG.info(addressReceiver);
        LOG.info(rpcUrl);
        final RequestCallerService callerService = new RequestCallerService();
        ArrayList<IRunTestCase> testsToExecute = new ArrayList<>();
        ArrayList<HashMap<String, String>> results = new ArrayList<>();
        testsToExecute.add(new GetBlocksTest());

        testsToExecute.forEach(test -> results.add(test.executeTest(callerService, rpcUrl)));

        boolean chainIsNotProducing = true;

        GetBlockCountCmd cmd = new GetBlockCountCmd();
        while (chainIsNotProducing){
            try {
                String response = callerService.postRequest(rpcUrl, cmd);
                LOG.info("Block count message was:");
                LOG.info("\n" + response);
                if (!response.contains("\"result\":\"0\""))
                    chainIsNotProducing = false;
            } catch (Exception e){
                LOG.warn("Chain not ready yet. Will wait");
            }
            Thread.sleep(10000L);
        }

        LOG.info("----------------------------- TEST FINISHED --------------------------------------------");
        LOG.info("-----------------------------               --------------------------------------------");
        LOG.info("-----------------------------               --------------------------------------------");
        results.forEach(result -> {
            if(result.get("status").equals("error")){
                LOG.error("Name: " + result.get("name") + "               Status: " + result.get("status"));
                LOG.error("Error message :" + result.get("message"));
            } else {
                LOG.info("Name: " + result.get("name") + "               Status: " + result.get("status"));
            }
        });
        LOG.info("----------------------------------------------------------------------------------------");
        LOG.info("----------------------------------------------------------------------------------------");
        LOG.info("----------------------------------------------------------------------------------------");
    }

}
