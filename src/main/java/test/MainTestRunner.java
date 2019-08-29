package test;

import message.request.cmd.GetBlockCountCmd;
import message.util.RequestCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testcase.attack.DoubleSpendingAttack;
import testcase.rpc.GetAccountTest;
import testcase.rpc.GetAllProposalTest;
import testcase.rpc.GetAllProposalVotesTest;
import testcase.rpc.GetBlocksTest;
import testcase.IRunTestCase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainTestRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MainTestRunner.class);

    public static void main (String [] args) throws Exception {

        /*
        Required objects
         */
        final String privKeySender = args[0];
        final String address1 = args[1];
        final String rpcUrl1 = args[2];
        final String address2 = args[3];
        final String rpcUrl2 = args[4];

        final RequestCallerService callerService = new RequestCallerService();
        ArrayList<IRunTestCase> testsToExecute = new ArrayList<>();
        ArrayList<HashMap<String, String>> results = new ArrayList<>();

        /*
        Add TestCases to execution List
         */
        testsToExecute.add(new GetBlocksTest());
        testsToExecute.add(new GetAllProposalTest());
        testsToExecute.add(new GetAllProposalVotesTest());
        testsToExecute.add(new GetAccountTest(address1));
        testsToExecute.add(new DoubleSpendingAttack(privKeySender, address1));

        /*
        Wait until the chain produces blocks
         */
        boolean chainIsNotProducing = true;
        GetBlockCountCmd cmd = new GetBlockCountCmd();
        while (chainIsNotProducing){
            try {
                String response = callerService.postRequest(rpcUrl1, cmd);
                LOG.info("Waiting for chain to start producing.");
                if (!response.contains("\"result\":\"0\""))
                    chainIsNotProducing = false;
                else Thread.sleep(30000L);
            } catch (Exception e){
                LOG.error("Something went wrong. Is RPC configured properly?");
                Thread.sleep(30000L);
            }
        }

        /*
        Execute all tests
         */
        testsToExecute.forEach(test -> results.add(test.executeTest(callerService, rpcUrl1, rpcUrl2)));

        /*
        Log results
         */
        LOG.info("----------------------------- TEST FINISHED -----------------------------");
        LOG.info("-----------------------------               -----------------------------");
        LOG.info("-----------------------------               -----------------------------");
        LOG.info("\n\n");
        results.forEach(result -> {
            if(result.get("status").equals("error")){
                LOG.error("Name: " + result.get("name") + " Status: " + result.get("status"));
                LOG.error("Test message :" + result.get("message"));
            } else {
                LOG.info("Name: " + result.get("name") + " Status: " + result.get("status"));
                LOG.info("Test Message: " + result.get("message"));
            }
            LOG.info("\n\n");
        });
        LOG.info("-------------------------------------------------------------------------");
        LOG.info("-------------------------------------------------------------------------");
        LOG.info("-------------------------------------------------------------------------");
    }

}
