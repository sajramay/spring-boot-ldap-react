output "the_ami_to_be_used" {
  value = data.aws_ami.amazon_linux_2
}

output "instance_ip" {
  value = aws_instance.spring_boot.public_ip
}
