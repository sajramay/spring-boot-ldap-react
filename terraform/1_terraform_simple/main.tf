# define provider with region set in variables
provider "aws" {
  version = "~> 2.65"
  region  = var.region
}

# define local variables
locals {
  appname = "Spring Boot LDAP Demo"
}

data "aws_subnet" "spring_boot_ldap_subnet" {
  availability_zone = "eu-west-2a"
}

data "aws_vpc" "default_vpc" {
  default = true
}

# use data to get the latest amazon linux 2 AMI
data "aws_ami" "amazon_linux_2" {
  owners      = ["amazon"]
  most_recent = true

  filter {
    name   = "owner-alias"
    values = ["amazon"]
  }
  filter {
    name   = "architecture"
    values = ["x86_64"]
  }
  filter {
    name   = "description"
    values = ["Amazon Linux 2 AMI 2.0*"]
  }
}

resource "aws_security_group" "allow_http" {
  name   = "allow_http"
  vpc_id = data.aws_vpc.default_vpc.id

  ingress {
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # this is needed for the provisioner step below
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# create the EC2 instance
resource "aws_instance" "spring_boot" {
  ami                    = data.aws_ami.amazon_linux_2.id
  instance_type          = var.instance_type
  vpc_security_group_ids = [aws_security_group.allow_http.id]
  subnet_id              = data.aws_subnet.spring_boot_ldap_subnet.id
  key_name               = "spring_ldap_key_pair"
  user_data = file("userdata.sh")
  tags = {
    Name = local.appname
  }
  connection {
    type        = "ssh"
    host        = self.public_ip
    user        = "ec2-user"
    private_key = file("/xxx/spring_ldap_key_pair.pem")
  }
  # upload artifact using the provisioner
  provisioner "file" {
    source      = "../../target/spring-boot-ldap.jar"
    destination = "/home/ec2-user/spring-boot-ldap.jar"
  }
}
