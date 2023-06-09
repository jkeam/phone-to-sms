# Phone to SMS

This project answers a phone call with custom text and then sends them a follow up SMS custom message.
Update the values for twilio sid and twilio auth token or set environment variables that will be replaced
in `application.properties`.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/phone-to-sms-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Deployment

1. Create namespace `kubectl apply -f ./kube/base/namespace.yaml`
2. Create `./kube/overlays/prod/secret_keys.yaml` with the keys to match the env vars in `./kube/base/deployment.yaml`
3. Add `image` info to `./kube/base/kustomization.yaml`
4. Update `./kube/base/ingress.yaml` to use the right alb arn and the right hostname and tls hostname
5. `kubectl apply -k ./kube/overlays/prod` to deploy application
