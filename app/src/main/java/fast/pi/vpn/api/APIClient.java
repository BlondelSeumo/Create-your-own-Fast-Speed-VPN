package fast.pi.vpn.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anupamchugh on 05/01/17.
 */

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance(String base) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(base)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
