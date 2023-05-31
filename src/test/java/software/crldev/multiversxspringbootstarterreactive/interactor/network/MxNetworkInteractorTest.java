package software.crldev.multiversxspringbootstarterreactive.interactor.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import software.crldev.multiversxspringbootstarterreactive.api.model.NetworkConfig;
import software.crldev.multiversxspringbootstarterreactive.api.model.NodeHeartbeatStatus;
import software.crldev.multiversxspringbootstarterreactive.api.model.ShardStatus;
import software.crldev.multiversxspringbootstarterreactive.client.MxProxyClient;
import software.crldev.multiversxspringbootstarterreactive.interactor.Helper;
import software.crldev.multiversxspringbootstarterreactive.interactor.WrappedResponses;

@ExtendWith(MockitoExtension.class)
class MxNetworkInteractorTest {

  MxNetworkInteractor networkInteractor;

  @Mock
  MxProxyClient client;

  @BeforeEach
  void setUp() {
    networkInteractor = new MxNetworkInteractorImpl(client);
  }

  @Test
  void getNetworkConfig() {
    var chainId = "D";
    var minGasLimit = BigInteger.valueOf(50_000L);

    var apiResponse = WrappedResponses.GetNetworkConfigResponse
        .builder()
        .networkConfig(NetworkConfig
            .builder()
            .chainId(chainId)
            .minGasLimit(minGasLimit)
            .build())
        .build();

    Helper.verifyInteractionOk(
        client,
        apiResponse,
        () -> networkInteractor.getNetworkConfig(),
        (r) -> {
          assertEquals(chainId, r.getChainId());
          assertEquals(minGasLimit, r.getMinGasLimit());
        }, HttpMethod.GET);
  }

  @Test
  void getShardStatus() {
    var nonce = 10L;
    var currentRound = 5235L;

    var apiResponse = WrappedResponses.GetShardStatusResponse
        .builder()
        .shardStatus(ShardStatus
            .builder()
            .nonce(nonce)
            .currentRound(currentRound)
            .build())
        .build();

    Helper.verifyInteractionOk(
        client,
        apiResponse,
        () -> networkInteractor.getShardStatus("sh1"),
        (r) -> {
          assertEquals(nonce, r.getNonce());
          assertEquals(currentRound, r.getCurrentRound());
        }, HttpMethod.GET);
  }

  @Test
  void getNodeHeartbeatStatus() {
    var identity1 = "node1";
    var identity2 = "node2";

    var apiResponse = WrappedResponses.GetNodeHeartbeatStatusResponse
        .builder()
        .heartbeatstatus(List.of(
            NodeHeartbeatStatus.builder().identity(identity1).build(),
            NodeHeartbeatStatus.builder().identity(identity2).build()
        ))
        .build();

    Helper.verifyInteractionOk(
        client,
        apiResponse,
        () -> networkInteractor.getNodeHeartbeatStatus(),
        (r) -> {
          assertEquals(identity1, r.get(0).getIdentity());
          assertEquals(identity2, r.get(1).getIdentity());
        }, HttpMethod.GET);
  }
}