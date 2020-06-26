# Dokumentation

## Build

Die Anwendung kann manuell mit dem build.sh Skript gebaut werden.

## Überblick Programmablauf


1. Laden der Konfiguration aus Umgebungsvariablen des Pods (Diese werden in der configmap.yaml definiert)
2. Client für Elastic und K8s erstellen
3. Startup Job ausführen(Ilm Policy Anlegen, Fallbackindex erstellen ..)
4. Informer für Namespace Events starten
5. Für added Namespace Events werden Indices und Templates für jeden Beat angelegt


## Beats

Beats sind externe Anwendungen, die die erstellten Indices benötigen,
um Logs oder ähnliches an Elastic zu senden. Typische Beispiele sind **Metricbeat** oder 
**Filebeat**

Sie werden in der Configmap wie folgt definiert:
```yaml
{
    "beatName":"filebeat",
    "indexSuffix":"logs",
    "version": 7.5.1,
    "fallbackIndexName":"filebeat",
    "ilmPolicy":{
      "name":"logs-policy",
      "requestBody":"PUT_ILM_POLICY_LOGS_REQUEST_BODY"
  }
}
```
Dabei sind *indexSuffix* und *version* Teil der 
Namenskonvention der erstellten Indices. Die *ilmPolicy*
bestimmt welche ILM Policy für die Indicies, die für
den jeweiligen Beat und Namespace erstellt werden, zugewiesen wird.

## Konfiguration von ILM Policy Index Settings und Index Template Settings

Das Erstellen von Policies, Indices, Templates erfolgt über Rest-Endpunkte,
wobei der Requestbody die Informationen über Settings enthält.
In der configmap sind die dazugehörigen Templates hinterlegt. Diese können 
durch die values.yaml konfiguriert werden.

In den Templates selber sind manchmal Platzhalter der Form $* zu finden.
Diese werden zur Laufzeit durch das Programm ersetzt.

## Kubernetes Controller & Namespace Events

Für das Überwachen der Kubernetes Ressource *namespace* 
wird ein Informer genutzt. Bei Veränderungen an der
Ressource *namespace* werden Events erzeugt, auf 
die der Informer reagieren kann.

Es gibt folgende Events:
* ADDED
* MODIFIED
* DELETED

Wobei jedoch nur Added Events verarbeitet werden und zu einer Reaktion führen

## Jobs und Tasks

Mithilfe von *Jobs* und *Tasks* werden Baumstrukturen erstellt werden.

**Tasks** bilden die Blätter des Baumes und sind konkrete Api Aufrufe
an ElasticSearch.

**Jobs** bilden die Knoten des Baumes bilden, beinhalten
die Logik zur Auswertung und Ausführungsreihenfolge der einzelnen Tasks.

### Tasks

Jeder Api Aufruf gilt als einzelner Task. Tasks sind abgeschlossen, sobald
man **eine** Antwort von ElasticSearch bekommen hat. Für die Auswertung dieser 
Antworten sind Jobs zuständig.

### Parallel | Sequential Job

Diese beiden Jobarten bilden die Grundlage der Baumstruktur. Sie bestimmen,
ob die Jobs, die sie umfassen parallel oder sequentiell ausgeführt werden können.

### Root Job 
Root Jobs bilden die Wurzel des Baumes. Nur RootJobs sollten explizit ausführt 
werden. RootJobs haben ein Unterjob. Dieser sollte ein Paralleler oder 
sequentieller Job sein. Root Jobs bauen zusätzlich einen Json Job Tree 
und schreiben ihn eine Datei unter 
**"/usr/share/efk-controller-logs/logs.json"**

### Custom Jobs
Manchmal reicht die parallele oder sequentielle Verknüpfung von Tasks oder Jobs 
nicht aus, um ein Vorgang abzubilden, der sich aufgrund der Antworten eines
Tasks unterschiedlich verhält. Dafür können eigene Jobs erstellt werden.
Es ist zu vermerken, dass diese Jobs immer in einem Sequential | Parallel Job 
gewrappt werden sollten.

### Beispiel
(RootJob):
- (SequentialJob) StartupElasticService:
    1. (Task) PostStartIlmTask
    2. (ParallelJob) create Ilm Policy for each policy defined in Beats:
        - (CustomJob) CreateIlmPolicyJob for foo:
            - (Task) GetIlmPolicyTask
            - (Task) PutIlmPolicyTask
        - (CustomJob) CreateIlmPolicyJob for bar:
            - (Task) GetIlmPolicyTask
            - (Task) ...
        - (CustomJob) CreateIlmPolicyJob for ...

 

