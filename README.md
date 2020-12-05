# Instructions for development environment setup

1. Clone analyzer project (dependency for request-handler):
   ```
   git clone https://github.com/staveesh/request-handler.git
   ```
2. Change to analyzer directory:
   ```
   cd analyzer
   ```

3. Install its dependencies:
   ```
   mvn clean install
   ``` 
   
4. Change to parent directory:
   ```
   cd ..
   ```
5. Clone the request-handler project:
   ```
   git clone https://github.com/staveesh/request-handler.git
   ```
6. Change directory to request-handler:
   ```
   cd request-handler
   ```
7. Build dependencies of request-handler:
   ```
   mvn clean install
   ```
8. Run the following command from the request-handler to launch the server:
   ```
    docker-compose up
   ```