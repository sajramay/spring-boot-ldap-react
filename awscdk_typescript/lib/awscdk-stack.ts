import * as cdk from '@aws-cdk/core';
import * as ec2 from '@aws-cdk/aws-ec2';
import { countResources } from '@aws-cdk/assert';
import { CfnOutput } from '@aws-cdk/core';
import { MachineImage, Port } from '@aws-cdk/aws-ec2';

export class AwscdkStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // The code that defines your stack goes here
    const vpc = ec2.Vpc.fromLookup(this, 'myvpc', {
      isDefault: true,
    })

    const ami = ec2.MachineImage.latestAmazonLinux({
      cpuType : ec2.AmazonLinuxCpuType.X86_64,
      generation: ec2.AmazonLinuxGeneration.AMAZON_LINUX_2,
      virtualization: ec2.AmazonLinuxVirt.HVM,
      storage: ec2.AmazonLinuxStorage.GENERAL_PURPOSE,
      edition: ec2.AmazonLinuxEdition.STANDARD
    })

    const allow_http_sg = new ec2.SecurityGroup(this, 'allow_http_sg', {
      vpc: vpc,
      allowAllOutbound: true
    })
    allow_http_sg.connections.allowFromAnyIpv4(Port.tcp(22))
    allow_http_sg.connections.allowFromAnyIpv4(Port.tcp(9090))

    const t2_micro = new ec2.InstanceType('t2.micro')

    const userData = ec2.UserData.forLinux()
    userData.addCommands('sudo amazon-linux-extras install java-openjdk11')

    const ec2_instance = new ec2.Instance(this, 'spring_boot', {
      vpc: vpc,
      machineImage: ami,
      instanceType: t2_micro,
      securityGroup: allow_http_sg,
      keyName: 'spring_ldap_key_pair',
      userData: userData
    })

    new CfnOutput(this, 'ec2_public_ip', {
      value:ec2_instance.instancePublicIp
    })
  }
}
