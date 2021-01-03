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
      INFLUXDB_USERNAME=#value
      INFLUXDB_PASSWORD=#value
      INFLUXDB_NAME=#value
      INFLUXDB_PORT=#value
      MONGODB_USERNAME=#value
      MONGODB_PASSWORD=#value
      MONGODB_NAME=#value
      MONGODB_PORT=#value
      MONGODB_HOST=#value
      HTTP_SERVER_PORT=#value
      TCP_SERVER_PORT=#value
      FILE_SERVER_HOSTNAME=#value
      MILLISECONDS_TILL_RETRY_CONNECT=#value
      MILLISECONDS_TILL_ANALYZE_PCAPFILES=#value
      MILLISECONDS_INIT_DELAY=#value
      FILE_MONITOR_DELAY=#value
      NUM_RETRY_CONNECT=#value
   ```

6. Run the following command from the current directory to launch the server:
   ```
   bash start.sh
   ```
7. To monitor server logs, run:
   ```
   docker-compose logs -f -t
   ```