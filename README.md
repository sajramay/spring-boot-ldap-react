
## Spring Boot Application with ReactJS, Spring Data JPA and Security enabled 

This application is a lightweight Spring Boot and ReactJS outline application which can be used as the basis for your own apps

### Building

Start the Spring Boot development server with hotreload as follows

```
$ mvn clean spring-boot:run -P hotreload
```

Now start the React development server (in the same folder as the package.json) and browse to `http://localhost:3000` for hot-reload of the ReactJS app while you are developing
```
$ cd src/main/frontend
$ npm install
$ npm start
```

You can now make code changes in ReactJS and Java and the code will be hot-reloaded into the running application

### Releasing

Once development is complete, simply run the following from the directory that contains `pom.xml` to create a release version of the application
```
$ mvn clean install -P react
```

If you don't want to use Maven to build the release version of the ReactJS app run the following in the `frontend` directory and then run Maven separately
```
$ cd src/main/frontend
$ npm run build
$ cd -
$ mvn clean install
```

You can now run the app using Java 8 or above as follows
```
$ ${JAVA_HOME}/bin/java -jar target/spring-boot-react-0.0.1-SNAPSHOT.jar
```

### Login Credentials
These are the hard-coded credentials for this demo app and they can be found in `application.properties` Please change these in your version.
```text
Username : user1
Password : password123
```

### Jenkins
Install Jenkins from jenkins.io ensuring that the Pipeline plugin is installed.  Install the following plugins
- Pipeline Maven Integration Plugin
- NodeJS plugin (https://wiki.jenkins.io/display/JENKINS/NodeJS+Plugin)

Configure Java and Maven and set the Maven installation name to 'maven_3_6_0'.  Jenkins will install Maven and Java on demand.

Configure NodeJS in Global Tool configurations and set the installation name to 'NodeJS_11_4_0'

Create a new Pipeline project.  Under Pipeline pick 'Pipeline script from SCM' and set the SCM path to your repo (or this repo for a test)

