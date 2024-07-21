# Easy Retrofit

## Overview

The `Api` library is an abstract class designed to simplify handling API calls with Retrofit in Android. It provides a structured approach to manage API call states, including success, failure, and loading, with support for optional callbacks and blocking mode.

## Features

- **Success Handling**: Easily handle API responses with customizable callbacks.
- **Failure Handling**: Define actions to take when an API call fails.
- **Loading State Management**: Track and respond to loading state changes.
- **Blocking Mode**: Control whether concurrent API calls are allowed.
- **Customizable Callbacks**: Set callbacks for start, response, failure, end, and busy states.

## Installation

To use the `Api` library in your Android project, follow these steps:

1. **Add the required dependency** 

   ```gradle
   implementation("com.github.mostafa-tamer:EasyRetrofit:2.1.1") 
   ```
   
2. **Add the following to settings.gradle file**

   ```gradle
   dependencyResolutionManagement {
      repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
      repositories {
          google()
          mavenCentral()
          maven("https://jitpack.io")
  
      }
   }
   ```

## Usage

### Basic Example
 
**Configure the API call**

   ```java
   interface ApiService {
       @GET("/data")
       Call<String> getDataFromTheServer();
   }
 
   Retrofit retrofit = RetrofitClient.getInstance();
   ApiService apiService = retrofit.create(ApiService::class.java);

   MyApiCall apiCall = apiService.getDataFromTheServer()
       .setOnStart(() -> {
           // Show loading indicator
       })
       .setOnResponse((response, statusCode) -> {
           // Handle the API response
       })
       .setOnFailure(throwable -> {
           // Handle API call failure
       })
       .setOnEnd(() -> {
           // Hide loading indicator
       })
       .setLoadingStateObserver(isLoading -> {
           // Respond to loading state changes
       })
       .allowBlocking(() -> {
           // Handle busy state when blocking mode is enabled
       });

   apiCall.beginRequest(); // Start the API call
   ```

### Key Methods

- **`setLoadingStateObserver(LoadingStateObserver observer)`**: Set an observer to be notified of loading state changes.
- **`setOnStart(OnStart onStart)`**: Set a callback to be invoked when the API call starts.
- **`setOnResponse(OnResponse<T> onResponse)`**: Set a callback to be invoked when the API call receives a response.
- **`setOnFailure(OnFailure onFailure)`**: Set a callback to be invoked when the API call fails.
- **`setOnEnd(OnEnd onEnd)`**: Set a callback to be invoked when the API call ends.
- **`allowBlocking(OnBusy onBusy)`**: Enable blocking mode and set a callback for when the API call is busy.
- **`unLockBlocking()`**: Unlock blocking mode to allow new API calls.

## API Callbacks

- **`OnStart`**: Invoked when the API call starts.
- **`OnResponse<T>`**: Invoked when a response is received.
- **`OnFailure`**: Invoked when the API call fails.
- **`OnEnd`**: Invoked when the API call ends.
- **`OnBusy`**: Invoked when the API call is busy.

## License

This library is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

Feel free to customize it as needed for your specific requirements!
