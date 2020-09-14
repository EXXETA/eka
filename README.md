# EKA - Elasticsearch K8s Automator

https://jira.exxeta.com/browse/SWEMTS-48549

Achtung: Noch nicht produktiv einsetzbar


Ziel ist es den Rollout von Elasticsearch Indices für Filebeat und
Metricbeat zu automatisieren.
Indices sollen in Abhängigkeit von Namespaces im Kubernetes Cluster
erstellt werden.



# Funktionsweise

Die Anwendung kann in einem Cluster in dem ElasticSearch
läuft deployed werden. Eine Teilkomponente ist ein *listener* für
**Kubernetes Namespace Events**. In Abhängigkeit dieser Events
werden:
* Indices durch die Elasticsearch Api erstellt.
* IndexTemplates für jeden Index erstellt.

IndexTemplates versehen den Index mit einem Rolloveralias
und der dazu passenden ILM Policy

# Deploy in lokalen Kubernetes

**Vorraussetzungen**
* Elasticsearch läuft im Cluster
* Elasticsearch Credentials und Certificate sind zugreifbar als kubectl secret
* Thiller ist installiert

1. Herunterladen des Image ```$ docker pull docker.pkg.github.com/exxeta/eka/eka:latest```
2. Exxeta Repo zu Helm hinzufügen ```$ helm repo add exxeta https://exxeta.github.io/eka/```
3. Helm chart ausführen ```$ helm install -n eka exxeta/eka```



# Änderungen für Release
* Artefakt umbenennen im Java Projekt
* Dockerimage Tag ändern in build.sh
* Image Tag ändern in helm/java-controller/values.yaml
