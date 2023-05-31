package software.crldev.multiversxspringbootstarterreactive.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import software.crldev.multiversxspringbootstarterreactive.domain.ApiModelToDomainConvertible;
import software.crldev.multiversxspringbootstarterreactive.domain.account.Address;
import software.crldev.multiversxspringbootstarterreactive.domain.common.Balance;
import software.crldev.multiversxspringbootstarterreactive.domain.common.Nonce;
import software.crldev.multiversxspringbootstarterreactive.domain.transaction.GasLimit;
import software.crldev.multiversxspringbootstarterreactive.domain.transaction.GasPrice;
import software.crldev.multiversxspringbootstarterreactive.domain.transaction.Hash;
import software.crldev.multiversxspringbootstarterreactive.domain.transaction.PayloadData;
import software.crldev.multiversxspringbootstarterreactive.domain.transaction.Signature;
import software.crldev.multiversxspringbootstarterreactive.domain.transaction.Transaction;
import software.crldev.multiversxspringbootstarterreactive.domain.transaction.TransactionStatus;

/**
 * API response when querying for transactions by address
 *
 * @author carlo_stanciu
 */
@Value
@Jacksonized
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransactionForAddress implements ApiModelToDomainConvertible<Transaction> {

  @JsonProperty("hash")
  String hash;
  @JsonProperty("fee")
  String fee;
  @JsonProperty("miniBlockHash")
  String miniBlockHash;
  @JsonProperty("nonce")
  Long nonce;
  @JsonProperty("round")
  Long round;
  @JsonProperty("value")
  BigInteger value;
  @JsonProperty("receiver")
  String receiver;
  @JsonProperty("sender")
  String sender;
  @JsonProperty("receiverShard")
  Long receiverShard;
  @JsonProperty("senderShard")
  Long senderShard;
  @JsonProperty("gasPrice")
  BigInteger gasPrice;
  @JsonProperty("gasLimit")
  BigInteger gasLimit;
  @JsonProperty("gasUsed")
  BigInteger gasUsed;
  @JsonProperty("data")
  String data;
  @JsonProperty("signature")
  String signature;
  @JsonProperty("timestamp")
  Long timestamp;
  @JsonProperty("status")
  String status;
  @JsonProperty("searchOrder")
  Integer searchOrder;
  @JsonProperty("scResults")
  List<SmartContractResult> scResults;

  @Override
  public Transaction toDomainObject() {
    var transaction = new Transaction();
    transaction.setReceiver(Address.fromBech32(this.getReceiver()));
    transaction.setSender(Address.fromBech32(this.getSender()));
    transaction.setValue(Balance.fromNumber(this.getValue()));
    transaction.setNonce(Nonce.fromLong(this.getNonce()));
    transaction.setGasPrice(GasPrice.fromNumber(this.getGasPrice()));
    transaction.setGasLimit(GasLimit.fromNumber(this.getGasLimit()));
    transaction.setHash(Hash.fromString(this.getHash()));
    transaction.setPayloadData(PayloadData.fromBase64Encoded(this.getData()));
    transaction.applySignature(Signature.fromHex(this.getSignature()));
    transaction.setStatus(TransactionStatus.fromString(this.getStatus()));
    return transaction;
  }
}
