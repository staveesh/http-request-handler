# Instructions for development environment setup

1. Clone analyzer project (a dependency for request-handler)
   ```
   git clone https://github.com/staveesh/request-handler.git
   ```
2. ```
   cd analyzer
   ```

3. ```
   mvn clean install
   ``` 
   
4. ```
   cd ..
   ```
5. ```
   git clone https://github.com/staveesh/request-handler.git
   ```
6. ```
   cd request-handler
   ```
7. ```
   mvn clean install
   ```
8. Run the following command from the request-handler:
   ```
    docker-compose up
   ```