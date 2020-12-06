# Server setup

## Prerequisites

Make sure that `docker` and `docker-compose` are installed on your host machine.

## Instructions

1. Clone this project:
   ```
   git clone https://github.com/staveesh/request-handler.git
   ```
2. Change the current directory to `request-handler`:
   ```
   cd request-handler
   ```
   
3. Create a `.env` file inside the current directory:

   ```
   touch .env
   ```

4. Set the following environment variables in the `.env` file:

   ```
      INFLUXDB_USERNAME=
      INFLUXDB_PASSWORD=
      INFLUXDB_NAME=
      INFLUXDB_PORT=
      
      MONGODB_USERNAME=
      MONGODB_PASSWORD=
      MONGODB_NAME=
      MONGODB_PORT=
      MONGODB_HOST=
      
      HTTP_SERVER_PORT=
      TCP_SERVER_PORT=
      
      FILE_SERVER_HOSTNAME=
      MILLISECONDS_TILL_RETRY_CONNECT=,
      MINUTES_TILL_ANALYZE_PCAPFILES=,
      MINUTES_INIT_DELAY=,
      FILE_MONITOR_DELAY=,
      NUM_RETRY_CONNECT=
   ```

5. Run the following command from the current directory to launch the server:
   ```
   docker-compose up
   ```