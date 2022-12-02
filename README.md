# Context Analyzer
A context analyzer component for service re-selection based on quality of service parameters. This component is part of an architecture for enabling context aware workflow management based on Asset Admininistration Shell.

## Important resources
- The ontology file can be found at [DeviceServiceOnt.owl](/DeviceServiceOnt.owl)
- GraphDB server settings can be changed at [Tools.java](/mgep.ContextAwareAasBpmn/src/main/java/mgep/ContextAwareAasBpmn/Core/Tools.java)

## Compatibility
- GraphDB
- Java 8

## Deployment
- Generate war file from the API project ``mgep.ContextAwareAasBpmn`` by executing ``mvn install``. The war file will be placed in ``C:\apache-tomcat-8.5.61\webapps\`` by default. You can change this path in the pom.xml file under the xml element ``maven-war-plugin/configuration/outputDirectory``.
- Deploy war file into your prefered java web server.

## Test
- A client project is provided including test data ``mgep.DeviceServiceOntClient``. Run this as Java Application to fill some testing data into GraphDB. You can change the data according to your needs by modifying [SynchronizeThisDeviceData.java](/mgep.DeviceServiceOntClient/src/mgep/DeviceServiceOntClient/Main/SynchronizeThisDeviceData.java).
- Once the war file has been deployed, use postman or any similar to consume the REST endpoints.
- Most of the REST endpoints are to retrieve some data from GraphDB. But ``ValidateContextSelectBestService``: Suggests a better service by evaluating the requested serviceName and the provided Quality of Service (QoS) parameters.
    - Request body:
    ``` 
    {
        "aasIdentifier": "AssetAdministrationShell---1",
        "serviceName": "Service_ThrowPiece",
        "qualityParameters": [
            {
                "name": "AvgNetworkLatency",
                "evaluationExpression": "AvgNetworkLatency <= 300"
            },
            {
                "name": "SuccessRate",
                "evaluationExpression": "SuccessRate >= 75"
            }
        ]
    } 
    ```
    - Response body:
    ```
    {
        "canExecute": false,
        "message": "A better service is recomended after evaluation of quality of service parameters",
        "suggestedService": {
            "aasIdentifier": "AssetAdministrationShell---2",
            "url": "http://192.168.56.102:80/robot/throw_piece",
            "method": "POST",
            "name": "Service_ThrowPiece",
            "serviceIdentifier": "313295db-3f90-4a3a-9ef1-5767a39fb0df",
            "description": "Throws current piece out of the feed tray",
            "requestBody": null,
            "responseBody": null,
            "inputParameters": [
                {
                    "name": "message_piece_thrown",
                    "type": "json",
                    "value": "{  'message': 'piece thrown',  'next_color': 'Yellow',  'thrown_color': 'Blue'}"
                }
            ],
            "outputParameters": [],
            "qualityParameters": [
                {
                    "name": "SuccessRate",
                    "correspondsTo": "SERVICE",
                    "dataType": "Decimal",
                    "value": "122",
                    "evaluationExpression": "SuccessRate >= 80"
                },
                {
                    "name": "AvgNetworkLatency",
                    "correspondsTo": "DEVICE",
                    "dataType": "Integer",
                    "value": "210",
                    "evaluationExpression": "NetworkLatency <= 300"
                },
                {
                    "name": "LastNetworkLatency",
                    "correspondsTo": "DEVICE",
                    "dataType": "Integer",
                    "value": "108",
                    "evaluationExpression": "LastNetworkLatency <= 300"
                },
                {
                    "name": "AvgResponseTime",
                    "correspondsTo": "SERVICE",
                    "dataType": "Integer",
                    "value": "7768",
                    "evaluationExpression": "AvgResponseTime <= 1000"
                },
                {
                    "name": "LastResponseTime",
                    "correspondsTo": "SERVICE",
                    "dataType": "Integer",
                    "value": "4373",
                    "evaluationExpression": "LastResponseTime <= 1000"
                },
                {
                    "name": "Humidity",
                    "correspondsTo": "DEVICE",
                    "dataType": "Decimal",
                    "value": "60",
                    "evaluationExpression": "Humidity <= 50"
                },
                {
                    "name": "Temperature",
                    "correspondsTo": "DEVICE",
                    "dataType": "Decimal",
                    "value": "32",
                    "evaluationExpression": "Temperature <= 30"
                },
                {
                    "name": "Weight",
                    "correspondsTo": "DEVICE",
                    "dataType": "Decimal",
                    "value": "5",
                    "evaluationExpression": "Weight <= 10"
                },
                {
                    "name": "Size",
                    "correspondsTo": "DEVICE",
                    "dataType": "Decimal",
                    "value": "11",
                    "evaluationExpression": "Size <= 10"
                },
                {
                    "name": "BatteryLevel",
                    "correspondsTo": "DEVICE",
                    "dataType": "Decimal",
                    "value": "91",
                    "evaluationExpression": "BaterryLevel >= 65"
                },
                {
                    "name": "DistanceAwayFromWorkStation",
                    "correspondsTo": "DEVICE",
                    "dataType": "Decimal",
                    "value": "73",
                    "evaluationExpression": "DistanceAwayFromWorkStation <= 70"
                }
            ],
            "async": true
        }
    }
    ```

## Licence
Apache-2.0 license
