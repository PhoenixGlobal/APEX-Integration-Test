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
    public HashMap<String, String> executeTest(RequestCallerService caller, String rpcUrl) {
        HashMap<String, String> testResult = new HashMap<>();
        testResult.put("name", "GetAccountCmd Test");
        testResult.put("status", "error");
        testResult.put("message", "Statuscode was not 200");
        try {
            final GetAccountCmd cmd = new GetAccountCmd(address);
            final GetAccountCmd cmdWrong = new GetAccountCmd(address + "b");
            final String response = caller.postRequest(rpcUrl, cmd);
            final String responseBad = caller.postRequest(rpcUrl, cmdWrong);
            if(response.contains("\"status\":200") && responseBad.contains("\"status\":400")){
                testResult.put("status", "success");
                testResult.put("message", "All tests executed as expected");
            }
        } catch (Exception e){
            testResult.put("message", e.getMessage());
        }
        return testResult;
    }

}
