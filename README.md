# HawkCD
## Continuous Delivery Software

[![Join the chat at https://gitter.im/rndsolutions/hawkcd](https://badges.gitter.im/rndsolutions/hawkcd.svg)](https://gitter.im/rndsolutions/hawkcd?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[<img src="https://img.shields.io/badge/build%20by-hawkcd-orange.svg">](http://hawkcd.io/)




HawkCD is an open source  cross-platform application release automation server. It has been built with the Continuous Delivery practices and principles in mind to help agile software development teams orchestrate and automate their software Build, Test and Deploy workflows in the SDLC  

###Setting up build enviroment 

```bash

#git
sudo apt-get install git

#java
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer

#gradle
sudo add-apt-repository ppa:cwchien/gradle
sudo apt-get update
sudo apt-get install gradle

#nodejs
sudo apt-get install build-essential
curl -sL https://deb.nodesource.com/setup_6.x | sudo -E bash -
sudo apt-get install -y nodejs

#gulp & bower
sudo npm install gulp -g
sudo npm install bower -g
```
### Build *.tar.gz - agent & HServer packages
```bash

./build1.sh build_all

```


###License

HawkCD is an open source project, sponsored by <a href="http://rnd-solutions.net/">R&D Solutions Ltd.</a> under the <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache License, Version 2.0</a>.

