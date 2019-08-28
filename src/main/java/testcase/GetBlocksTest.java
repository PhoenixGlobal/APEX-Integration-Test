package testcase;

import message.request.cmd.GetBlocksCmd;
import message.util.RequestCallerService;

import java.util.HashMap;

public class GetBlocksTest implements IRunTestCase {

    @Override
    public HashMap<String, String> executeTest(RequestCallerService caller, String rpcUrl) {
        HashMap<String, String> testResult = new HashMap<>();
        testResult.put("name", "Get Blocks Command Test");
        try {
            final GetBlocksCmd cmd = new GetBlocksCmd();
            final String response = caller.postRequest(rpcUrl, cmd);
            if(response.contains("\"status\":200") && response.contains("\"succeed\":true")){
                testResult.put("status", "success");
                testResult.put("message", response);
            }
        } catch (Exception e){
            testResult.put("status", "error");
            testResult.put("message", e.getMessage());
        }
        return testResult;
    }

}
