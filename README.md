# Backend of the Nosto assignment

A service that allows to get perform a conversion operation between two currencies.

You need to make a POST request to `/api` endpoint with a following sample payload:

```
{
    "sourceCurrency": "USD",
    "targetCurrency": "GBP",
    "monetaryValue": 30342.15
}
```
Where `sourceCurrency` and `targetCurrency` must be valid **ISO 4217** currency codes.

The response payload will have the following sample format:
```
{
  "sourceCurrency": "USD",
  "targetCurrency": "GBP",
  "monetaryValue": 30342.15,
  "convertedValue": 23056.33,
  "localeFormattedConvertedValue": "£23,056.33",
  "locale": "en_US"
}
```
Where `localeFormattedConvertedValue` is **Webi18n** representation in respect to **locale**. You can specify the locale in your request using a valid `Accept-Language` header. If this head is not specified in your request, the service with default to the system's value. Locale affects the location of the currency symbol in decimal symbol in `localeFormattedConvertedValue`. However, the currency symbol itself is defined by `targetCurrency` in your request's body.

In case you supply incorect values for the request body, the service will respond with an error status code and message, e.g.: `GBA is not a valid ISO 4217 currency code`

The service itself is obtaing the exchange rate from SWOP third-party service, which requires API key. You need to provide the key if you want launch the application and obtain the conversion instead of **Unauthorized** error response. Instruction how to do that can be found below.

The service is deployed to [Render](https://render.com/) and can be interacted via this address: **https://nosto.onrender.com/api** (initial request can be processed really slowly, due to intentional mechanisms of the free-tier deployments on the platform)

## Launching the service

The project was developed using:

-   **Java** version **17.0.9**
-   **Maven** version **3.8.6**
-   **Docker** version **24.0.5**

If you want to run the application locally, make sure that **Java** and **Maven** are installed. Or if you want to launch the application inside a container, then **Docker**.

### Using **Maven**

1. Clone the project from GitHub
2. Create `.env` file in the root of the project (that is, inside `backend` directory) and specify there these:

```
SWOP_API_KEY=<your SWOP API key>
CACHE_TTL=<integer value that represents seconds>
```
3. Open a terminal, make sure that you are in `backend` directory in run `mvn spring-boot:run`. You optionally run `mvn clean install` prior to `spring-boot:run`.

### Using **Docker**

1. Build Docker Image

There are 2 Dockerfiles in the project. This was done to optimize deployment in **GitHub Actions**. But for a local run, the first Dockerfile will suffice.

```
docker build -t backend --build-arg SWOP_API_KEY=<again your SWOP API key> --build-arg CACHE_TTL=<integer value that represents seconds> .
```

2. Run Docker Container
```
docker run -p 8080:8080 --name backend backend
```
Explicitly map host's port 8080 to container's port 8080
**OR**

```
docker run -P --name backend backend
```
Will map any available port on the host to the exposed port in image (8080).


## Using the application

The system uses **Actuator** dependency, that exposes some useful endpoints, like a `/health` endpoint. When you launch your application, you can test if all is good: [http://localhost:8080/actuator/health]()

The main endpoint, however, is [http://localhost:8080/api]() to which you should make **POST** requests with a json payload of:
```
{
    "sourceCurrency": "USD",
    "targetCurrency": "GBP",
    "monetaryValue": 30342.15
}
```
Where `sourceCurrency` and `targetCurrency` must be valid **ISO 4217** currency codes.
You can also attach localization headers to your request, for example: 
```
Accept-Language: de-DE
```

## Q & A

### 1) Caching implementation

Caching implementation was made using a bean, that contains a **TreeMap** property and handy methods. When client makes a request, the services uses the bean for a lookup and either returns a cached value, or indicated that actual request to SWOP needs to be made. In that case, the response from SWOP is loaded to the cache TreeMap with a Time-To-Live value (TTL), in **seconds**, obtained from the `CACHE_TTL` (default is 60 seconds).
`CacheControl` is also used to attach cache headers to the response of the service to the client. The mechanism is a bit more peculiar, since it determines how much remeaning validy time is left in seconds and then specifies that value for the cache header, so that client's browsers could perform caching on the client side as well.

### 2) Why 2 Dockerfiles

Two Dockerfiles were used to accelerate the building and deployment process. `Dockerfile` is mainly for local runs, when for example it is impossible to install **Java** on the host machine. While `DockerfileProd` is needed in the **GitHub Action workflow** for easier and faster push of the image to the project's registry on **GitHub Packages**.


### 3) Security measures

Besides validation of request body's properties, I have also activated Spring Security measures against CSRF and XSS attacks. However, since the Frontend part on Vue had not been done by the submission date, I had to enable access to the main `/api` endpoint.

### 4) Why **Render** service and not **AWS** or **Heroku**

I initially wanted to build an ECS infrastructure and deploy EC2-based containers: backend, frontend, Redis, InfluxDB, Grafana. I started the work using AWS CDK, but faced challenges:
1) First, access of images in **GitHub Packages**. Even upload of the secret token to Secret Manager and resolution in CDK stack did not help.
2) So I changed to push of the images to both **GitHub Packages** and **ECR**. However, the stack deployment was still failing. Eventually, even sample deployments from AWS, where EC2 was used, were failing. When I tried even to manually deploy my application to Elastic Beanstalk using AWS Console, I got better error log - my account got restrictions on deployments of specific resources in specific regions. Unfortunately, for some resources it was not possible to explicitly set the region, that is why I had been facing resource creation issues (the problem is still ongoing with AWS support).
I then wanted to deploy the app to Heroku, only to realize that they no longer provide fully free deployments, and I did not want to purchase a subscription for one assignment (I use AWS for my hobby projects a lot).
So, I found **Render** service where I was able to deploy the backend app. It successfully connected to my GitHub Packages registry and it even included a web hook, that I added to my CD workflow, so that when a new image is built, the platform pulls it and redeploys the app.
That is why, though initially I wanted to submit a project with a structure like this:
```
Nosto
 ├── .github
 ├── backend
 ├── frontend
 ├── grafana
 ├── IaC
 ├── influxdb
 ├── redis
```
I got only:
```
Nosto
 ├── .github
 ├── backend
```