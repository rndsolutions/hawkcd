FROM registry.hawkengine.net:5000/hawkbase:0.1
MAINTAINER Radoslav Minchev <rminchev@rnd-solutions.net>

#setup file structure
RUN mkdir -p /usr/src/app/source /usr/src/app/build
WORKDIR /usr/src/app/source
COPY . /usr/src/app/source

RUN gradle build -x test

RUN cp -r build/libs/* /usr/src/app/build/  

WORKDIR /usr/src/app/build
#run when the container is started
CMD ["java", "-jar", "hawk-agentj-all.jar"] 
