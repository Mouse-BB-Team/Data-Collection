# Data-Collection

## Requirements:

### API:

#### Kubernetes:

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