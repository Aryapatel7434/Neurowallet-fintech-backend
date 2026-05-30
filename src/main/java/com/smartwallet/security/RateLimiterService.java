package com.smartwallet.security;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {
    //User -> Number of Requests
    private final Map<String,Integer> requestCount =
            new HashMap<>();
     //User -> FIrst Requesr Time
    private final Map<String,LocalDateTime> requestTime =
            new HashMap<>();

    private static final int MAX_REQUESTS = 20;

    public boolean isAllowed(String key){
         //Get current Time
        LocalDateTime now =
                LocalDateTime.now();
        //First Requesr check
        if(!requestTime.containsKey(key)){

            requestTime.put(key, now);
            requestCount.put(key, 1);

            return true;
        }

        LocalDateTime firstRequest =
                requestTime.get(key);

        if(firstRequest.plusMinutes(1)
                .isBefore(now)){

            requestTime.put(key, now);
            requestCount.put(key, 1);

            return true;
        }

        int count =
                requestCount.get(key);

        if(count >= MAX_REQUESTS){

            return false;
        }

        requestCount.put(
                key,
                count + 1
        );

        return true;
    }
}