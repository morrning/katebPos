package com.hesabix.katebposapp.retrofit;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import org.json.JSONObject;
import retrofit2.http.Query;
import com.hesabix.katebposapp.model.PersonListResponse;
import com.hesabix.katebposapp.model.PersonListRequest;
import retrofit2.http.Path;
import com.hesabix.katebposapp.model.BusinessInfo;
import com.hesabix.katebposapp.model.PersonType;
import com.hesabix.katebposapp.model.PersonPreLabel;
import com.hesabix.katebposapp.model.Person;
import okhttp3.RequestBody;
import com.hesabix.katebposapp.model.CommodityListRequest;
import com.hesabix.katebposapp.model.CommodityListResponse;
import com.hesabix.katebposapp.model.CashDesk;
import com.hesabix.katebposapp.model.SellDocListRequest;
import com.hesabix.katebposapp.model.SellDocListResponse;
import com.hesabix.katebposapp.model.Commodity;
import com.hesabix.katebposapp.model.CommoditySearchRequest;
import com.hesabix.katebposapp.model.ApiResponse;

public interface ApiService {
    @POST("user/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("captcha/image")
    Call<ResponseBody> getCaptchaImage();

    @GET("business/list")
    Call<List<Business>> getBusinesses();

    @GET("user/current/info")
    Call<UserInfo> getUserInfo();

    @POST("user/change/password")
    Call<JSONObject> changePassword(@Body JSONObject passwordData);

    @GET("money/get/all")
    Call<CurrencyResponse> getCurrencies();

    @POST("business/insert")
    Call<JSONObject> insertBusiness(@Body JSONObject businessData);

    @POST("person/list")
    Call<PersonListResponse> getPersonList(@Body PersonListRequest request);

    @GET("business/get/info/{businessId}")
    Call<BusinessInfo> getBusinessInfo(@Path("businessId") int businessId);

    @GET("person/types/get")
    Call<List<PersonType>> getPersonTypes();

    @GET("person/prelabels/list")
    Call<List<PersonPreLabel>> getPersonPreLabels();

    @GET("person/info/{code}")
    Call<Person> getPersonInfo(@Path("code") String code);

    @POST("person/mod/0")
    Call<Person> createPerson(@Body Person person);

    @POST("person/mod/{code}")
    Call<Person> updatePerson(@Path("code") String code, @Body Person person);

    @POST("accounting/rows/search")
    Call<List<JSONObject>> searchAccountingRows(@Body RequestBody requestBody);

    @POST("commodities/search")
    Call<CommodityListResponse> searchCommodities(@Body CommodityListRequest request);

    @GET("cashdesk/list")
    Call<List<CashDesk>> getCashDesks();

    @GET("cashdesk/info/{code}")
    Call<CashDesk> getCashDeskInfo(@Path("code") String code);

    @POST("cashdesk/mod/{code}")
    Call<ApiResponse> modifyCashDesk(@Path("code") String code, @Body CashDesk cashDesk);

    @POST("sell/docs/search")
    Call<SellDocListResponse> searchSellDocs(@Body SellDocListRequest request);

    @POST("accounting/remove")
    Call<JSONObject> removeInvoice(@Body JSONObject request);

    @GET("sell/get/info/{code}")
    Call<JSONObject> getInvoiceInfo(@Path("code") String code);

    @POST("commodity/list/search")
    Call<List<Commodity>> searchCommodities(@Body CommoditySearchRequest request);
}