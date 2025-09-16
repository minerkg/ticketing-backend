
# Stage 1: Build WAR

FROM eclipse-temurin:24-jdk AS builder

# Install Gradle manually (8.14.3)
RUN apt-get update && apt-get install -y wget unzip \
    && wget https://services.gradle.org/distributions/gradle-8.14.3-bin.zip -O gradle.zip \
    && unzip gradle.zip -d /opt \
    && ln -s /opt/gradle-8.14.3/bin/gradle /usr/bin/gradle \
    && rm gradle.zip

WORKDIR /app

# Copy Gradle config and source
COPY build.gradle settings.gradle ./
COPY src ./src



# Build WAR (skip tests for faster builds if you want)
RUN gradle clean build --no-daemon -x test

# Stage 2: Runtime with Tomcat + JDK 24

FROM eclipse-temurin:24-jdk

WORKDIR /opt


RUN apt-get update && apt-get install -y wget \
    && wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.46/bin/apache-tomcat-10.1.46.tar.gz \
    && tar xzf apache-tomcat-10.1.46.tar.gz \
    && mv apache-tomcat-10.1.46 tomcat \
    && rm apache-tomcat-10.1.46.tar.gz

WORKDIR /opt/tomcat/webapps

# Remove default ROOT app
RUN rm -rf ROOT

COPY --from=builder /app/build/libs/*.war api-v1.war

#COPY .env /opt/tomcat/webapps/.env


EXPOSE 8080
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
