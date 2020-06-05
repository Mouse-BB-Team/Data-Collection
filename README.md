![release-date](https://img.shields.io/date/1591360552?color=red&label=release-date&style=flat-square)
![code-size](https://img.shields.io/github/languages/code-size/Mouse-BB-Team/Data-Collection?style=flat-square)
![tag](https://img.shields.io/github/v/tag/Mouse-BB-Team/Data-Collection?style=flat-square)
[![Website][web-shield]][web-url]
![Contributors](https://img.shields.io/github/contributors/Mouse-BB-Team/Data-Collection?style=flat-square)
![Last-commit](https://img.shields.io/github/last-commit/Mouse-BB-Team/Data-Collection?style=flat-square)
![Languages](https://img.shields.io/github/languages/count/Mouse-BB-Team/Data-Collection?style=flat-square)
[![MIT License][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://mouse-bb.pl">
    <img src="https://user-images.githubusercontent.com/50112357/83871505-761b7080-a730-11ea-8a93-c5429244d6db.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Mouse Behavioral Biometrics</h3>

  <p align="center">
Protection of Web applications with behavioral biometrics.
<br>
<i>Data collection module</i>
    <br />
    <a href="https://mouse-bb.pl">Visit Site</a>
    ·
    <a href="https://github.com/Mouse-BB-Team/Data-Collection/issues">Report Bug</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [License](#license)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements)
* [Authors](#authors)
* [Appendix](#appendix)
  * [Authorization sequence diagram](#authorization-sequence-diagram)
  * [Kubernetes settings](#kubernetes-settings)



<!-- ABOUT THE PROJECT -->
## About The Project

![](https://user-images.githubusercontent.com/50112357/83873411-0c9d6100-a734-11ea-8dbf-0a9c16d042d7.jpeg)

This project was created in the manner of preparing the bachelor's degree thesis on AGH University of Science and Technology, Department of Computer Science, Electronics and Telecommunications.
The main idea is to collect data from user mouse, such as clicks, movement, etc..
The data will be used to feed a Machine Learning model to distinguish Web bots and humans on real commercial websites.


### Built With

**API**:
* [Spring](https://spring.io/) – main framework used for API
* [Maven](https://maven.apache.org/) – dependency Management
* [PostgreSQL](https://www.postgresql.org/) – database
* [Docker](https://www.docker.com/) – used to provide an easy way to deploy
* [JWT Token](https://jwt.io/) – used to authenticate users
* [OAuth2](https://oauth.net/2/) – custom authorization server
* [Mockito](https://site.mockito.org/) –mocking framework for unit tests
* [JUnit](https://junit.org/junit5/) - framework for unit tests

**JS-Proxy**:
* [Node](https://nodejs.org/en/) – JavaScript runtime
* [Npm](https://www.npmjs.com/) – package manager
* [Express](https://expressjs.com/) – main framework used for proxy
* [Docker](https://www.docker.com/) – used to provide an easy way to deploy
* [JWT Token](https://jwt.io/) – used to validate users
* [Redis](https://redis.io/) –database to cache user token for efficiency
* [Jest](https://jestjs.io/) - mocking/unit testing framework

**Deployment**:
* [Google Cloud Platform](https://cloud.google.com/) – cloud services
* [Kubernetes](https://kubernetes.io/) –container orchestration

<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

Things you need to install before running:
```
Java 8
Maven
Postgres
Redis
Docker
```

### Installation
 
1. Clone the repo and change the directory:
```sh
git clone https://github.com/Mouse-BB-Team/Data-Collection.git
cd Data-Collection
```
####API:
1. Change directory to api/ and build a java package:
```
cd api/
mvn clean compile package spring-boot:repackage
```
2. Use docker-compose file to deploy api locally:
```
docker-compose -f docker-compose.yaml up
```
> NOTE: you MUST configure your own env variables in docker-compose.yaml

####JS-Proxy:
1. Change directory to js-proxy/
2. Run docker-compose file to deploy js-proxy:
```
docker-compose -f docker-compose.yaml up
```
> NOTE: you MUST configure your own env variables in docker-compose.yaml to match previously set variables in API

#### Google Cloud Platform deployment:
If you want to deploy the app to GCP, use the yaml configuration files from k8s/ directory. Some additional settings are provided in the appendix at the end of document.

<!-- USAGE EXAMPLES -->
## Usage

From now on you should be able to run the application in your Web browser on localhost:PORT (PORT env configured in docker files). You can signup/login into the site and the data will be collected in the background to the database.


<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.

<!-- CONTACT -->
## Contact

Mail - <a href="mailto:mouse.bb.team@gmail.com">mouse.bb.team@gmail.com</a>

Project Link: [https://github.com/Mouse-BB-Team](https://github.com/Mouse-BB-Team)



<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements

Our thesis supervisor:
* [Piotr Chołda - Website](http://home.agh.edu.pl/~cholda/)


## Authors

* **Kamil Kaliś** – [kamkali](https://github.com/kamkali)
* **Piotr Kuglin** – [lothar1998](https://github.com/lothar1998)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[web-shield]: https://img.shields.io/website?style=flat-square&url=https%3A%2F%2Fwww.mouse-bb.pl
[web-url]: https://mouse-bb.pl


[license-shield]: https://img.shields.io/github/license/Mouse-BB-Team/Data-Collection?style=flat-square
[license-url]: https://github.com/Mouse-BB-Team/Data-Collection/blob/master/LICENSE.txt


## Appendix
### Authorization sequence diagram:
![JWT-obtaining-sequence](https://github.com/Mouse-BB-Team/Data-Collection/blob/master/js-proxy/JWT%20token%20obtaining%20sequence.jpg)

### Kubernetes settings:

##### 1. `dc-api` secret with *env* variables
	DEFAULT_ADMIN_USERNAME: admin
	DEFAULT_ADMIN_PASSWORD: admin
	OAUTH2_CLIENTID: client_id
	OAUTH2_CLIENTSECRET: password
	OAUTH2_RSAPUBLICKEY: ssh-rsa_format
	OAUTH2_RSAPRIVATEKEY: ssh-rsa_format

##### 2. `postgres` secret with *env* variables
	SPRING_DATASOURCE_URL: jdbc:postgresql://url/database
	SPRING_DATASOURCE_USERNAME: admin
	SPRING_DATASOURCE_PASSWORD: admin

##### 3. `static external IP address` named web-static-ip:

	gcloud compute addresses create web-static-ip --global

