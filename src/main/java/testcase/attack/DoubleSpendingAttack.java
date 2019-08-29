package testcase.attack;

import crypto.CPXKey;
import crypto.CryptoService;
import message.request.cmd.GetAccountCmd;
import message.request.cmd.SendRawTransactionCmd;
import message.response.ExecResult;
import message.transaction.FixedNumber;
import message.transaction.Transaction;
import message.transaction.TransactionType;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.MainTestRunner;
import testcase.IRunTestCase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.util.HashMap;

public class DoubleSpendingAttack implements IRunTestCase {

    private ECPrivateKey privKey;
    private CryptoService cryptoService;
    private String reciever;
    private GenericJacksonWriter writer;

    private static final Logger LOG = LoggerFactory.getLogger(MainTestRunner.class);

    public DoubleSpendingAttack(String privKeyRaw, String receiver) throws Exception {
        this.cryptoService = new CryptoService();
        this.privKey = cryptoService.getECPrivateKeyFromRawString(privKeyRaw);
        this.reciever = receiver;
        this.writer = new GenericJacksonWriter();
    }

    @Override
    @SuppressWarnings("unchecked")
    public HashMap<String, String> executeTest(RequestCallerService caller, String rpcUrl1, String rpcUrl2) {

        HashMap<String, String> testResult = new HashMap<>();
        testResult.put("name", "DoubleSpendingAttack");
        testResult.put("status", "ERROR");
        testResult.put("message", "Test failed");

        try {

            /*
            Initialize needed params
             */
            final String fromHash = CPXKey.getScriptHash(privKey);
            final String toHash = CPXKey.getScriptHashFromCPXAddress(reciever);

            /*
            Get sender account information
             */
            final GetAccountCmd getAccountCmdSender = new GetAccountCmd(CPXKey.getPublicAddressCPX(privKey));
            final ExecResult responseAccSender = writer.getObjectFromString(ExecResult.class, caller.postRequest(rpcUrl1, getAccountCmdSender));
            HashMap<String, Object> responseMapSender = (HashMap<String, Object>) responseAccSender.getResult();

            /*
            Get receiver account information
             */
            final GetAccountCmd getAccountCmdReciever = new GetAccountCmd(reciever);
            ExecResult responseAccReceiver = writer.getObjectFromString(ExecResult.class, caller.postRequest(rpcUrl1, getAccountCmdReciever));
            HashMap<String, Object> responseMapReciever = (HashMap<String, Object>) responseAccReceiver.getResult();

            /*
            Current Status
             */
            final long nonce = (int) responseMapSender.get("nextNonce");
            final BigDecimal balanceReceiver = BigDecimal.valueOf(Double.parseDouble((String) responseMapReciever.get("balance")));

            /*
            Create Transaction
             */
            final Transaction tx = Transaction.builder()
                    .txType(TransactionType.TRANSFER)
                    .fromPubKeyHash(fromHash)
                    .toPubKeyHash(toHash)
                    .amount(new FixedNumber(1000))
                    .nonce(nonce)
                    .data(new byte[0])
                    .gasPrice(new FixedNumber(0.0000003))
                    .gasLimit(BigInteger.valueOf(300000L))
                    .version(1)
                    .executeTime(Instant.now().toEpochMilli())
                    .build();

            /*
            Sign two identical transactions
             */
            SendRawTransactionCmd cmd = new SendRawTransactionCmd(tx.getBytes(cryptoService, privKey));
            SendRawTransactionCmd cmdCopy = new SendRawTransactionCmd(tx.getBytes(cryptoService, privKey));

            /*
            Send transactions simultaneously to different nodes
             */
            new Thread(() -> {
                try {
                    writer.getObjectFromString(ExecResult.class, caller.postRequest(rpcUrl1, cmd));
                } catch (Exception e) {
                    LOG.error("Failed to send TX1");
                }
            }).start();

            new Thread(() -> {
                try {
                    writer.getObjectFromString(ExecResult.class, caller.postRequest(rpcUrl2, cmdCopy));
                } catch (Exception e) {
                    LOG.error("Failed to send TX2");
                }
            }).start();

            /*
            Wait until txs are processed
             */
            Thread.sleep(5000L);

            /*
            Get updated balance
             */
            responseAccReceiver = writer.getObjectFromString(ExecResult.class, caller.postRequest(rpcUrl1, getAccountCmdReciever));
            responseMapReciever = (HashMap<String, Object>) responseAccReceiver.getResult();
            final BigDecimal balanceReceiverUpdated = BigDecimal.valueOf(Double.parseDouble((String) responseMapReciever.get("balance")));

            /*
            Make sure balance increased less than the value of both transactions
             */
            if(balanceReceiver.add(new BigDecimal(2000L)).compareTo(balanceReceiverUpdated) < 0){
                throw new Exception("Double spending detected");
            }

            testResult.put("status", "SUCCESS");
            testResult.put("message", "All tests executed as expected");

        } catch (Exception e){
            testResult.put("message", e.getMessage());
        }

        return testResult;
    }

}
