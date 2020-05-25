# MicroServiceWithSpringCloud
Création de la partie backend d’un exemple d’application basée sur des micro-service en utilisant Spring cloud

![](ArchitectureGlobale.jpg)

1. Création des micros-service métiers en mode express basés sur JPA, Spring Data, Spring Data Rest, H2 Data base, Open Feign
    - Customer Service
    - Inventory Service
    - Billing Service
	
2. Développement et mise en place du Discovery Service Netflix Eureka Service

3. Développement d'un service proxy orchestration avec Spring Cloud Gateway avec les trois modes de routage :
    - Routage Statique avec Configuration déclarative application.yml
    - Routage statique avec Configuration programmatique (Classe de configuration)
    - Routage Dynamique en s’appuyant sur le service d’enregistrement Eureka Discovery. 
	
4. Utilisation des services de spring cloud 
	- Actuator pour le monitoring et le management des services
	- Hystrix pour Circuit Breaker 
	- Hystrix Dash Board



## Scénario: 

Au démarrage, les services customer, inventory, billing et meme le geteway s'enregistre aupres de eureka service. 
Il renseigne a ce server registry leur nom, leurs adresse IP et leur port.

Le gateway est le point d'entré des requettes externes envoyées par les clients.
Une fois la requette arrivée au niveau du gateaway, il recupere le nom du service qui est invoyé et 
demande a eureka servive de lui donné l'addresse et le port du service. 

Ainsi le gateway ayant l'adresse complete du service peut directement le contacter et lui envoyer la requette du client.
La reponse sera recuperer par ce meme gateway et rencoyer au client.

Remarque : les requettes qui sont envoyées et recu par le gateway peuvent etre soumisent a des validations 

	- Itinéraire : L'élément de base de la passerelle. Il est défini par un ID, un URI de destination, une collection de prédicats
				   et une collection de filtres. Une route est mise en correspondance si le prédicat agrégé est vrai.

	- Prédicat : Il s'agit d'un prédicat de fonction Java 8 . Le type d'entrée est un framework SpringServerWebExchange.
				 Cela vous permet de faire correspondre tout ce qui provient de la requête HTTP, comme les en-têtes ou les paramètres.

	- Filtre : Ce sont des instances de Spring FrameworkGatewayFilter qui ont été construites avec une usine spécifique.
			   Ici, vous pouvez modifier les demandes et les réponses avant ou après l'envoi de la demande en aval.


Les clients font des demandes à Spring Cloud Gateway. Si le mappage du gestionnaire de passerelle détermine qu'une demande
correspond à un itinéraire, elle est envoyée au gestionnaire Web de passerelle. Ce gestionnaire exécute la demande via 
une chaîne de filtres spécifique à la demande. Les filtres peuvent exécuter la logique à la fois avant et après l'envoi de la demande de proxy.
Toute la logique de filtrage «pré» est exécutée ensuite, la demande de procuration est effectuée. 
Une fois la demande de proxy effectuée, la logique de filtrage «post» est exécutée.


## Démarage et test des  services

1.	EUREKA-SERVICE
	- Ouvrez le projet avec votre editeur et demarrer le. Si tout marche correctement, tester l'url suivant 
	- http://localhost:8761/
	
2.	CUSTOMER-SERVICE
	- Meme chose ouvrez le projet sur votre éditeur exemple inteliji et demarrer le service
	  tester l'url suivant pour s'assurer du bon fonctionnement du service 
	- http://localhost:8081/customers
	
3.	INVENTORY-SERVICE
	- ouvrez le projet sur inteliji et demarer le service puis tetster l'url suivant
	- http://localhost:8082/products/
	
4.	BILLING-SERVICE
	- Ouvrez le projet sur inteliji et demarer le service puis tester l'url suivant sur votre navigateur
	- http://localhost:8083/fullBill/1
	
	fullBill est une projection que nous avons creer dans les codes pour afficher les infos du customer et des deatils du product
	
5.	GATEWAY
	- Meme chose pour le projet gateway. Ouvrez le sur inteliji et demarrer le service puis tester l'url
	- http://localhost:8888/INVENTORY-SERVICE/products
	- http://localhost:8888/CUSTOMER-SERVICE/customers
	- http://localhost:8888/BILLING-SERVICE/fullBill/1
	
6.	HYSTRIX-DASHBOARD
	- ouvrez le projet sur inteliji et demarrer le service puis tester l'url suivant 
	- http://localhost:9999/hystrix/
	
7.	RAPID-API
	- Dans le projet nous avons aussi intégrer des API externe comme countries et MuslimSalat tester en faisant quelques modifications
	au niveau de la configuration du service gateway au niveau de la configutation statique des filters des routes.
	au niveau des entetes changer la valeur de rapidapi key et metez votre propre clé apres inscription sur le site de rapidapi.
	
	- http://localhost:8888/publicCountries/region/africa
	
	


Mot clés : Java, JEE, Spring, Spring Cloud, Micro services, Spring Cloud Gateway, Eureka Discovery service, Hystrix, Circuit Breaker

Documentation 

Spring Cloud Gateway : https://cloud.spring.io/spring-cloud-gateway/reference/html/

Video explicative du travail : https://www.youtube.com/watch?v=rIYK0C0ES94&t=2129s

En cas de besoin contacter nous : http://mmdiago.com/