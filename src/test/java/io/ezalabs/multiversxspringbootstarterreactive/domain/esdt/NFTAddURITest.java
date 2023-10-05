package io.ezalabs.multiversxspringbootstarterreactive.domain.esdt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.ezalabs.multiversxspringbootstarterreactive.config.constants.ESDTConstants;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import io.ezalabs.multiversxspringbootstarterreactive.domain.common.Balance;
import io.ezalabs.multiversxspringbootstarterreactive.domain.common.Nonce;
import io.ezalabs.multiversxspringbootstarterreactive.domain.esdt.common.ESDTUri;
import io.ezalabs.multiversxspringbootstarterreactive.domain.esdt.common.TokenIdentifier;
import io.ezalabs.multiversxspringbootstarterreactive.domain.transaction.GasLimit;
import io.ezalabs.multiversxspringbootstarterreactive.domain.wallet.Wallet;

class NFTAddURITest {

  final Wallet wallet = Wallet.fromPrivateKeyHex("8442d0bcadbae1b75eff1165f1e3a61f120bddbb440393d8ba3c366342ee4f62");

  @Test
  void create() {
    final var identifier = TokenIdentifier.fromString("YMY-19jf9a");
    final var nonce = Nonce.fromLong(2L);

    final var uri1 = ESDTUri.fromString("ipfsCID/img.jpeg", ESDTUri.Type.MEDIA);
    final var uri2 = ESDTUri.fromString("ipfsCID/img2.jpeg", ESDTUri.Type.MEDIA);
    final Set<ESDTUri> uris = new LinkedHashSet<>(List.of(uri1, uri2));

    var nft = NFTAddURI.builder()
        .tokenIdentifier(identifier)
        .nonce(nonce)
        .uris(uris)
        .build();

    var req = nft.toTransactionRequest(wallet);

    assertEquals(wallet.getAddress().getBech32(), req.getReceiverAddress().getBech32());
    assertEquals(Balance.zero().getValue(), req.getValue().getValue());
    assertEquals(GasLimit.defaultNftCreate().getValue(), req.getGasLimit().getValue());

    var dataArgs = req.getData().toString().split("@");
    assertEquals(ESDTConstants.ESDT_NFT_ADD_URI_CALL, dataArgs[0]);
    assertEquals(identifier.getHex(), dataArgs[1]);
    assertEquals(nonce.getHex(), dataArgs[2]);
    assertEquals(uri1.getHex(), dataArgs[3]);
    assertEquals(uri2.getHex(), dataArgs[4]);
  }

}