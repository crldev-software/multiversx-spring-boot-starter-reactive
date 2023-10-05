package io.ezalabs.multiversxspringbootstarterreactive.domain.esdt.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.ezalabs.multiversxspringbootstarterreactive.config.constants.ESDTConstants;
import org.junit.jupiter.api.Test;
import io.ezalabs.multiversxspringbootstarterreactive.domain.account.Address;
import io.ezalabs.multiversxspringbootstarterreactive.domain.common.Nonce;
import io.ezalabs.multiversxspringbootstarterreactive.domain.esdt.NFTSFTGlobalOp;
import io.ezalabs.multiversxspringbootstarterreactive.domain.esdt.common.TokenIdentifier;
import io.ezalabs.multiversxspringbootstarterreactive.domain.transaction.GasLimit;
import io.ezalabs.multiversxspringbootstarterreactive.domain.wallet.Wallet;

class NFTSFTGlobalOpTest {

  private static final Wallet wallet = Wallet.fromPrivateKeyHex(
      "8442d0bcadbae1b75eff1165f1e3a61f120bddbb440393d8ba3c366342ee4f62");
  private final TokenIdentifier identifier = TokenIdentifier.fromString("YMY-8888aa");
  private final Nonce nonce = Nonce.fromLong(2L);

  @Test
  void freeze() {
    var minting = builder()
        .type(NFTSFTGlobalOp.Type.FREEZE)
        .build();

    var request = minting.toTransactionRequest(wallet);

    assertEquals(Address.fromBech32(ESDTConstants.ESDT_ISSUER_BECH32_ADDRESS).getBech32(),
        request.getReceiverAddress().getBech32());
    assertEquals(GasLimit.defaultEsdtGlobalOp().getValue(), request.getGasLimit().getValue());
    assertTrue(request.getValue().isZero());

    var dataArgs = request.getData().toString().split("@");
    assertEquals(ESDTConstants.ESDT_NFT_FREEZE_CALL, dataArgs[0]);
    assertEquals(identifier.getHex(), dataArgs[1]);
    assertEquals(nonce.getHex(), dataArgs[2]);
    assertEquals(wallet.getAddress().getHex(), dataArgs[3]);
  }

  @Test
  void unfreeze() {
    var burning = builder()
        .type(NFTSFTGlobalOp.Type.UNFREEZE)
        .build();

    var request = burning.toTransactionRequest(wallet);

    var dataArgs = request.getData().toString().split("@");
    assertEquals(ESDTConstants.ESDT_NFT_UNFREEZE_CALL, dataArgs[0]);
  }

  @Test
  void wipe() {
    var burning = builder()
        .type(NFTSFTGlobalOp.Type.WIPE)
        .build();

    var request = burning.toTransactionRequest(wallet);

    var dataArgs = request.getData().toString().split("@");
    assertEquals(ESDTConstants.ESDT_NFT_WIPE_CALL, dataArgs[0]);
  }

  private NFTSFTGlobalOp.NFTSFTGlobalOpBuilder builder() {
    return NFTSFTGlobalOp.builder()
        .address(wallet.getAddress())
        .nonce(nonce)
        .tokenIdentifier(identifier);
  }

}