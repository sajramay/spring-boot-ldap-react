import * as cdk from '@aws-cdk/core';
import * as ec2 from '@aws-cdk/aws-ec2';
import * as asg from '@aws-cdk/aws-autoscaling';
import * as elb from '@aws-cdk/aws-elasticloadbalancingv2'
import * as r53 from '@aws-cdk/aws-route53'
import * as tgt from '@aws-cdk/aws-route53-targets'

export class AwscdkStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // VPC
    const vpc = ec2.Vpc.fromLookup(this, 'myvpc', {
      isDefault: true,
    })

    // AMI
    const ami = ec2.MachineImage.latestAmazonLinux({
      cpuType :       ec2.AmazonLinuxCpuType.X86_64,
      generation:     ec2.AmazonLinuxGeneration.AMAZON_LINUX_2,
      virtualization: ec2.AmazonLinuxVirt.HVM,
      storage:        ec2.AmazonLinuxStorage.GENERAL_PURPOSE,
      edition:        ec2.AmazonLinuxEdition.STANDARD
    })

    const awsTags = [
      {key:'Name', value:'Spring Boot Ldap React'},
      {key:'Env',  value:'Development'},
    ]

    // Security Group - front end app servers
    const frontSg = new ec2.SecurityGroup(this, 'Frontend SG', {
      vpc,
      securityGroupName: 'FrontEnd SG',
      allowAllOutbound: true
    })
    frontSg.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(8080))
    frontSg.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(22))

    // Security Group - ALB
    const elbSg = new ec2.SecurityGroup(this, 'ELB SG', {
      vpc,
      securityGroupName: 'ELB SG',
      allowAllOutbound: false
    })
    elbSg.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(443))
    elbSg.addEgressRule(frontSg, ec2.Port.tcp(8080))

    // Security Group - RDS
    const rdsSg = new ec2.SecurityGroup(this, 'RDS SG', {
      vpc,
      securityGroupName: 'RDS SG',
      allowAllOutbound: false
    })
    rdsSg.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(5432))

    // User data for Auto Scaling Group
    const user_data = ec2.UserData.forLinux()
    user_data.addCommands('sudo yum install psql')
    user_data.addCommands('sudo amazon-linux-extras install java-openjdk11')

    // Auto Scaling Group
    const autoScalingGroup = new asg.AutoScalingGroup(this, 'ASG', {
      vpc,
      instanceType: ec2.InstanceType.of(ec2.InstanceClass.T2, ec2.InstanceSize.MICRO),
      machineImage: ami,
      minCapacity: 1,
      maxCapacity: 2,
      allowAllOutbound: false,
      userData: user_data,
      keyName: 'spring_ldap_key_pair'
    })
    autoScalingGroup.addSecurityGroup(frontSg)

    // Application Load Balancer
    const alb = new elb.ApplicationLoadBalancer(this, 'ALB', {
      vpc,
      internetFacing: true,
      http2Enabled: true,
      securityGroup: elbSg
    })

    const alb_target_group = new elb.ApplicationTargetGroup(this, 'target group', {
      vpc,
      port: 8080,
      protocol: elb.ApplicationProtocol.HTTP,
      targets: [autoScalingGroup],
      //healthCheck: {
      //  enabled: true,
      //  path: '/healthcheck',
      //  protocol: elb.Protocol.HTTP,
      //  healthyHttpCodes: '200'
      //}
    })

    const alb_listener = new elb.ApplicationListener(this, 'ALB Listener', {
      protocol: elb.ApplicationProtocol.HTTP,
      loadBalancer: alb,
      defaultTargetGroups: [alb_target_group]
    })

    // Route 53 zone and DNS entry
    const hosted_zone = new r53.HostedZone(this, 'HostedZone', {
      zoneName: 'Spring_Boot_LDAP_Zone',
      vpcs: [vpc]
    })

    const a_record = new r53.ARecord(this, 'ALB A Record', {
      recordName: 'springreactldap',
      zone: hosted_zone,
      target: r53.RecordTarget.fromAlias(new tgt.LoadBalancerTarget(alb))
    })
  }
}
