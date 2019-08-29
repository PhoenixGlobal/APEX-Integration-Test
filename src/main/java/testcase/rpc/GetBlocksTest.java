package testcase.rpc;

import message.request.cmd.GetBlocksCmd;
import message.util.RequestCallerService;
import testcase.IRunTestCase;

import java.util.HashMap;

public class GetBlocksTest implements IRunTestCase {

    @Override
    public HashMap<String, String> executeTest(RequestCallerService caller, String rpcUrl) {
        HashMap<String, String> testResult = new HashMap<>();
        testResult.put("name", "GetBlocksCmd Test");
        testResult.put("status", "ERROR");
        testResult.put("message", "Test failed");
        try {
            final GetBlocksCmd cmd = new GetBlocksCmd();
            final String response = caller.postRequest(rpcUrl, cmd);
            if(response.contains("\"status\":200") && response.contains("\"succeed\":true") && response.contains("\"hash\"")){
                testResult.put("status", "SUCCESS");
                testResult.put("message", "All tests executed as expected");
            }
        } catch (Exception e){
            testResult.put("message", e.getMessage());
        }
        return testResult;
    }

}
