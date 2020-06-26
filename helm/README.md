# Deploy

**Wichtig** 
* Tiller muss auf dem Cluster installiert sein, damit Helm funktioniert
* die values.yaml **muss** mitgegeben werden - Sie enthält die Default-Konfiguration


Installation: ``helm install -n java-k8s-operator --values ./values.yaml . ``

Deinstallation: ``helm delete --purge java-k8s-operator``

# Konfiguration

Die Anwendung wird durch eine **ConfigMap** konfiguriert,
die beim Deploy als *resource* erstellt wird. Sie befindet sich unter
*templates/configmap.yaml*. 

Es können folgende Sachen konfiguriert werden durch die **values.yaml**
```yaml  
# Configmap configuration

# pod-name of elasticsearch master
ELASTICSEARCH_MASTER_HOSTNAME: 'elasticsearch-master'
# port to use for api calls to elasticsearch master
ELASTICSEARCH_MASTER_PORT: '9200'
# should an ilm policy be overridden if one
# with an identical name already exists? 'true' | 'false'
ILM_POLICY_FORCE_UPDATE: 'false'
# 'true'|'false'
# determines whether or not jobs execute
DRY_RUN: 'false'
# time to sleep on job failure | in ms
SLEEP_TIMER: '3000'

FILEBEAT_VERSION: 7.5.0

METRICBEAT_VERSION: 7.6.1

NAMESPACE_EXEMPTIONS:
    - "default"
    - "docker"
    - "kube-node-lease"
    - "kube-public"
    - "kube-system"

INDEX:
  SETTINGS:
    NUMBER_OF_SHARDS: 1
    NUMBER_OF_REPLICAS: 1

```
