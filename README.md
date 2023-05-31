# MultiversX Spring Boot Starter Reactive

This is a **Spring Boot Starter** project for integrating with the **MultiversX Network**, with the
goal of achieving an **effortless autoconfigured integration** with the network.\
The client is implemented using project Reactor as the Reactive Streams' specification
implementation, allowing **fully non-blocking operations** and providing **efficient demand
management** when interacting with the network, ideal for building **scalable reactive
microservices**.

[![Build Status](https://app.travis-ci.com/crldev-software/elrond-spring-boot-starter-reactive.svg?branch=main)](https://app.travis-ci.com/crldev-software/elrond-spring-boot-starter-reactive)

[![java](https://img.shields.io/badge/Java11-07405E?style=for-the-badge&logo=java&logoColor=white)](https://openjdk.java.net/projects/jdk/11)
[![spring](https://img.shields.io/badge/SpringBoot2.0-217346?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![reactor](https://img.shields.io/badge/reactor-navy?style=for-the-badge&logo=s&logoColor=white)](https://projectreactor.io/)

## Author

[@carlo-stanciu](https://www.github.com/carlo-stanciu)

[![](https://crldev.software/img/readme-banner.png)](#)

## Features

- Auto synchronise network configurations from the MultiversX Network at startup based on the
  configured gateway
- Non-blocking network requests with the reactive MultiversX client
- Easy to use Interactors for executing various blockchain operations
- A lot of abstracted complexity in creating addresses, wallets, transactions

## Usage

To use the starter, add the following dependency to the dependencies section of your build
descriptor:

- Maven (in your pom.xml)

```
<dependency>
  <groupId>software.crldev</groupId>
  <artifactId>multiversx-spring-boot-starter-reactive</artifactId>
  <version>1.2.0</version>
</dependency>
```

- Gradle (in your build.gradle file)

```
dependencies {
  implementation(group: 'software.crldev', name: 'multiversx-spring-boot-starter-reactive', version: '1.2.0')
}
```

- And some other required dependencies for cryptographic functions:

```
implementation group: 'org.bouncycastle', name: 'bcmail-jdk15on', version: '1.70'
implementation group: 'org.bouncycastle', name: 'bcpkix-jdk15on', version: '1.70'
implementation group: 'org.bouncycastle', name: 'bcprov-jdk15on', version: '1.70'
implementation group: 'org.bouncycastle', name: 'bcprov-ext-jdk15on', version: '1.70'
implementation group: 'org.bitcoinj', name: 'bitcoinj-core', version: '0.16.2'
```

## Documentation

First part of integration is setting up ```application.yaml```. If nothing is set, defaults will be
used.

```yml
spring:
  multiversx:
    client:
      (optional) gateway: devnet (default) (mainnet | testnet | devnet)
      (optional) customProxyUrl: https://custom-proxy.com
      readTimeoutMillis: 10000 (default)
      writeTimeoutMillis: 10000 (default)
```

The project uses object notations from the **blockchain terminology** like **Address, Wallet,
Transaction, Nonce, Gas, Signature** etc ... so it's required to be familiar with them.

An **[Address](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/account/Address.java)**
can be instantiated in two ways:

```
- fromHex (public key in HEX String)
- fromBech32 (address in Bech32 String)
```

A **[Wallet](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/wallet/Wallet.java)**
is used for **signing transactions**. It can be instantiated in multiple ways:

```
- fromPrivateKeyBuffer (private key in byte[] format)
- fromPrivateKeyHex (private key in HEX String)
- fromPemFile (using a PEM file as an input) (both File & reactive FilePart supported)
- fromMnemonic (using a mnemonic phrase)
- fromKeyStore (not yet implemented)
```

We can generate a mnemonic phrase by
using **[MnemonicsUtils](src/main/java/software/crldev/multiversxspringbootstarterreactive/util/MnemonicsUtils.java)**
.

The interaction with the MultiversX Network is done with the help of a set components called **
Interactors**, which provide all the required functionalities based on segregated parts of the
network:

**[Account Interactor](src/main/java/software/crldev/multiversxspringbootstarterreactive/interactor/account/MxAccountInteractor.java)**

```
- getAccountInfo
- getBalance
- getNonce
- getTransactions
- getStorageValue
- getStorage
```

**[Block Interactor](src/main/java/software/crldev/multiversxspringbootstarterreactive/interactor/block/MxBlockInteractor.java)**

```
- queryHyperblockByNonce
- queryHyperblockByHash
- queryShardBlockByNonceFromShard
- queryShardBlockByHashFromShard
```

**[Network Interactor](src/main/java/software/crldev/multiversxspringbootstarterreactive/interactor/network/MxNetworkInteractor.java)**

```
- getNetworkConfig
- getShardStatus
- getNodeHeartbeatStatus
```

**[Transaction Interactor](src/main/java/software/crldev/multiversxspringbootstarterreactive/interactor/transaction/MxTransactionInteractor.java)**

```
- sendTransaction
- sendBatchOfTransactions
- simulateTransaction
- estimateTransactionCost
- queryTransactionInfo
- queryTransactionStatus
```

The Transaction Interactor has methods used for a more granular approach to transaction operations.\
In order to create a sendable transaction, we must first create an instance of a transaction
using **[Transaction](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/transaction/Transaction.java)**
domain object (setting **nonce, gasLimit, version** etc), then sign it using a wallet and transform
it to a payload for the Transaction Interactor (using **toSendable()** method).

For a more simple way of doing transaction operations, the interactor also has overloaded methods
for **sending, simulating and estimating.**
The methods are abstracting the complexity of transaction creation: automatically assigns proper
nonce value, computes fee based on data input and applies the signature before execution. The
required inputs are
a **[Wallet](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/wallet/Wallet.java)**
and the following payload with minimum necessary data:

**[TransactionRequest](src/main/java/software/crldev/multiversxspringbootstarterreactive/interactor/transaction/TransactionRequest.java)**

```
 - receiver address
 - value
 - data
 - gas limit (optional)
```

Example usage:

```java
@Autowired MxTransactionInteractor interactor;

    Mono<TransactionHash> sendTransaction(File pemFile){
    var wallet=WalletCreator.fromPemFile(pemFile);

    var tRequest=TransactionRequest.builder()
    .receiverAddress(Address.fromBech32("erd1gklqdvxxxxxxxxxxxxxxxxxxxxx"))
    .value(Balance.fromEgld(3.5))
    .data(PayloadData.fromString("hello MultiversX"))
    .build();

    return interactor.sendTransaction(wallet,tRequest);
    }
```

**[Smart Contract Interactor](src/main/java/software/crldev/multiversxspringbootstarterreactive/interactor/smartcontract/MxSmartContractInteractor.java)**

```
- callFunction
- query
- queryHex
- queryString
- queryInt
```

This component has methods which interact with the smart contracts on the network (obviously).

In order to call a smart contract function, we need to pass an instance of ContractFunction:

**[ContractFunction](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/smartcontract/ContractFunction.java)**

```
 - smart contract address
 - function name
 - array of arguments
 - value
 - gas limit (optional)
```

Example usage:

```java
@Autowired MxSmartContractInteractor interactor;

    Mono<TransactionHash> callFunction(File pemFile){
    var wallet=WalletCreator.fromPemFile(pemFile);

    var function=ContractFunction.builder()
    .smartContractAddress(Address.fromBech32("erd1xxxxxxxxxxxxxxxxxxxx8llllsh6u4jp"))
    .functionName(FunctionName.fromString("addName"))
    .args(List.of(FunctionArg.fromString("MultiversX"))
    .value(Balance.zero())
    .build();

    return interactor.callFunction(wallet,function);
    }
```

The ContractFunction generates a payload based on function name and args (HEX encoded), creates,
assigns nonce, gas (if not specified, default is used), signs and executes a transaction.

Also, for querying we can use the following object:

**[ContractQuery](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/smartcontract/ContractQuery.java)**

```
 - smart contract address
 - function name
 - array of arguments
 - value
 - caller address (optional)
```

**[ESDT Interactor](src/main/java/software/crldev/multiversxspringbootstarterreactive/interactor/esdt/MxESDTInteractor.java)**

```
- processEsdtTransaction
- getTokensForAccount
- getTokenRolesForAccount
- getAllTokens
- getTokenProperties
- getTokenSpecialRoles
- getNftDataForAccount
- getNftSftForAccount
- getTokensWithRole
```

This component has methods which cover all the ESDT related transaction and queries on the network.

```processEsdtTransaction``` takes an ***ESDTTransaction*** arg, which has multiple implementations:

* **[ESDTIssuance](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/ESDTIssuance.java)** (
  can be of type FUNGIBLE, SEMI_FUNGIBLE, NON_FUNGIBLE or META)
* **[ESDTGlobalOp](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/ESDTGlobalOp.java)** (
  can be of type PAUSE, UNPAUSE, FREEZE, UNFREEZE or WIPE)
* **[ESDTLocalOp](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/ESDTLocalOp.java)** (
  can be of type MINT or BURN)
* **[ESDTTransfer](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/ESDTTransfer.java)**
* **[ESDTNFTMultiTransfer](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/ESDTNFTMultiTransfer.java)**
* **[ESDTUpgrade](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/ESDTUpgrade.java)**
* **[ESDTOwnershipTransfer](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/ESDTOwnershipTransfer.java)**
* **[ESDTRoleAssignment](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/ESDTRoleAssignment.java)** (
  can be SET or UNSET)
* **[NFTCreation](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/NFTCreation.java)**
* **[NFTAttributesUpdate](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/NFTAttributesUpdate.java)**
* **[NFTAddURI](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/NFTAddURI.java)**
* **[NFTCreationRoleTransfer](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/NFTCreationRoleTransfer.java)**
* **[NFTStopCreation](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/NFTStopCreation.java)**
* **[NFTSFTLocalOp](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/NFTSFTLocalOp.java)** (
  can be of type ADD or BURN)
* **[NFTSFTGlobalOp](src/main/java/software/crldev/multiversxspringbootstarterreactive/domain/esdt/NFTSFTGlobalOp.java)** (
  can be of type FREEZE, UNFREEZE or WIPE)

Example on how to issue a fungible ESDT:

```java
@Autowired MxESDTInteractor interactor;

    Mono<TransactionHash> issueEsdt(File pemFile){
    var wallet=WalletCreator.fromPemFile(pemFile);

    var transaction=ESDTIssuance.builder()
    .type(Type.FUNGIBLE)
    .tokenName(TokenName.fromString("Fung Token"))
    .tokenTicker(TokenTicker.fromString("FNGTNK"))
    .initialSupply(TokenInitialSupply.fromNumber(BigInteger.TEN))
    .decimals(TokenDecimals.fromNumber(2))
    .properties(Set.of(
    new TokenProperty(TokenPropertyName.CAN_FREEZE,true),
    new TokenProperty(TokenPropertyName.CAN_CHANGE_OWNER,true),
    new TokenProperty(TokenPropertyName.CAN_ADD_SPECIAL_ROLES,true)));

    return interactor.processEsdtTransaction(wallet,transaction);
    }
```

For all transaction, the gas limit is already configured, but you can always set a custom value.

The rest of the ESDT operations are done in a similar fashion. NFT, SFT and META creation are also
made super easy. You can follow
the **[MultiversX ESDT documentation](https://docs.multiversx.com/tokens/esdt-tokens/)** where you
have the steps for all operations regarding ESDT, which are all covered by this framework.

<br>

For more details regarding the implementation, please consult the
project's **[official Javadoc documentation](https://crldev.software/docs/elrond-spring-boot-starter-reactive/)**
.

## Demo

You can find an example of a spring-boot service using this
framework **[HERE](https://github.com/crldev-software/multiversx-spring-boot-demo)**.

## Next features

In the next releases the following features have been planned:

- Account storage API
- Wallet Connect integration
- Wallet Creator - method to instantiate wallet from password-protected JSON keystore file
- Other enhancements

## Changelog

[All notable features and changes to this project will be documented in CANGELOG.md file](CHANGELOG.md)

## Contributing

Contributions are always welcome!

You can get in touch with me using the links below and figure out together how to make the project
better.

Also, if you appreciate my effort and want to help me develop & maintain the MultiversX Spring Boot
Framework, you can buy me some coffee via xPortal.

## 🔗 Links

[![portfolio](https://img.shields.io/badge/crldev.software-red?style=for-the-badge&logo=noi&logoColor=white)](https://crldev.software)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/carlo-cristian-stanciu)
[![twitter](https://img.shields.io/badge/Twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/carlo_stanciu)



  