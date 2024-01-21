# number
This is a SpringBoot API to call a bunch of URL to retrive result numbers. Then my API sort and merge these numbers and return the result as JSON format. I am using Java 21, SpringBoot 3 and Webclient. Follow below steps to start and test. 



# SpringBoot API

This project is a SpringBoot API that exposes an endpoint `/numbers`.

## Base Function

The `/numbers` endpoint receives a list of URLs through a "GET" query parameter.

Example:
```sh
http://127.0.0.1:8080/numbers?urlList=http://127.0.0.1:8090/primes&urlList=http://127.0.0.1:8090/fibo&urlList=http://127.0.0.1:8090/rand
````

Each URL returns a JSON data structure that looks similar to this:
```json
{
  "numbers”: [50,4,18,24,90,88,99,98,56,47]
}
```

The service retrieves each of these URLs, merges the numbers from all URLs, sorts them in ascending order, and ensures that each number appears only once in the result. The endpoint then returns a JSON data structure with the result as the list of numbers. If a URL is not valid or does not return the correct result, it is ignored.

## Getting Started
1. Start the Test Server
   For testing purposes, a server is provided which listens on port 8090 and provides the endpoints /primes, /fibo, /odd, and /rand.

Run the example server using this command:
```sh
docker run --detach --publish 8090:8090 emanuelschmoczer/coding-challenge-test-server:latest
```
Try out some calls before you start:
```sh
curl http://127.0.0.1:8090/primes
curl http://127.0.0.1:8090/fibo
curl http://127.0.0.1:8090/odd
curl http://127.0.0.1:8090/rand
```

2. Start the SpringBoot Application

Start the SpringBoot Application by Maven and send a request to:
```sh
http://localhost:8080/number?urlList=http://127.0.0.1:8090/primes&urlList=http://127.0.0.1:8090/fibo&urlList=http://127.0.0.1:8090/odd&urlList=http://127.0.0.1:8090/rand
```

## Key features.

1. Use TreeSet to be DataType to do sorting the merging.

2. Use WebClient to call URLs asynchronizely.

3. Limit URLs response within 500ms but still loading the result at the backend and cache it into CaffeineCache.


