 # Copyright (C) 2016 R&D Solutions Ltd.
 #
 # Licensed under the Apache License, Version 2.0 (the "License");
 # you may not use this file except in compliance with the License.
 # You may obtain a copy of the License at
 #
 # http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.

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
