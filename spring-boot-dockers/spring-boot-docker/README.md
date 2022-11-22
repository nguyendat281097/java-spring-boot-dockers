I. Dockerize a Standalone Spring Boot Application

1. Initiate a new spring boot project
2. In ./, run 'mvn clean package' to build project
3. Run 'java -jar target/spring-boot-docker-1.0.jar' to start spring boot project. (optional)
4. In ./, create a new 'Dockerfile' file with the content:
	FROM openjdk:11
	MAINTAINER nvd.com
	COPY target/spring-boot-docker-1.0.jar spring-boot-docker-1.0.jar
	ENTRYPOINT ["java","-jar","/spring-boot-docker-1.0.jar"]
5. To create an image from our Dockerfile, we have to run ‘docker build' like before:
	docker build --tag=spring-boot-docker-training:latest .
6. To run container from our image:
	docker run -p8888:8080 spring-boot-docker-training:latest
	 - 8888: docker port exposed
	 - 8080: spring boot port
7. If we run the container in detached mode, we can inspect its details, stop it, and remove it with the following commands:
	docker inspect spring-boot-docker-training
	docker stop spring-boot-docker-training
	docker rm spring-boot-docker-training

II. Dockerize Applications in a Composite

1. Initiate a new spring boot project
2. In ./, run 'mvn clean package' to build project
3. Run 'java -jar target/spring-boot-docker-1.0.jar' to start spring boot project. (optional)
4. In ./, create a new 'Dockerfile' file with the content:
	FROM openjdk:11
	MAINTAINER nvd.com
	COPY target/spring-boot-docker-2-1.0.jar spring-boot-docker-product-1.0.jar
	ENTRYPOINT ["java","-jar","/spring-boot-docker-product-1.0.jar"]
5. Create a new docker-compose.yml with the content:
	version: '2'

	services:
		spring-boot-docker-training:
			container_name: spring-boot-docker-training
			build:
				context: spring-boot-docker
				dockerfile: Dockerfile
			image: spring-boot-docker-training:latest
			ports:
				- 8888:8080
			networks:
				- spring-cloud-network
		spring-boot-docker-product:
			container_name: spring-boot-docker-product
			build:
				context: spring-boot-docker-2
				dockerfile: Dockerfile
			image: spring-boot-docker-product:latest
			ports:
				- 9999:8081
			networks:
				- spring-cloud-network
	networks:
		spring-cloud-network:
			driver: bridge
			
	- version: Specifies which format version should be used. This is a mandatory field. Here we use the newer version, whereas the legacy format is ‘1'.
	- services: Each object in this key defines a service, a.k.a container. This section is mandatory.
		- build: If given, docker-compose is able to build an image from a Dockerfile
			- context: If given, it specifies the build-directory, where the Dockerfile is looked-up.
			- dockerfile: If given, it sets an alternate name for a Dockerfile.
		- image: Tells Docker which name it should give to the image when build-features are used. Otherwise, it's searching for this image in the library or remote-registry.
		- networks: This is the identifier of the named networks to use. A given name-value must be listed in the networks section.
	- networks: In this section, we're specifying the networks available to our services. In this example, we let docker-compose create a named network of type ‘bridge' for us. If the option external is set to true, it will use an existing one with the given name.
6. Check docker-compose for syntax-errors by command: 
	docker-compose config
7. Build images, create the defined containers, start containers by command:
	docker-compose up --build
8. To stop the containers, remove them from Docker and remove the connected networks from it, run command:
	docker-compose down
	
III. Scaling Services
A nice feature of docker-compose is the ability to scale services. For example, we can tell Docker to run three containers for the spring-boot-docker-training and two containers for the spring-boot-docker-product.

1. Remove container_name from out docker-compose.yml
2. Update port to tell Docker map a range of ports on the host to one static port inside Docker.
	ports:
		- 18800-18888:8080

	ports:
		- 19900-19999:8081
3. Run below command to scale our services:
	This command will create a single spring-boot-docker-training and a single spring-boot-docker-product
	docker-compose --file docker-compose-scale.yml up -d --build --scale spring-boot-docker-training=1 spring-boot-docker-product=1

	To scale our services:
	docker-compose --file docker-compose-scale.yml up -d --build --scale spring-boot-docker-training=3 spring-boot-docker-product=2