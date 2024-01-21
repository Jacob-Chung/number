# number
This is a SpringBoot API to call a bunch of URL to retrive result numbers. Then my API sort and merge these numbers and return the result as JSON format. I am using Java 21, SpringBoot 3 and Webclient. Follow below steps to start and test. 



## Base Function

A SpringBoot API that exposes an endpoint "/numbers". This endpoint receives a list of URLs through a "GET" query parameter. 

eg: http://127.0.0.1:8080/numbers?urlList=http://127.0.0.1:8090/primes&urlList=http://127.0.0.1:8090/fibo&urlList=http://127.0.0.1:8090/rand

When the "/numbers" endpoint is called, your service shall retrieve each of the URLs specified in the query parameter.
Each URL returns a JSON data structure that looks similar to this:
{"numbers”: [50,4,18,24,90,88,99,98,56,47]}

The JSON data structure contains an object with a key named "numbers", and a value that is a list of numbers.
After retrieving each of these URLs, the service shall merge the numbers coming from all URLs, sort them in
ascending order and make sure that each number appears only once in the result.
The endpoint shall then return a JSON data structure like in the example above with the result as the list of
numbers.
If an URL is not valid or does not return the correct result simply ignore it.

## How to start?

1. Start test server.

For testing purposes, there is a server, which listens on port 8090 and provides the endpoints /primes, /fibo, /odd, and /rand.
Please, run the example server by using this command.
docker run --detach --publish 8090:8090 emanuelschmoczer/coding-challenge-test-server:latest
And try out some calls before you start. (Note that the test server simulates Fmeouts and errors):
curl http://127.0.0.1:8090/primes
curl http://127.0.0.1:8090/fibo
curl http://127.0.0.1:8090/odd
curl http://127.0.0.1:8090/rand

2. Start SpringBoot Appication by maven and send request to: 
http://localhost:8080/number?urlList=http://127.0.0.1:8090/primes&urlList=http://127.0.0.1:8090/fibo&urlList=http://127.0.0.1:8090/odd&urlList=http://127.0.0.1:8090/rand

## Key features.

1. Use TreeSet to be DataType to do sorting the merging.

2. Use WebClient to call URLs asynchronizely.

3. Limit URLs response within 500ms but still loading the result at the backend and cache it into CaffeineCache.


