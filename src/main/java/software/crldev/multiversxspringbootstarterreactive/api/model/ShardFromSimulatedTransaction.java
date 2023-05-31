package software.crldev.multiversxspringbootstarterreactive.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * part of API response when simulating transaction execution
 *
 * @author carlo_stanciu
 */
@Value
@Jacksonized
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ShardFromSimulatedTransaction {

  @JsonProperty("status")
  String status;
  @JsonProperty("failReason")
  String failReason;
  @JsonProperty("hash")
  String hash;

}