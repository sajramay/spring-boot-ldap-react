variable "region" {
  type        = string
  description = "AWS Region (Europe (London))"
}

# variables require
variable "profile" {
  type        = string
  description = "AWS credentials profile you want to use (default)"
}

variable "instance_type" {
  type = string
}