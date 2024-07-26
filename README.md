# Easy Retrofit


## Overview

`Easy Retrofit` designed to simplify handling API calls with Retrofit in Android. It provides a structured approach to manage API call states, including success, failure, and loading, with support for optional callbacks and blocking mode.

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
   implementation("com.squareup.retrofit2:retrofit:2.9.0")
   implementation("com.squareup.retrofit2:converter-gson:2.9.0")
   ```
   
2. **Add the following to settings.gradle file**

   ```gradle
   dependencyResolutionManagement {
      repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
      repositories {
          google()
          mavenCentral()
          maven("https://jitpack.io")   // add JitPack
      }
   }
   ```

## Usage

### Basic Example

**Define the API Service Interface**

```kotlin
interface ApiService {
    @GET("/data")
    fun getDataFromTheServer(): Call<String>
}
```

**Configure Retrofit with the necessary factory**

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(ApiAdapterFactory()) // Add this adapter factory
    .build()
```

**Create the API Service Instance**

```kotlin
val retrofit = RetrofitClient.getInstance()
val apiService = retrofit.create(ApiService::class.java)
```

**Set Up and Make the API Call**

```kotlin
val apiCall = apiService.getDataFromTheServer()
   .setOnStart {
       // Show loading indicator
   }.setOnResponse { response, statusCode ->
       // Handle the API response
   }.setOnFailure { throwable ->
       // Handle API call failure
   }.setOnEnd {
       // Hide loading indicator
   }.setLoadingStateObserver { isLoading ->
       // Respond to loading state changes
   }.allowBlocking {
       // Handle busy state when blocking mode is enabled
   }

apiCall.beginRequest() // Start the API call
```


## License

This library is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

Feel free to customize it as needed for your specific requirements!
