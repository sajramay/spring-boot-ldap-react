# Terraform for Spring Boot LDAP Demo

This terraform plan will create a simple configuration that used the default VPC and a single subnet 
with all resources in that subnet.  This subnet is public facing so that clients can access the 
spring boot server.

This deployment uses a provisioner which is not a recommended deployment approach but suffices for this simple example.

NOTE : You will need to edit the main.tf file to point to the location of your PEM file for the keypair used to create
the Spring Boot instance.