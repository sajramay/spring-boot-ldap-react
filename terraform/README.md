# Terraform

## Table of Contents

- [Terraform Basics](#terraform-basics)
  * [Variables](#variables)
  * [Locals](#locals)
  * [Outputs](#outputs)
  * [Data Sources](#data-sources)
  * [Provisioners](#provisioners)
  * [Terraform Workspaces](#terraform-workspaces)
  * [Modules](#modules)

## Terraform Basics

There are numbered subdirectories that are increasingly complex and demonstrate how to use Terraform for deploy this
Spring Boot application.

* 1_terraform_simple : very simple deployment of a single ec2 instance
* 2_terraform_elb : a more advanced deployment with a database and an elb

### Variables
Variables are declared in `variables.tf` and can be defined on the command line to in a file called `terraform.tfvars` in the same directory.  Alternatively pass them on the command line using the `-var` parameter to terraform.

```hcl 
$ terraform apply -var-file myvars.tfvars
```

### Locals
Locals are variables that are defined within a `.tf` file. They can be used, for example, to set the `Name` tag for AWS resources.

```hcl
locals {
    appname = "Spring Boot LDAP Demo"
}

# Use with local.appname
```

### Outputs
Outputs allow developers to print specific pieces of information to the command line after the terraform utility is run. This is useful, for example, to get the ips that have been assigned to your EC2 instances.  This can be a string or an attribute of any resource.  Set `sensitive=true` if you want the output saved to the state file but not to the console.

```hcl
output "sample_string" {
    value = "testone"
}

output "instance_ip" {
    value = aws_instance.spring_boot.public_ip
}
```

### Data Sources
Data sources allow developers to grab data from existing resources that are available in the cloud provider.  This may be something like the VPC id of the default VPC that exists in your Amazon account

```hcl
provider "aws" {
    version = "~> 2.65"
    region = "eu-west-2
}

data "aws_vpc" "myvpc" {
    # you can, for example, pick the default VPC
    default = true
    # OR use a filter
    filter {
        name = "tag:Name"
        value = ["Spring Boot LDAP Demo VPC"]
    } 
}

output "myvpc" {
    value = data.aws_vpc.myvpc
}

```

You can also fetch an existing AMI for example
```hcl
data "aws_ami" "my_ami" {
    owners = ["self"]
    most_recent = true
}
```

### Provisioners
Similar to Amazon user data they allow you to make final configuration changes to your resources.  They are a last-resort option and are not normally needed.  The file provisioner can be used to write files to the newly created instance. Note that we will use Packer to create a custom AMI in the advanced

```hcl
resource "aws_instance" "spring-ldap" {
    ami = "some ami id from amazon"
    instance_type = "t2.micro"
    tags {
        Name = local.appname
    }
    connection {
        type = "ssh"
        host = self.public_ip
        user = "ec2-user"
        private_key = file("/home/user/key.pem")
    }
    provisioner "file" {
        source = "../target/spring-boot-react.jar"
        destination = "/spring-boot-react.jar"
    }
}
```

Remote exec allows us to run a command on the remote machine.

```hcl
resource "aws_instance" "spring-ldap" {
    ami = "some ami id from amazon"
    instance_type = "t2.micro"
    tags {
        Name = local.appname
    }
    connection {
        type = "ssh"
        host = self.public_ip
        user = "ec2-user"
        private_key = file("/home/user/key.pem")
    }
    provisioner "remote-exec" {
        inline = [
            "touch /var/tmp/test.txt"
        ]
    }
}
```

You can use `when = "destroy"` so that the provisioner is only run on teardown of the resource

### Terraform Workspaces
Workspace allow you to store state for a single Terraform configuration file in separate areas. For example, you may want separate state for dev and prod such that you can test any changes in a dev environment.

Show workspaces:
```hcl
$ terraform workspace list
* default
```
```hcl
$ terraform workspace new prod
  default
* prod
```
```hcl
$ terraform workspace select dev
```

We can refer to the workspace name in Terraform by say assigning a local and using that in the configuration
```hcl
locals {
    instance_name = "${terraform.workspace}-instance"
}

resource "aws_instance" "webserver" {
    tags = {
        Name = local.instance_name
    }
}
```

Variables can be declared in a `variables.tf` file and defined in environment specific tf files such as `dev.tfvars` and `prod.tfvars` the use the file as follows:
```hcl 
$ terraform apply -var-file dev.tfvars
```

### Modules

Modules allow you to group multiple resources together that can be called from a different configuration, passing in the required parameters that are needed.  In Terraform, everything has a parent module.  There is always a root module and there are child modules as well.  You can publish modules to the Terraform registry or just use them locally.

A sample configuration is something like the following:
```
- setup
  - modules
    - webserver
      main.tf
      variables.tf
      output.tf
      README.md
    - 
```

The `main.tf` has something like the following:
```hcl
terraform {
    required_version = ">= 0.12"
}

resource "aws_subnet" "webserver" {
    vpc_id     = var.vpc_id
    cidr_block = var.cidr_block
}

resource "aws_instance" "webserver" {
    ami           = var.ami
    instance_type = var.instance_type
    subnet_id     = aws_subnet.webserver_id

    tags {
        Name = "${var.webserver_name} webserver"
    }
}
```

The `variables.tf` file will then declare the variables that were used in the `main.tf`
```hcl 
variable "vpc_id" {
    type        = string
    description = "VPC id"
}

variable "cidr_block" {
    type        = string
    description = "Subnet cidr block"
}

variable "webserver_name" {
    type        = string
    description = "Name of webserver"
}

variable "ami" {
    type = string
    description = "AMI to use on the webserver instance"
}

variable "instance_type" {
    type = string
    description = "Instance type"
}
```

Outputs are used to share the results of execution of a module so that they can be used within the rest of the Terraform configuration
```hcl
output "webserver" {
    value       = aws_instance.webserver
    description = "Webserver contents"
}
```

This module can be invoked as follows, from say the setup directory.  The variables are supplied when the module is imported.

```hcl
provider "aws" {
    region = "eu-west-2"
}

resource "aws_vpc" "main" {
    cidr_block = "10.0.0.0/16"
}

module "spring_ldap_react_webserver" {
    source         = "../modules/webserver"
    vpc_id         = aws_vpc.main.id
    cidr_block     = aws_vpc.main.cidr_block
    webserver_name = "spring_ldap"
    ami            = "ami-99999999999"
    instance_type  = "t2.large"
}

resource "aws_elb" {
    instances = module.spring_ldap_react_webserver.webserver.id
}
```

You can also import modules from within modules but this is not recommended.  Also, do not use modules as simple wrappers for single resources.
