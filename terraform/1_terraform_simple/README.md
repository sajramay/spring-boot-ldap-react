# Terraform Simple Deployment
This terraform plan will create a simple configuration that includes a single
VPC and a single subnet will all resources in that subnet.  This subnet is public facing so that clients can access the 
spring boot server.

## Terraform Basics

### Variables
Variables are declared in `variables.tf` and can be defined on the command line to in a file called `terraform.tfvars` in the same directory.  Alternatively pass them on the command line using the `-var` parameter to terraform.

### Locals
Locals are variables that are defined within a `.tf` file. We do not use them apart from to set the `Name` tag for AWS resources.

```hcl
locals {
    appname = "Spring Boot LDAP Demo"
}

# Use with ${local.appname}
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

# Terraform for Spring Boot LDAP Demo

## VPC
We define a VPC to and split that across two AWS Availability Zones but splitting that VPC into two subnets.
```tf
resource "aws_vpc" "main" {
  cidr_block = var.vpc_cidr_block
}

resource "aws_subnet" "main" {
  vpc_id = aws_vpc.main.id
  cidr_block = var.subnet_cidr_block
  map_public_ip_on_launch = true
  tags = {
    Name = "spring-boot-subnet"
  }
}
```