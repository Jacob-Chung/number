## 1. The endpoint needs to return the result as quickly as possible, but always within 500 milliseconds.

- This is ambiguous. What should it response if it takes more than 500 milliseconds? Error or still the result or an empty list?

- My interpretation: Only to show error if it takes more than 500 milliseconds. It's unsecure to show the result if it takes more than 500 milliseconds. And Requirement 4 says is to return an empty list only if all URLs returned errors or took too long to respond
and no previous response is stored in the cache.

## 2. All URLs that were successfully retrieved within the given timeframe must influence the result of the endpoint.

- What if all URLs were successfully retrieved within the given timeframe but my application takes more than 500 milliseconds to return the result? Under this case, if I still return error, they cannot influence the result of the endpoint. 

- My interpretation: If all URLs were successfully retrieved within the given timeframe, the result of the endpoint should be all cached. If my application takes more than 500 milliseconds to return the result, it should return the error for the endpoint.

## 3. Cache the response...

- If using cache, it's important to specify the TTL...

- My interpretation: I will use the 60 seconds of the cache because these I found out the response of http://127.0.0.1:8090/odd and http://127.0.0.1:8090/rand would change frequently.