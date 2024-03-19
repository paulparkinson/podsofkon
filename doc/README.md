# Pods Of Kon Doc

This is the main documentation page.
From here you can go to pages on each of the topics below which will explain how the tech is used in the game and related material.

## Requirements and Building
[Requirements And Setup For Game](https://github.com/paulparkinson/podsofkon/blob/main/doc/requirementsandsetup.md)
[Building Or Downloading Game](https://github.com/paulparkinson/podsofkon/blob/main/doc/build.md)



## Overall Flow Of Game

•	Unity front end calls into Oracle Database and Pods of Kon Kubernetes microservice running in OKE

•	Database has been provision and maintained by Oracle Database Operator (OraOpertor)

•	Pods Of Kon microservice sends requests to “GrabDish” microservice application which uses

•	Database access in multiple data types (Relational and JSON (JSON Duality), Spatial, Graph, ...)

•	TxEventQ/Kafka event-driven microservice patterns: Transactional Outbox, CQRS, Event Sourcing, and Saga

•	Database serves as overall content management system for all media types in the game but also…

•	Spatial in Oracle Database uses geometry on 3d models to resize, etc. and streams them to frontend

•	Oracle Unified OpenTelemetry Observability for end-to-end metrics, logs, and tracing.

•	Oracle Database makes calls out to other AI services for content generation.


## Topics
[Multi-language support, Microservices, and Kubernetes](https://github.com/paulparkinson/podsofkon/blob/main/doc/microservices.md)

[AI](https://github.com/paulparkinson/podsofkon/blob/main/doc/ai.md)

[Messaging, Event-Driven Patterns with TxEventQ (formerly AQ) and Kafka](https://github.com/paulparkinson/podsofkon/blob/main/doc/messaging.md)

["DevOps to DataOps" Oracle End to End OpenTelemetry Solution](https://github.com/paulparkinson/podsofkon/blob/main/doc/observability.md)

[Sagas](https://github.com/paulparkinson/podsofkon/blob/main/doc/sagas.md)

[Spatial](https://github.com/paulparkinson/podsofkon/blob/main/doc/spatial.md)

[TrueCache Multiplayer Online and Mobile](https://github.com/paulparkinson/podsofkon/blob/main/doc/multiplayerandmobile.md)

[XR](https://github.com/paulparkinson/podsofkon/blob/main/doc/xr.md)



## YouTube
[Pods Of Kon YouTube channel/playlist] (https://www.youtube.com/playlist?list=PLc-GvTDCJw-KdepJm_BOE31jRrw6her2d)


## Blogs

https://medium.com/@paul-parkinson
https://dzone.com/users/4571557/paulparkinson.html
https://hackernoon.com/u/paulparkinson
https://blogs.oracle.com/authors/paulparkinson
https://dev.to/paulparkinson