# QoSMon

![Architecture](architecture.png)

This repository corresponds to the "Measurement Server" component in the above architecture. The server accepts job
specifications from a user and distributes them to the Android phones. The job specifications have to follow the below
format:

```json
{
  "jobDescription": {
    "measurementDescription": {
      "type": "<String> : One of ['ping', 'dns_lookup', 'traceroute', 'http', 'tcp_speed_test']",
      "key": "<String> : A unique identifier for the job",
      "startTime": "<String> : An ISO format UTC date string",
      "endTime": "<String> : An ISO format UTC date string",
      "count": "<Integer> : number of nodes to run the job on",
      "intervalSec": 1,
      "priority": "<Integer> : 1-10, 10 being the highest priority",
      "parameters": {
        "target": "<String> : The server against which measurements will be run. Example : www.google.com",
        "server": "null",
        "dirUp": "Boolean : (Only applicable to tcp_speed_test)"
      },
      "instanceNumber": 1
    },
    "nodeCount": 1,
    "jobInterval": {
      "jobIntervalHr": "<Integer> : Job interval hours",
      "jobIntervalMin": "<Integer> : Job interval minutes",
      "jobIntervalSec": "<Integer> : Job interval seconds"
    }
  },
  "requestType": "SCHEDULE_MEASUREMENT",
  "userId": "Anonymous"
}
```

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

3. Checkout the `original` branch.
   ```
   git checkout original
   ```

4. Create a `.env` file inside the current directory:

   ```
   touch .env
   ```

5. Set the following environment variables in the `.env` file:

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
      SCHEDULING_ALGO_NAME=#value (One of [random, rr, edf, aosd, dosd])
   ```

6. Run the following command to launch the server:
   ```
   docker-compose up --build
   ```