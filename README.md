# Documentation

## System Capabilities

The PowerQoPE system is equipped to assist Android phone users in taking charge of their device's security. In current implementation, the system can be used to collect measurement data and store it in a repository for further analysis. All the security decisions made by the system are static and based on what has been observed previously by experimentation. In future work, appropriate machine learning techniques can be applied on the collected data to make user-specific security decisions.

A measurements engine forms the heart of the PowerQoPE system. A user can initiate three types of measurements from the Android application. These include speed tests, HTTP tests and video tests. Speed tests measure the latency, upload and download speed of data transfer between a nearest server and the user. HTTP tests provide insights about DNS response time, page load time and other related parameters when a website (either user-defined or system-defined) is visited from within the app. Video tests measure the buffer, load time and bandwidth for a system-defined video file. In the current implementation, these tests can only be initiated by the user. In future, they can be scheduled on user's phone on the experimenter's demand.

For users who may be unfamiliar with terms like DNS, filters, VPN, cipher suites etc. the system provides three main security configurations - `low`, `medium` and `high`. Users can choose these options depending on the observed performance of applications that reside within their phones. As per previous experiments, if a stricter security level is chosen, the performance of applications that access internet is expected to degrade for poorer connections. Thus, users with a very high quality internet connection can enjoy the benefits of maximum security that could be achieved using this app.

For a technically sound user, the app provides an `Advanced options` radio button. On tapping this button, they can choose the specific DNS provider as well as the type of filter (family, advertisements, security) depending on whether it's available with the provider or not. Since the list of supported cipher suites is fairly large, the app only provides the category of cipher (low, medium, high) as an option. If the user selects a specific cipher category, then all ciphers belonging to that category are sent to any subsequent HTTP request made from within the app.

## System Architecture and Design Choices

The PowerQoPE system's architecture consists of the below components:

1. **Orchestration server** : This is a centralized server which is statically configured to provide the best possible security configuration for a user. A phone can be assigned a DNS filter (DoH, DoT or Do53) based on user's network type and desired level of security. This server also communicates with the central repository for accessing measurements and their results.
2. **Android application** : The application is the component that resides in the users' phones. It communicates with the orchestration server using the websockets protocol. Whenever a user makes a security choice, their phone's current network state is sent to the orchestration server on the basis of which a decision is made.
3. **DNS Proxy server** : The android application described above uses an OpenVPNService for enforcing a configuration involving remote VPN. The implementation for OpenVPNService is based on OpenVPN - a virtual private network system that implements techniques to create secure point-to-point or site-to-site connections in routed or bridged configurations and remote access facilities. Several freely available OpenVPN servers can be used for experimentation, but we decide to choose only Japan-based servers as they are meant for academic use only. In order to not re-invent the wheel, we also decided to extend the implementation of the EazyVPN-free app for our purposes. This app only supports IP address of a custom DNS server that runs on port 53. Any request to a specific website is resolved using this DNS server, and then a route to that website is added to the tunnel in order to provide a secure connection. Since we were looking for a VPN only in high configuration, where there's a high likelihood of DoH being used, we wanted this DNS server to be the URL of a recursive resolver, like https://1.1.1.1/dns-query. The only way this could be achieved was to set up a proxy server on port 53 and pass it to the openvpn profile for all DoH configurations. In all other cases, i.e DoT and DoH, the Android app makes use of a local VPN server to resolve websites.

## Testing environment setup

1. The first step is to get the orchestration server running in a docker container. 
   1. Clone the below repository:
      ```
      git clone https://github.com/staveesh/qosmon-request-handler
      ```
   2. Change directory to qosmon-request-handler:
      ```
      cd qosmon-request-handler
      ```
   3. Checkout the `powerqope` branch:
      ```
      git checkout powerqope
      ```
   4. Create an empty `.env` file
      ```
      touch .env
      ```
   5. Paste the below contents into the `.env` file:
      ```
      INFLUXDB_USERNAME=root
      INFLUXDB_PASSWORD=root
      INFLUXDB_NAME=powerqope
      INFLUXDB_PORT=8086
      MONGODB_USERNAME=root
      MONGODB_PASSWORD=root
      MONGODB_NAME=qosmon
      MONGODB_PORT=27017
      MONGODB_HOST=mongodb
      HTTP_SERVER_PORT=7800
      FILE_SERVER_HOSTNAME=
      MILLISECONDS_TILL_RETRY_CONNECT=120000
      MILLISECONDS_TILL_ANALYZE_PCAPFILES=86400000
      MILLISECONDS_INIT_DELAY=60000
      FILE_MONITOR_DELAY=5000
      NUM_RETRY_CONNECT=3
      SCHEDULING_ALGO_NAME=rr
      ```
      Feel free to set up your own usernames and passwords. Also make sure that no other process is running on `HTTP_SERVER_PORT`, `INFLUXDB_PORT`, or `MONGODB_PORT`. Scheduling algorithm defaults to `Round Robin` for now. But this can be changed to `AOSD`, `DOSD`, or `EDF`. Since the current version only supports user measurements, this wouldn't have much effect on scheduling performance.
   6. Launch the below command to start the orchestration server:
      ```
      docker-compose up --build
      ```
2. The next step is to create a build of the PowerQoPE android app that points to the above server.
3. A Technitium DNS proxy should also be running and the app should know it's address as well. For now, the app is programmed with both the orchestration server and the proxy running at `lab.enockmbewe.com`. If this is to be changed in future, a new build of the app has to be created. The below links may prove useful for setting up the proxy or for playing around with it:
   1. https://blog.technitium.com/2020/07/how-to-host-your-own-dns-over-https-and.html
   2. https://github.com/TechnitiumSoftware/DnsServer/blob/master/APIDOCS.md