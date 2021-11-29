package om.sas.coursecafe.view.fragments;

import om.sas.coursecafe.view.notification.MyResponse;
import om.sas.coursecafe.view.notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA5fYVYLE:APA91bFeO7sO8240FUG-v4rWL8hlugH-s-A8VXpUxeqP6N9qmL6bWXRYx8JvmE0QspmW-dCn79scKN-dcqF_uFSkQsxFB7IGxAP0wr5bLkVepcVB5FPlQPDjxr6F92GYLJ8N0-GesGUj"

            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification (@Body Sender body);

}
