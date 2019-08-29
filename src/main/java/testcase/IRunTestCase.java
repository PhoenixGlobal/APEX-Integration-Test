package testcase;

import message.util.RequestCallerService;

import java.util.HashMap;

@FunctionalInterface
public interface IRunTestCase {

    HashMap<String, String> executeTest(RequestCallerService caller, String rpcUrl1, String rpcUrl2);

}
