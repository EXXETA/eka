# EKA - Elasticsearch K8s Automator


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

# Build

Das bauen des Dockerimages erfolgt durch Ausführen der
**build.sh** im project/ Unterordner

# Deploy in lokalen Kubernetes

**Vorraussetzungen**
* Elasticsearch läuft im Cluster
* Elasticsearch Credentials und Certificate sind zugreifbar als kubectl secret
* Thiller ist installiert

Installation erfolgt mit ***helm/init_local.sh***

Deinstallation erfolgt mit ***helm/destroy_local.sh***


# Änderungen für Release
* Artefakt umbenennen im Java Projekt
* Dockerimage Tag ändern in build.sh
* Image Tag ändern in helm/java-controller/values.yaml
