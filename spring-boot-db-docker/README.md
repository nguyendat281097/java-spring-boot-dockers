I. Build Springboot + PostgreSQL with docker

1. Initiate a new Springboot project + PostgreSQL
2. In ./, run 'mvn clean package -DskipTests' to build project without testing
3. In ./, create a new 'Dockerfile' file with the content:
	FROM openjdk:11
	MAINTAINER nvd.com
	COPY target/springboot-databases-docker.jar springboot-databases-docker.jar
	ENTRYPOINT ["java","-jar","/springboot-databases-docker.jar"]
	EXPOSE 8080
4. To create an image from our Dockerfile, we have to run ‘docker build' like before:
	docker build --tag=springboot-databases-docker:latest .
5. Create a new docker-compose.yml with the content:
	version: '2'
	services:
	  API:
		image: 'springboot-databases-docker:latest'
		ports:
		  - "8888:8080"
		depends_on:
		  PostgreSQL:
			condition: service_healthy
		environment:
		  - SPRING_DATASOURCE_URL=jdbc:postgresql://PostgreSQL:5432/postgres-demo
		  - SPRING_DATASOURCE_USERNAME=admin
		  - SPRING_DATASOURCE_PASSWORD=admin
		  - SPRING_JPA_HIBERNATE_DDL_AUTO=update

	  PostgreSQL:
		image: postgres
		ports:
		  - "15432:5432"
		environment:
		  - POSTGRES_PASSWORD=admin
		  - POSTGRES_USER=admin
		  - POSTGRES_DB=postgres-demo
		healthcheck:
		  test: ["CMD-SHELL", "pg_isready -U admin -d postgres-demo"]
		  interval: 10s
		  timeout: 5s
		  retries: 5

	(API.environment: replace the information in application.properties)

6. Check docker-compose for syntax-errors by command: 
	docker-compose config
7. Create the defined containers, start containers by command:
	docker-compose up
8. To stop the containers, remove them from Docker and remove the connected networks from it, run command:
	docker-compose down
	
II. Build Springboot + MySQL with docker

1. Initiate a new Springboot project + MySQL
2. In ./, run 'mvn clean package -DskipTests' to build project without testing
3. In ./, create a new 'Dockerfile' file with the content:
	FROM openjdk:11
	MAINTAINER nvd.com
	COPY target/springboot-databases-docker.jar springboot-databases-docker.jar
	ENTRYPOINT ["java","-jar","/springboot-databases-docker.jar"]
	EXPOSE 8080
4. To create an image from our Dockerfile, we have to run ‘docker build' like before:
	docker build --tag=springboot-databases-docker:latest .
5. Create a new docker-compose.yml with the content:
	version: '3'
	services:
	  mysqldb:
		image: mysql:5.7
		restart: unless-stopped
		env_file: ./.env
		environment:
		  - MYSQL_ROOT_PASSWORD=$MYSQLDB_ROOT_PASSWORD
		  - MYSQL_DATABASE=$MYSQLDB_DATABASE
		ports:
		  - $MYSQLDB_LOCAL_PORT:$MYSQLDB_DOCKER_PORT
		healthcheck:
		  test: ["CMD-SHELL", "mysqladmin" ,"ping", "-h", "localhost"]
		  interval: 10s
		  timeout: 5s
		  retries: 5

	  API:
		image: 'springboot-databases-docker:latest'
		ports:
		  - $SPRING_LOCAL_PORT:$SPRING_DOCKER_PORT
		depends_on:
		  mysqldb:
			condition: service_healthy
		build: ./
		restart: on-failure
		env_file: ./.env
		environment:
		  SPRING_APPLICATION_JSON: '{
			"spring.datasource.url"  : "jdbc:mysql://mysqldb:$MYSQLDB_DOCKER_PORT/$MYSQLDB_DATABASE?useSSL=false",
			"spring.datasource.username" : "$MYSQLDB_USER",
			"spring.datasource.password" : "$MYSQLDB_ROOT_PASSWORD",
			"spring.jpa.properties.hibernate.dialect" : "org.hibernate.dialect.MySQL5InnoDBDialect",
			"spring.jpa.hibernate.ddl-auto" : "update"
		  }'
		volumes:
		  - .m2:/root/.m2
		stdin_open: true
		tty: true
		
	volumes:
	  db:

6. Check docker-compose for syntax-errors by command: 
	docker-compose config
7. Create ./.env that where will contain all environment information:
	MYSQLDB_USER=root
	MYSQLDB_ROOT_PASSWORD=123456
	MYSQLDB_DATABASE=mysql_demo
	MYSQLDB_LOCAL_PORT=13306
	MYSQLDB_DOCKER_PORT=3306

	SPRING_LOCAL_PORT=8888
	SPRING_DOCKER_PORT=8080
8. Create the defined containers, start containers by command:
	docker-compose up
9. To stop the containers, remove them from Docker and remove the connected networks from it, run command:
	docker-compose down
	
III. Build Springboot + MSSQL with docker

1. Initiate a new Springboot project + MSSQL
2. In ./, run 'mvn clean package -DskipTests' to build project without testing
3. In ./, create a new 'Dockerfile' file with the content:
	FROM openjdk:11
	MAINTAINER nvd.com
	COPY target/springboot-databases-docker.jar springboot-databases-docker.jar
	ENTRYPOINT ["java","-jar","/springboot-databases-docker.jar"]
	EXPOSE 8080
4. To create an image from our Dockerfile, we have to run ‘docker build' like before:
	docker build --tag=springboot-databases-docker:latest .
5. Create a new docker-compose.yml with the content:
	version: '4'
	services:
	  sql-server-db:
		image: mcr.microsoft.com/mssql/server:2017-latest
		ports:
		  - "11433:1433"
		environment:
		  SA_PASSWORD: "@Dn12345678"
		  ACCEPT_EULA: "Y"

	  API:
		image: 'springboot-databases-docker:latest'
		ports:
		  - "8888:8080"
		depends_on:
		  - sql-server-db

6. Check docker-compose for syntax-errors by command: 
	docker-compose config
7. Create the defined containers, start containers by command:
	docker-compose up
8. To stop the containers, remove them from Docker and remove the connected networks from it, run command:
	docker-compose down