## Logging

Das Logging wird über Logback realisiert und benutzt ein
Logback-logstash-encoder, der die Lognachrichten in Json formatiert.
Konfigurationsmöglichkeiten sind aktuell sehr eingeschränkt. Jedoch gibt es 
die Möglichkeit das Log level durch eine java system property zu setzen.
Das kann durch Anpassen des Ausführungsbefehls realisiert werden.


## Elasticsearch Client 

Der Elasticsearch Client benötigt folgende Informationen
* Credentials
* Certificate
* Elasticsearch Master Hostname / Port

**Credentials** werden als Umgebungsvariable aus einem 
`kubectl secret` gemountet

deployment.yaml
```yaml
  containers:
    - name: {{ .Chart.Name }}
      env:
        - name: 'ELASTICSEARCH_USERNAME'
          valueFrom:
            secretKeyRef:
                name: elastic-credentials
                key: username
        - name: 'ELASTICSEARCH_PASSWORD'
          valueFrom:
            secretKeyRef:
              name: elastic-credentials
              key: password
```
**Elasticsearch Master Hostname / Port** werden ebenfalls
als Umgebungsvariable gemountet. Sie können in der **values.yaml**
angepasst werden.


```yaml
  # pod-name of elasticsearch master
  ELASTICSEARCH_MASTER_HOSTNAME: 'elasticsearch-master'
  # port to use for api calls to elasticsearch master
  ELASTICSEARCH_MASTER_PORT: '9200'
```

## Kubernetes Client

Da die Anwendung innerhalb des Clusters deployed wird,
wird der Kubernetes Client automatisch konfiguriert durch
`ApiClient client = ClientBuilder.cluster().build();`


# SSL Verification Elasticsearch
Da wir Elasticsearch im Security Modus betreiben, benötigen wir ein Zertifikat,
 um eine sichere Verbindung herzustellen. 

Aktuell erstellen wir dafür ein selbst signiertes Zertifikat 
beim Deployskript von Elasticsearch.
Damit der http-client in **java** dieses Zertifikat nutzen
kann, muss es einem **Java Key Store** hinzugefügt werden,
genauer einem Truststore. Für die Generierung dieses Truststores
ist ein **Init-Container** zuständig.


## Funktion im Detail
Wurde Elasticsearch nach Anleitung, wie [hier](https://gitlab.lej.eis.network/exxeta.platform.suite/base/eps.logging.k8s/tree/master/elasticsearch)
beschrieben, installiert, so ist das Zertifikat als **elastic-certificate-pem**
Secret vorhanden.
```
$ kubectl get secret
NAME                                        TYPE                                  DATA   AGE
default-token-4j2fw                         kubernetes.io/service-account-token   3      3h14m
elastic-certificate-pem                     Opaque                                1      3h12m
elastic-certificates                        Opaque                                1      3h12m
elastic-credentials                         Opaque                                2      3h12m
```
Dieses Secret wird nun als Volume in den Init-Container gemountet. 
Außerdem mounten wir *truststone*. In *truststore* wird letztlich
der erzeugte Truststone gespeichert.
```yaml
          volumeMounts:
            - name: elastic-cert
              mountPath: /tmp/elastic-cert
              readOnly: true
            - name: truststore
              mountPath: /tmp/truststor
```
Innerhalb des Init-Containers läuft dann ein Shell Skript, dass
den Truststore erzeugt und das Zertifikat hinzufügt.
```bash
#!/bin/bash
echo Creating Truststore
echo Convert .pem to .der
            
cert_in=/tmp/elastic-cert/elastic-certificate.pem
cert_out=/tmp/truststore/elastic-certificate.der
            
openssl x509 -outform der -in $cert_in -out $cert_out
yes 'password' | keytool -import -file $cert_out -noprompt -alias ElasticCert -keystore /tmp/truststore/myTrustStore.jks
rm $cert_out
exit 0
```
Damit **beendet** der Init-Container und der eigentliche Container **startet**.
Dabei mountet sich wieder das *truststore* Volume
```yaml
          volumeMounts:
            - name: truststore
              mountPath: /usr/share/truststore
              readOnly: true
```
Abschließend muss der JVM noch mitgeteilt werden, wo sich der Truststore befindet.
```yaml
          command: ["java", "-jar", "-Djavax.net.ssl.trustStore=usr/share/truststore/myTrustStore.jks", "deployments/app.jar"]
```
