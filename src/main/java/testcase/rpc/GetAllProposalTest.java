package testcase.rpc;

import message.request.cmd.GetAllProposalCmd;
import message.util.RequestCallerService;
import testcase.IRunTestCase;

import java.util.HashMap;

public class GetAllProposalTest implements IRunTestCase {

    @Override
    public HashMap<String, String> executeTest(RequestCallerService caller, String rpcUrl) {
        HashMap<String, String> testResult = new HashMap<>();
        testResult.put("name", "GetAllProposalCmd Test");
        testResult.put("status", "ERROR");
        testResult.put("message", "Test failed");
        try {
            final GetAllProposalCmd cmd = new GetAllProposalCmd();
            final String response = caller.postRequest(rpcUrl, cmd);
            if(response.contains("\"status\":200") && response.contains("\"succeed\":true") && response.contains("\"proposals\"")){
                testResult.put("status", "SUCCESS");
                testResult.put("message", "All tests executed as expected");
            }
        } catch (Exception e){
            testResult.put("message", e.getMessage());
        }
        return testResult;
    }

}
