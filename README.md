
# Application Bootstrap Approach
### Spring Boot
This application starts with Spring Boot v2 which was created using the helper site at https://start.spring.io

From there the following options were selected
```
web, security, ldap, actuator, jpa, hateoas 
```

The application was then configured to use the H2 in-memory database and fill it with some sample data to serve to the client

```
application.properties
....
spring.datasource.url=jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'db/ddl.sql'\\;RUNSCRIPT FROM 'db/data.sql'
spring.datasource.username=test
spring.datasource.password=test
spring.datasource.driver-class-name=org.h2.Driver
```

```
pom.xml
....
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

### ReactJS and Redux
The client application was created using the Create React App scripts as follows:
```
$ npm i -g create-react-app
$ cd src/main
$ create-react-app frontend
$ cd frontend
$ npm i --save react-redux react-router react-router-dom redux redux-thunk seamless-immutable axios
$ npm install
```

# Setting Up ReactJS
Edit ```package.json``` and add the following "postbuild" line so that the generated ReactJS application can be served from the Spring Boot static resources folder
```
....
"build": "react-scripts build",
"postbuild": "rm -rf ../resources/static/* && mkdir -p ../resources/static && cp -r build/* ../resources/static",
"test": "react-scripts test --env=jsdom",
....
```

In order to communicate with the backend Spring Boot server while using the React dev server, we can add proxy requets to the package.json.  These are ignored when the production app is built.
```
package.json
....
  "proxy": {
    "/api": {
      "target" : "http://localhost:8080/"
    },
    "/auth": {
      "target" : "http://localhost:8080/"
    },
    "/logout": {
      "target" : "http://localhost:8080/"
    }

```


Now start the React development server (in the same folder as the package.json) and browse to `http://localhost:3000` for hot-reload of the ReactJS app while you are developing
```
$ npm start
```

# Setting Up Spring Boot
We also want to hot-reload the server so that we can edit the code and have Spring restart the server after compilation.  
This is only needed for development so we can simply use a Maven profile so that we only use this feature when needed.
```
pom.xml
....
	<profiles>
		<profile>
			<id>hotreload</id>
			<dependencies>
				<dependency>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-devtools</artifactId>
				</dependency>
			</dependencies>
		</profile>
	</profiles>

```  
To build a production version of the app, we can use the `frontend-maven-plugin` in a profile as follows:
```
pom.xml
....
		<profile>
			<id>react</id>
			<build>
				<plugins>
					<plugin>
						<groupId>com.github.eirslett</groupId>
						<artifactId>frontend-maven-plugin</artifactId>
						<version>1.6</version>

						<configuration>
							<nodeVersion>v10.1.0</nodeVersion>
							<npmVersion>7.0.0</npmVersion>
							<workingDirectory>src/main/frontend</workingDirectory>
						</configuration>

						<executions>
							<execution>
								<id>install node and npm</id>
								<goals>
									<goal>install-node-and-npm</goal>
								</goals>
							</execution>

							<execution>
								<id>npm install</id>
								<goals>
									<goal>npm</goal>
								</goals>

								<configuration>
									<arguments>install</arguments>
								</configuration>
							</execution>

							<execution>
								<id>npm run-script build</id>
								<goals>
									<goal>npm</goal>
								</goals>

								<configuration>
									<arguments>run-script build</arguments>
								</configuration>
							</execution>
						</executions>
					</plugin>
				</plugins>
			</build>
		</profile>
```

Start the Spring Boot development server with hotreload as follows

```
mvn clean spring-boot:run -P hotreload
```

# Building a Production Version of the App
Once dev is complete, simply run the following from the directory that contains `pom.xml`
```
mvn clean install -P react
```
You can now run the app using Java 8 or above as follows
```
java -jar target/spring-boot-ldap-react-0.0.1-SNAPSHOT.jar
```
# Some Interesting Parts of the App

### Setting up Spring Boot security
TODO