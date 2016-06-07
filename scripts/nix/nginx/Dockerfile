FROM nginx
MAINTAINER Radoslav Minchev <rminchev@rnd-solutions.net>

#copy default configuration to the container
COPY default /etc/nginx/sites-available/default

#replace the default config
COPY nginx.conf /etc/nginx/nginx.conf

#enable the default site
WORKDIR /etc/nginx

RUN mkdir sites-enabled

WORKDIR /etc/nginx/sites-enabled

RUN ln -s ../sites-available/default default



