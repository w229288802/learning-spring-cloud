package cn.itcast.storage.exception;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FeignErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(final String methodKey, final Response response) {
    final String error = getResponseBodyAsString(response.body());
    JSONObject jsonObject = JSON.parseObject(error);
    log.error("{} failed with response {}", methodKey, response);
    return new ServiceException(jsonObject.getString("msg"), jsonObject.getString("code"));
  }

  private String getResponseBodyAsString(final Response.Body body) {
    try {
      return Util.toString(body.asReader(StandardCharsets.UTF_8));
    } catch (final IOException e) {
      log.error("Failed to read the response body with error: ", e);
    }
    return null;
  }
}