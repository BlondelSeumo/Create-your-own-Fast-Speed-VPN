package fast.pi.vpn.api;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApis {
    @GET("/")
    Call<ApiResponse> requestip(@Query("format") String formate);


}
