package testcase.rpc;

import message.request.cmd.GetAccountCmd;
import message.util.RequestCallerService;
import testcase.IRunTestCase;

import java.util.HashMap;

public class GetAccountTest implements IRunTestCase {

    private String address;

    public GetAccountTest(String address){
        this.address = address;
    }

    @Override
    public HashMap<String, String> executeTest(RequestCallerService caller, String rpcUrl1, String rpcUrl2) {
        HashMap<String, String> testResult = new HashMap<>();
        testResult.put("name", "GetAccountCmd Test");
        testResult.put("status", "ERROR");
        testResult.put("message", "Test failed");
        try {
            final GetAccountCmd cmd = new GetAccountCmd(address);
            final GetAccountCmd cmdWrong = new GetAccountCmd(address + "b");
            final String response = caller.postRequest(rpcUrl1, cmd);
            final String responseBad = caller.postRequest(rpcUrl1, cmdWrong);
            if(response.contains("\"status\":200") && response.contains(address) && responseBad.contains("\"status\":400")){
                testResult.put("status", "SUCCESS");
                testResult.put("message", "All tests executed as expected");
            }
        } catch (Exception e){
            testResult.put("message", e.getMessage());
        }
        return testResult;
    }

}
