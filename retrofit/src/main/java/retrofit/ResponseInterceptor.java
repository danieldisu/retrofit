package retrofit;

import org.apache.commons.io.IOUtils;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by ddiaz on 25/04/14.
 */
public interface ResponseInterceptor {
  ResponseFacade intercept(ResponseFacade responseFacade);

  public class ResponseFacade {

    private final Response originalResponse;
    private Request request;
    private Response newResponse;
    private TypedByteArray originalResponseBody = null;
    private boolean shouldReRunRequestAfterIntercept = false;

    ResponseFacade(Response originalResponse, Request request) {
      final Response originalResponseCopy = new Response(originalResponse.getUrl(), originalResponse.getStatus(), originalResponse.getReason(), originalResponse.getHeaders(), getOriginalResponseBody(originalResponse));
      this.originalResponse = originalResponseCopy;
      this.request = request;
      this.newResponse = originalResponseCopy;
    }

    private void changeResponseBody(String newBody) {
      // TODO
    }

    public void changeStatus(int newStatus) {
      this.newResponse = new Response(newResponse.getUrl(), newStatus, newResponse.getReason(), newResponse.getHeaders(), newResponse.getBody());
    }

    public void changeHeaders(List<Header> headers) {
      this.newResponse = new Response(newResponse.getUrl(), newResponse.getStatus(), newResponse.getReason(), headers, newResponse.getBody());
    }

    public void addHeader(Header header) {
      originalResponse.getHeaders().add(header);
      this.newResponse = new Response(newResponse.getUrl(), newResponse.getStatus(), newResponse.getReason(), newResponse.getHeaders(), newResponse.getBody());
    }

    public TypedByteArray getOriginalResponseBody(Response originalResponse) {
      TypedByteArray newBody = null;
      InputStream is = null;
      try {
        is = originalResponse.getBody().in();
        byte[] bytes = IOUtils.toByteArray(is);
        originalResponseBody = new TypedByteArray(originalResponse.getBody().mimeType(), bytes);
        return originalResponseBody;
      } catch (IOException e) {
        e.printStackTrace();
      }
      return originalResponseBody;
    }

    public int getOriginalResponseStatus() {
      return originalResponse.getStatus();
    }

    protected Response getNewResponse() {
      return this.newResponse;
    }

    public boolean shouldReRunRequestAfter() {
      return shouldReRunRequestAfterIntercept;
    }

    public void setShouldReRunRequestAfterIntercept(boolean shouldReRunRequestAfterIntercept) {
      this.shouldReRunRequestAfterIntercept = shouldReRunRequestAfterIntercept;
    }

    public Response getOriginalResponse() {
      return originalResponse;
    }

    public Request getRequest() {
      return request;
    }
  }

  /**
   * A {@link ResponseInterceptor} which does no modification of requests.
   */
  ResponseInterceptor NONE = new ResponseInterceptor() {
    @Override
    public ResponseFacade intercept(ResponseFacade responseFacade) {
      return responseFacade;
    }
  };
}
