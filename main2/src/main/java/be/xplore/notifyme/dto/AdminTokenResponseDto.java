package be.xplore.notifyme.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminTokenResponseDto {

  @SerializedName(value = "access_token")
  private String accessToken;
  @SerializedName(value = "expires_in")
  private int expiresIn;
  @SerializedName(value = "refresh_expires_in")
  private int refreshExpiresIn;
  @SerializedName(value = "token_type")
  private String tokenType;
  @SerializedName(value = "not-before-policy")
  private int notBeforePolicy;
  private String scope;
}
