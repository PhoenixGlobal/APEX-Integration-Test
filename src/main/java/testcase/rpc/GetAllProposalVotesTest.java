package testcase.rpc;

import message.request.cmd.GetAllProposalVotesCmd;
import message.util.RequestCallerService;
import testcase.IRunTestCase;

import java.util.HashMap;

public class GetAllProposalVotesTest implements IRunTestCase {

    @Override
    public HashMap<String, String> executeTest(RequestCallerService caller, String rpcUrl1, String rpcUrl2) {
        HashMap<String, String> testResult = new HashMap<>();
        testResult.put("name", "GetAllProposalVotesCmd Test");
        testResult.put("status", "ERROR");
        testResult.put("message", "Test failed");
        try {
            final GetAllProposalVotesCmd cmd = new GetAllProposalVotesCmd();
            final String response = caller.postRequest(rpcUrl1, cmd);
            if(response.contains("\"status\":200") && response.contains("\"succeed\":true") && response.contains("\"votes\"")){
                testResult.put("status", "SUCCESS");
                testResult.put("message", "All tests executed as expected");
            }
        } catch (Exception e){
            testResult.put("message", e.getMessage());
        }
        return testResult;
    }

}
