package test;

import message.util.RequestCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testcase.GetBlocksTest;
import testcase.IRunTestCase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainTestRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MainTestRunner.class);

    public static void main (String [] args) {

        final String privKeySender = args[0];
        final String addressReceiver = args[1];
        final String rpcUrl = args[2];
        final RequestCallerService callerService = new RequestCallerService();
        ArrayList<IRunTestCase> testsToExecute = new ArrayList<>();
        ArrayList<HashMap<String, String>> results = new ArrayList<>();
        testsToExecute.add(new GetBlocksTest());

        testsToExecute.forEach(test -> results.add(test.executeTest(callerService, rpcUrl)));

        LOG.info("----------------------------- TEST FINISHED --------------------------------------------");
        LOG.info("-----------------------------               --------------------------------------------");
        LOG.info("-----------------------------               --------------------------------------------");
        int success = 0;
        int fail = 0;
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
